package com.portfolio.acfs.service;

import com.portfolio.acfs.entity.User;

public interface AuthService {

    User register(String email, String password);

    String authenticate(String email, String password);
}
