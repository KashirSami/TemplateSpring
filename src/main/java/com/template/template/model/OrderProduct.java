package com.template.template.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrderProduct {
    private String productId;
    private String nombre;
    private double precioUnitario;
    private int cantidad;
}
