package com.template.template.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AddToCartRequest {
    private String productId;
    private int cantidad;
}
