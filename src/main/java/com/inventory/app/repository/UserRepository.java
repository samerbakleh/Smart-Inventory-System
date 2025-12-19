package com.inventory.app.repository;
import com.inventory.app.model.enums.Role;
import java.util.ArrayList;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.io.IOException;

import com.inventory.app.model.User;

public class UserRepository {

    public static ArrayList<User> LoadUsersFromFile() {
        ArrayList<User> ArrayUsers = new ArrayList<>();
        String FilePath = "src\\main\\java\\com\\inventory\\app\\repository\\Users.txt";
        try {
            List<String> ArrayLines = Files.readAllLines(Paths.get(FilePath));
            for (String Line : ArrayLines) {
                ArrayUsers.add(_ConvertLineToUserObject(Line));
            }
        } catch (IOException e) {
            System.out.println("حدث خطأ أثناء قراءة الملف: " + e.getMessage());
        }

        return ArrayUsers;
    }

    public static String GetTextRole(Role role) {
        if (role == Role.eMANAGER)
            return "Manager";
        if (role == Role.eSUPERVISOR)
            return "Supervisor";

        return "Empty";
    }

    public static Role GetEnumRole(String role) {
        if (role.equals("Manager"))
            return Role.eMANAGER;
        if (role.equals("Superviosr"))
            return Role.eSUPERVISOR;
        return Role.eEmptyRole;
    }

    private static User _ConvertLineToUserObject(String line) {
        return _ConvertLineToUserObject(line, "#//#");
    }

    private static User _ConvertLineToUserObject(String Line, String Separator) {

        String[] data = Line.split(Separator);

        return new User(
                data[0],
                data[1],
                data[2],
                data[3],
                data[4],
                data[5],
                GetEnumRole(data[6])
        );
    }

    public static User FindByUsername(String username) {
        ArrayList<User> users = LoadUsersFromFile(); // مسؤولية Repository فقط
        for (User u : users) {
            if (u.get_UserName().equals(username)) return u;
        }
        return null;
    }
}
