package com.template.template.model;

import com.google.cloud.firestore.annotation.DocumentId;
import com.google.cloud.spring.data.firestore.Document;
import lombok.Getter;
import lombok.Setter;


@Setter
@Getter
@Document(collectionName = "productos")
public class Product {
    private String id;

    private String nombre;

    private String descripcion;

    private double precio;

    private int stock;

    private String categoria;

    public Product() {
    }
    public Product(String nombre, String descripcion, double precio, int stock, String categoria) {
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.precio = precio;
        this.stock = stock;
        this.categoria = categoria;
    }
}