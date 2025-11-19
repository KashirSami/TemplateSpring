package com.template.template.handler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

        return email.equals(adminEmail) &&
                rawPassword.equals(adminPassword);
    }
}