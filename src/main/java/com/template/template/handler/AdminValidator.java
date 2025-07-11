package com.template.template.handler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;


@Service
public class AdminValidator {

    @Value("${admin.email}")
    private String adminEmail;
    @Value("${admin.password.hash}")
    private String adminPasswordHash;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public boolean isAdmin(String email, String rawPassword) {
        return email.equals(adminEmail) &&
                passwordEncoder.matches(rawPassword, adminPasswordHash);
    }
}