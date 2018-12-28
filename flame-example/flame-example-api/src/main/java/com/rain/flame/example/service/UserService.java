package com.rain.flame.example.service;

import com.rain.flame.example.entity.User;

public interface UserService {
    public User getUser(String p, User user);

    public User aUser(String name);
}
