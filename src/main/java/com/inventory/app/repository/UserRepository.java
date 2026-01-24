package com.inventory.app.repository;
import com.inventory.app.model.enums.Role;
import java.util.ArrayList;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.io.IOException;
import com.inventory.app.util.AppConstants;

import com.inventory.app.model.User;

public class UserRepository {
    private static ArrayList<User> arrayUsers = new ArrayList<>();

    public static void loadUsersFromFile() {
        String filePath = AppConstants.USERS_FILE;
        try {
            List<String> arrayLines = Files.readAllLines(Paths.get(filePath));
            for (String line : arrayLines) {
                arrayUsers.add(convertLineToUserObject(line));
            }
        } catch (IOException e) {
            System.out.println("Error while reading users file" + e.getMessage());
        }

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
                Role.textToRole(data[6])
        );
    }

    public static User findByUsername(String username) {
        for (User u : arrayUsers) {
            if (u.getUserName().equals(username)) return u;
        }
        return null;
    }
}
