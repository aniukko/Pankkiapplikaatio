package com.example.pankkiapplikaatio;

//Includes information of users
public class Users {
    private String username;
    private String password;
    private String name;
    private String phoneNum;
    private String address;

    public Users() {}

    public Users (String u, String p, String n, String pn, String a) {
        username = u;
        password = p;
        name = n;
        phoneNum = pn;
        address = a;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhoneNum() {
        return phoneNum;
    }

    public void setPhoneNum(String phoneNum) {
        this.phoneNum = phoneNum;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
