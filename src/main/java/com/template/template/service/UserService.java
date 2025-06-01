package com.template.template.service;

import com.template.template.model.User;
import com.template.template.repo.UserRepo;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private PasswordEncoder passwordEncoder;


    @Transactional
    public String registerUser(String nombre,
                               String email,
                               String rawPassword,
                               String telefono) {
        if (userRepo.findByEmail(email).isPresent()) {
            return "El correo ya está registrado";
        }
        User u = new User();
        u.setNombre(nombre);
        u.setEmail(email);
        u.setPassword(passwordEncoder.encode(rawPassword));
        u.setTelefono(telefono);
        userRepo.save(u);
        return "success";
    }


    public User findByEmail(String email) {
        return userRepo.findByEmail(email).orElse(null);
    }


    @Transactional
    public void save(User u) {
        userRepo.save(u);
    }

    public User getAuthenticatedUser() {
        // Asumimos que, al autenticar, guardamos el UserDetails.username = email
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof org.springframework.security.core.userdetails.User) {
            String email = ((org.springframework.security.core.userdetails.User) principal).getUsername();
            return findByEmail(email);
        }
        return null;
    }
}

