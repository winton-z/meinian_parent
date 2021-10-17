package com.atguigu.service;

import com.atguigu.pojo.User;

public interface UserService { //ctrl + alt + b
    User findUserByUsername(String username);
}
