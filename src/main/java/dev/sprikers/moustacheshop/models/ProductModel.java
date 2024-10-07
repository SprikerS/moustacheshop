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
    private String price;
    private int stock;

    public double getPriceAsDouble() {
        return Double.parseDouble(price);
    }
}
