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
    @Value("${admin.password}")
    private String adminPassword;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostConstruct
    public void init() {
        this.adminPasswordHash = passwordEncoder.encode(adminPassword);
    }
    public boolean isAdmin(String email, String rawPassword) {
        System.out.println("Comparando adminEmail con: " + email);
        System.out.println("Password válida? " + passwordEncoder.matches(rawPassword, adminPasswordHash));
        return email.equals(adminEmail) &&
                passwordEncoder.matches(rawPassword, adminPasswordHash);
    }
}