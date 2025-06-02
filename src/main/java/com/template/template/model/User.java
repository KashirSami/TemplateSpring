package com.template.template.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;

@Data
@Getter
@Setter
public class User {

    private Long id;
    private String email;
    private String password;
    private String nombre;
    private String telefono;

    private String direccion;
    private String stripeCustomerId;
}