package com.template.template.handler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;


@Service
public class AdminValidator {

    private String adminEmail="kashir.sami13@gmail.com";
    private String adminPasswordHash="$2a$10$LsFcMJUHX6kRzGdAHMvzmuNV89oqPksHWijNvFv05CWLT7BY60H4G";

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostConstruct
    public void init() {
        this.adminPasswordHash = passwordEncoder.encode("Kashir123");
    }
    public boolean isAdmin(String email, String rawPassword) {
        System.out.println("Comparando adminEmail con: " + email);
        System.out.println("Password válida? " + passwordEncoder.matches(rawPassword, adminPasswordHash));
        return email.equals(adminEmail) &&
                passwordEncoder.matches(rawPassword, adminPasswordHash);
    }
}