package com.example.logmanagementsystem.service;

import com.example.logmanagementsystem.config.AppConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.servlet.http.HttpSession;

@Service
public class AuthService {

    @Autowired
    private AppConfig appConfig;

    public boolean authenticate(String username, String password) {
        return appConfig.getUsername().equals(username) &&
                appConfig.getPassword().equals(password);
    }

    public boolean isLoggedIn(HttpSession session) {
        return session.getAttribute("loggedIn") != null &&
                (Boolean) session.getAttribute("loggedIn");
    }

    public void login(HttpSession session) {
        session.setAttribute("loggedIn", true);
    }

    public void logout(HttpSession session) {
        session.invalidate();
    }
}