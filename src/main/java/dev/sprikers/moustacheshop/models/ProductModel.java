package dev.sprikers.moustacheshop.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductModel {
    private String id;
    private String name;
    private double price;
    private int stock;
    private String description;
    private String category;
}
