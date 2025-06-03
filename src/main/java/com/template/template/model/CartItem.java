package com.template.template.model;

import com.google.cloud.spring.data.firestore.Document;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Document(collectionName = "cart")
public class CartItem {
    private String id;
    private String itemName;
    private String productId;
    private double unitPrice;
    private int quantity;
}
