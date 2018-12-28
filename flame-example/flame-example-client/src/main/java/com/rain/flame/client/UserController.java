package com.rain.flame.client;

import com.rain.flame.config.annatation.Reference;
import com.rain.flame.example.entity.User;
import com.rain.flame.example.service.PasswordService;
import com.rain.flame.example.service.UserService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
public class UserController {
    @Reference
    UserService userService;
    @Reference
    PasswordService passwordService;

    @RequestMapping("/get")
    public User getUser() {
        return userService.getUser("1234", new User("234"));
    }

    @RequestMapping("/auser")
    public User aUser() {
        return userService.aUser("1234");
    }

    @RequestMapping("/password")
    public String password() {
        return passwordService.getPassword(new User("1"));
    }
}
