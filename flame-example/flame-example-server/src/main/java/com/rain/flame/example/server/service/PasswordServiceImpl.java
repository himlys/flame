package com.rain.flame.example.server.service;

import com.rain.flame.config.annatation.Service;
import com.rain.flame.example.entity.User;
import com.rain.flame.example.service.PasswordService;

@Service
public class PasswordServiceImpl extends AbstractService implements PasswordService {
    @Override
    public String getPassword(User user) {

        return "password is on the port: " + port;
    }
}
