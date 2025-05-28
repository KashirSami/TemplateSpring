package com.template.template.model;

import lombok.Data;

import java.util.Date;

@Data
public class Product {
    private String id;
    private String nombre;
    private String descripcion;
    private double valor;
    private String categoria;
    private int stock;
    private String imagenUrl;
    private Date fechaCreacion;
}