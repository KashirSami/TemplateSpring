package com.template.template.model;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
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

}


