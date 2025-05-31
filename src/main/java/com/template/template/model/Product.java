package com.template.template.model;

import com.alibaba.excel.annotation.ExcelProperty;

public class Product {

    @ExcelProperty("ID")
    private String id;

    @ExcelProperty("Name")
    private String nombre;

    @ExcelProperty("Description")
    private String descripcion;

    @ExcelProperty("Price")
    private double precio;

    @ExcelProperty("stock")
    private int stock;



    @ExcelProperty("categoria")
    private String categoria;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public double getPrecio() {
        return precio;
    }

    public void setPrecio(double precio) {
        this.precio = precio;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }
}