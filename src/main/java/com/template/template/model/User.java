package com.template.template.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;

@Entity
@Table(name = "users")
@Getter
@Setter
public class User {

    @jakarta.persistence.Id
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Datos mínimos que pides en el registro
    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;     // ya encriptada con BCrypt

    @Column(nullable = false)
    private String nombre;

    private String telefono;

    // ----------------------------------------------------------------
    // Campos “opcionales” que el usuario completará más tarde en su perfil
    // ----------------------------------------------------------------

    @Column(length = 255)
    private String direccion;
    // Se quedará nulo hasta que el usuario lo edite en “Mi perfil”

    @Column(name = "stripe_customer_id", length = 100)
    private String stripeCustomerId;

    // Se quedará nulo hasta que, al guardar un método de pago, crees
    // o asocies el cliente en Stripe y guardes aquí el ID.

    // (En el futuro podrías añadir otros campos: ciudad, provincia, código postal, etc.)
}