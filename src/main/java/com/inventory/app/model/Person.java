package com.inventory.app.model;

public class Person {
    private String _FirstName;
    private String _LastName;
    private String _Email;
    private String _Phone;

    public Person(String _FirstName, String _LastName, String _Email, String _Phone) {
        this._FirstName = _FirstName;
        this._LastName = _LastName;
        this._Email = _Email;
        this._Phone = _Phone;
    }

    public String get_FirstName() {
        return _FirstName;
    }

    public void set_FirstName(String _FirstName) {
        this._FirstName = _FirstName;
    }

    public String get_LastName() {
        return _LastName;
    }

    public void set_LastName(String _LastName) {
        this._LastName = _LastName;
    }

    public String get_Email() {
        return _Email;
    }

    public void set_Email(String _Email) {
        this._Email = _Email;
    }

    public String get_Phone() {
        return _Phone;
    }

    public void set_Phone(String _Phone) {
        this._Phone = _Phone;
    }

}
