package com.inventory.app.repository;

import java.util.ArrayList;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.io.IOException;

import com.inventory.app.model.User;

public class UserRepository {

    public static ArrayList<User> LoadUsersFromFile() {
        ArrayList<User> ArrayUsers = new ArrayList<>();
        String FilePath = "Smart-Inventory-System\\src\\main\\java\\com\\inventory\\app\\repository\\Users.txt";
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

    public static String GetTextRole(User.enRole role) {
        if (role == User.enRole.eMANAGER)
            return "Manager";
        if (role == User.enRole.eSUPERVISOR)
            return "Supervisor";

        return "Empty";
    }

    public static User.enRole GetEnumRole(String role) {
        if (role.equals("Manager"))
            return User.enRole.eMANAGER;
        if (role.equals("Superviosr"))
            return User.enRole.eSUPERVISOR;
        return User.enRole.eEmptyRole;
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


}
