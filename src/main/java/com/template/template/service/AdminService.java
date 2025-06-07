package com.template.template.service;

import org.springframework.stereotype.Service;

@Service
public class AdminService {

    private String adminEmail="kashir.sami13@gmail.com";

    public boolean isAdmin(String email) {
        return adminEmail.equals(email);
    }
}