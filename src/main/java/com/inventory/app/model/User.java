package com.inventory.app.model;

public class User extends Person {
    private String _UserName;
    private String _Password;
    enRole _Role ;
    public User(String FirstName , String LastName , String Email,String Phone,String userName, String password , enRole role) {
        super(FirstName,LastName,Email,Phone);
        _UserName = userName;
        _Password = password;
        _Role = role;
    }

    public String get_UserName() {
        return _UserName;
    }

    public String get_Password() {
        return _Password;
    }

    public void setUserName(String userName) {
        _UserName = userName;
    }


    public void setPassword(String password) {
        _Password = password;
    }

    public enum enRole {
        eMANAGER,
        eSUPERVISOR,
        eEmptyRole
    }

    public enRole get_Role() {
        return _Role;
    }


}
