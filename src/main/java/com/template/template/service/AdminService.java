package com.template.template.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class AdminService {

    @Value("${admin.email}")
    private String adminEmail;

    public boolean isAdmin(String email) {
        return adminEmail.equals(email);
    }
}