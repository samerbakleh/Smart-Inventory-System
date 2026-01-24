//package com.inventory.app.repository;
package com.inventory.app.repository;
import com.inventory.app.model.enums.Unit;

import com.inventory.app.model.Category;
import com.inventory.app.model.Item;
import com.inventory.app.util.AppConstants;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;

public class ItemRepository {

    public static class RepositoryException extends RuntimeException {
        public RepositoryException(String message) { super(message); }
        public RepositoryException(String message, Throwable cause) { super(message, cause); }
    }

    private static final String HEADER = "id,name,price,qty,minLimit,unit,categoryId";
    private static final int MIN_COLS_OLD = 6;
    private static final int COLS_NEW = 7;


    private final CategoryRepository categoryRepository;
    private final Path dataDir;
    private final Path itemsFile;
    private final char delimiter;

    public ItemRepository(CategoryRepository categoryRepository) {
        this.categoryRepository = Objects.requireNonNull(categoryRepository);
        this.dataDir = Paths.get(AppConstants.DATA_DIR);
        this.itemsFile = Paths.get(AppConstants.ITEMS_FILE);
        this.delimiter = AppConstants.CSV_DELIMITER.charAt(0);
    }

    private void ensureDataDir() {
        try {
            Files.createDirectories(dataDir);
        } catch (IOException e) {
            throw new RepositoryException("Cannot create data directory: " + dataDir, e);
        }
    }

    public List<Item> loadAll() {
        Map<Integer, Category> categoriesById = categoryRepository.loadById();
        return loadAll(categoriesById, false);
    }


    public List<Item> loadAll(Map<Integer, Category> categoriesById) {
        return loadAll(categoriesById, false);
    }


    public List<Item> loadAll(Map<Integer, Category> categoriesById, boolean strict) {
        ensureDataDir();

        List<Item> items = new ArrayList<>();
        if (!Files.exists(itemsFile)) return items;

        try (BufferedReader br = Files.newBufferedReader(itemsFile, StandardCharsets.UTF_8)) {
            String line;
            boolean headerChecked = false;

            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;

                if (!headerChecked) {
                    headerChecked = true;
                    String lower = line.toLowerCase(Locale.ROOT);
                    if (lower.startsWith("id" + delimiter)) continue;
                }

                try {
                    List<String> parts = Csv.parseLine(line, delimiter);

                    if (parts.size() < MIN_COLS_OLD) {
                        if (strict) throw new RepositoryException("Corrupted line (missing columns): " + line);
                        continue;
                    }

                    int id = parseInt(parts.get(0), "id");
                    String name = parts.get(1).trim();
                    double price = parseDouble(parts.get(2), "price");
                    int qty = parseInt(parts.get(3), "qty");
                    int minLimit = parseInt(parts.get(4), "minLimit");

                    Unit unit;
                    int categoryId;

                    if (parts.size() >= COLS_NEW) {
                        unit = Unit.parse(parts.get(5));
                        categoryId = parseInt(parts.get(6), "categoryId");
                    } else {
                        unit = Unit.PCS;
                        categoryId = parseInt(parts.get(5), "categoryId");
                    }

                    Category category = categoriesById.get(categoryId);
                    if (category == null) {
                        if (strict) throw new RepositoryException("Unknown categoryId=" + categoryId + " in line: " + line);
                        continue;
                    }

                    items.add(new Item(id, name, price, qty, minLimit, unit, category));

                } catch (RuntimeException ex) {
                    if (strict) throw ex;
                }
            }
        } catch (IOException e) {
            throw new RepositoryException("Error reading items file: " + itemsFile, e);
        }

        return items;
    }

    public void saveAll(Collection<Item> items) {
        ensureDataDir();

        Path tmp = itemsFile.resolveSibling(itemsFile.getFileName().toString() + ".tmp");

        try (BufferedWriter bw = Files.newBufferedWriter(
                tmp, StandardCharsets.UTF_8,
                StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING)) {

            bw.write(HEADER);
            bw.newLine();

            for (Item item : items) {
                String line = String.join(String.valueOf(delimiter),
                        String.valueOf(item.getId()),
                        Csv.escape(item.getName(), delimiter),
                        Double.toString(item.getPrice()),
                        String.valueOf(item.getQuantity()),
                        String.valueOf(item.getMinLimit()),
                        item.getUnit().name(),
                        String.valueOf(item.getCategory().getId())
                );

                bw.write(line);
                bw.newLine();
            }

        } catch (IOException e) {
            throw new RepositoryException("Error saving items file: " + itemsFile, e);
        }

        try {
            Files.move(tmp, itemsFile, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
        } catch (IOException atomicFail) {
            try {
                Files.move(tmp, itemsFile, StandardCopyOption.REPLACE_EXISTING);
            } catch (IOException e2) {
                throw new RepositoryException("Error replacing items file: " + itemsFile, e2);
            }
        }
    }

    private static int parseInt(String s, String field) {
        try {
            return Integer.parseInt(s.trim());
        } catch (Exception e) {
            throw new RepositoryException("Invalid " + field + ": " + s);
        }
    }

    private static double parseDouble(String s, String field) {
        try {
            return Double.parseDouble(s.trim());
        } catch (Exception e) {
            throw new RepositoryException("Invalid " + field + ": " + s);
        }
    }

    private static final class Csv {
        private Csv() {}

        static List<String> parseLine(String line, char delimiter) {
            List<String> out = new ArrayList<>();
            StringBuilder cur = new StringBuilder();
            boolean inQuotes = false;

            for (int i = 0; i < line.length(); i++) {
                char ch = line.charAt(i);

                if (inQuotes) {
                    if (ch == '"') {
                        if (i + 1 < line.length() && line.charAt(i + 1) == '"') {
                            cur.append('"');
                            i++;
                        } else {
                            inQuotes = false;
                        }
                    } else {
                        cur.append(ch);
                    }
                } else {
                    if (ch == '"') {
                        inQuotes = true;
                    } else if (ch == delimiter) {
                        out.add(cur.toString());
                        cur.setLength(0);
                    } else {
                        cur.append(ch);
                    }
                }
            }
            out.add(cur.toString());
            return out;
        }

        static String escape(String s, char delimiter) {
            if (s == null) return "";
            boolean needQuotes = false;
            for (int i = 0; i < s.length(); i++) {
                char ch = s.charAt(i);
                if (ch == delimiter || ch == '"' || ch == '\n' || ch == '\r') {
                    needQuotes = true;
                    break;
                }
            }
            if (!needQuotes) return s;
            return "\"" + s.replace("\"", "\"\"") + "\"";
        }
    }
}


