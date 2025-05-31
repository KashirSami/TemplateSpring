package com.template.template.model;

import com.alibaba.excel.annotation.ExcelProperty;

public class ProductRow {

    @ExcelProperty("ID")
    private String id;

    @ExcelProperty("nombre")
    private String nombre;

    @ExcelProperty("descripcion")
    private String descripcion;

    @ExcelProperty("categoria")
    private String categoria;

    @ExcelProperty("precio")
    private double precio;

    @ExcelProperty("stock")
    private int stock;



    public ProductRow() {
    }

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

    public void setDescripcion(String description) {
        this.descripcion = description;
    }

    public double getPrecio() {
        return precio;
    }

    public void setPrecio(double precio) {
        this.precio = precio;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }
}


