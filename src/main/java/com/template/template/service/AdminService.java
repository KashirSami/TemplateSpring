package com.template.template.service;

import com.google.api.client.util.Value;
import org.springframework.stereotype.Service;

@Service
public class AdminService {

    private String adminEmail="kashir.sami13@gmail.com";

    public boolean isAdmin(String email) {
        return adminEmail.equals(email);
    }
}