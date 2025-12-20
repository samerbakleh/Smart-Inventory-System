package com.inventory.app.model;
import com.inventory.app.model.enums.Role;
public class User extends Person {
    private String userName;
    private String password;
    Role role ;
    public User(String firstName , String lastName , String email,String phone,String userName, String password , Role role) {
        super(firstName,lastName,email,phone);
        this.userName = userName;
        this.password = password;
        this.role = role;
    }

    public String getUserName() {
        return userName;
    }

    public String getPassword() {
        return password;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }


    public void setPassword(String password) {
        this.password = password;
    }


    public Role getRole() {
        return role;
    }


}
