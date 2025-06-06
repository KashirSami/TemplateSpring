package com.template.template.model;

import com.google.cloud.spring.data.firestore.Document;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.List;

@Getter
@Setter
@Document(collectionName = "order")
public class Order {
    private String id;
    private String userId;
    private List<OrderProduct> product;
    private double total;
    private Date timestamp;
    private String status;
}
