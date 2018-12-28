package com.rain.flame.example.server.service;

import com.rain.flame.config.annatation.Service;
import com.rain.flame.example.entity.User;
import com.rain.flame.example.service.UserService;

@Service(protocol = "flame", registry = "flame")
// service可无参，详细参照说明文档。
public class UserServiceImpl extends AbstractService implements UserService {
    @Override
    public User getUser(String p, User user) {
        user.setUserName("TOM LEADER is on the port: " + port);
        return user;
    }

    @Override
    public User aUser(String name) {
        return new User("aUser TOM LEADER is on the port: " + port);
    }
}
