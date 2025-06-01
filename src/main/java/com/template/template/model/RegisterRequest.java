package com.template.template.model;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Data
public class RegisterRequest {
    private String nombre;
    private String email;
    private String confirmEmail;
    private String password;
    private String confirmPassword;
    private String telefono;
}
