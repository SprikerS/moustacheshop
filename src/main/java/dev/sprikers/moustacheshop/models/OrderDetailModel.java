package dev.sprikers.moustacheshop.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Data
@NoArgsConstructor
public class OrderDetailModel {
    private String id;
    private int cantidad;
    private double precioVenta;
    private double total;
    private ProductModel producto;
}
