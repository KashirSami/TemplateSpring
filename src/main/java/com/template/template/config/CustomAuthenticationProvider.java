package com.template.template.config;

import com.template.template.handler.AdminValidator;
import com.template.template.model.User;
import com.template.template.service.FirebaseAuthService;
import com.template.template.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.concurrent.ExecutionException;

@Component
public class CustomAuthenticationProvider implements AuthenticationProvider {

    @Autowired
    private AdminValidator adminValidator;

    @Autowired
    private FirebaseAuthService firebaseAuthService;

    @Autowired
    private UserService userService;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String username = authentication.getName();
        String password = authentication.getCredentials().toString();

        // 1. Check for Super Admin
        if (adminValidator.isAdmin(username, password)) {
            return new UsernamePasswordAuthenticationToken(
                    username,
                    password,
                    Collections.singletonList(new SimpleGrantedAuthority("ROLE_ADMIN"))
            );
        }

        // 2. Check for Firebase User
        try {
            boolean loginSuccess = firebaseAuthService.loginUser(username, password);
            if (loginSuccess) {
                User user = userService.findByEmail(username);
                String role = "ROLE_USER";
                
                // Check for dynamic admin role in database
                if (user != null && "ADMIN".equalsIgnoreCase(user.getRole())) {
                    role = "ROLE_ADMIN";
                }

                return new UsernamePasswordAuthenticationToken(
                        username,
                        password,
                        Collections.singletonList(new SimpleGrantedAuthority(role))
                );
            }
        } catch (Exception e) {
            throw new BadCredentialsException("Authentication failed", e);
        }

        throw new BadCredentialsException("Invalid credentials");
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
    }
}
