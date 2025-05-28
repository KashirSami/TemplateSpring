package com.template.template.model;

import com.alibaba.excel.annotation.ExcelProperty;

public class ProductRow {

    @ExcelProperty("ID")
    private String id;

    @ExcelProperty("Name")
    private String name;

    @ExcelProperty("Description")
    private String description;

    @ExcelProperty("Value")
    private double price;

    public ProductRow() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }
}


