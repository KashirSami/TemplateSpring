package com.template.template.handler;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class AdminValidator {

    @Value("${admin.username}")
    private String adminEmail;

    @Value("${admin.password}")
    private String adminPassword;

    public boolean isAdmin(String email, String rawPassword) {
        return email != null && email.equals(adminEmail) && 
               rawPassword != null && rawPassword.equals(adminPassword);
    }
}