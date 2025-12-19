package com.inventory.app.model;
import com.inventory.app.model.enums.Role;
public class User extends Person {
    private String _UserName;
    private String _Password;
    Role _Role ;
    public User(String FirstName , String LastName , String Email,String Phone,String userName, String password , Role role) {
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


    public Role get_Role() {
        return _Role;
    }


}
