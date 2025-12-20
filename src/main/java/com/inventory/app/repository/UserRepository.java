package com.inventory.app.repository;
import com.inventory.app.model.enums.Role;
import java.util.ArrayList;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.io.IOException;

import com.inventory.app.model.User;

public class UserRepository {

    public static ArrayList<User> loadUsersFromFile() {
        ArrayList<User> arrayUsers = new ArrayList<>();
        String filePath = "src\\main\\java\\com\\inventory\\app\\repository\\Users.txt";
        try {
            List<String> arrayLines = Files.readAllLines(Paths.get(filePath));
            for (String line : arrayLines) {
                arrayUsers.add(convertLineToUserObject(line));
            }
        } catch (IOException e) {
            System.out.println("حدث خطأ أثناء قراءة الملف: " + e.getMessage());
        }

        return arrayUsers;
    }

    public static String getTextRole(Role role) {
        if (role == Role.MANAGER)
            return "Manager";
        if (role == Role.SUPERVISOR)
            return "Supervisor";

        return "Empty";
    }

    public static Role getEnumRole(String role) {
        if (role.equals("Manager"))
            return Role.MANAGER;
        if (role.equals("Superviosr"))
            return Role.SUPERVISOR;
        return Role.EMPTY_ROLE;
    }

    private static User convertLineToUserObject(String line) {
        return convertLineToUserObject(line, "#//#");
    }

    private static User convertLineToUserObject(String line, String separator) {

        String[] data = line.split(separator);

        return new User(
                data[0],
                data[1],
                data[2],
                data[3],
                data[4],
                data[5],
                getEnumRole(data[6])
        );
    }

    public static User findByUsername(String username) {
        ArrayList<User> users = loadUsersFromFile(); // مسؤولية Repository فقط
        for (User u : users) {
            if (u.getUserName().equals(username)) return u;
        }
        return null;
    }
}
