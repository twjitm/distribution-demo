package com.distribution.demo.server.Impl;

import com.distribution.demo.server.UserService;

public class UserServiceImpl implements UserService {
    @Override
    public String getUserName(int uid) {
        return "hi " + uid + "+you name is hello world";
    }
}
