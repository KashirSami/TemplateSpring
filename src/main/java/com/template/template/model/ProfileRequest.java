package com.template.template.model;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class ProfileRequest {
    private String nombre;
    private String direccion;
    private String telefono;
}
