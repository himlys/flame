package com.rain.flame.example.entity;

public class User {
    private int userId;
    private String userName;
    public User(String userName){
        this.userName = userName;
    }
    public int getUserId() {
        return userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
}
