package dev.sprikers.moustacheshop.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Data
@NoArgsConstructor
public class OrderDetailModel {
    private String id;
    private int quantity;
    private double salePrice;
    private double total;
    private ProductModel product;
}
