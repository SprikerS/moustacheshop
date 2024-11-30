package dev.sprikers.moustacheshop.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductModel {
    private String id;
    private String nombre;
    private double precio;
    private int stock;
    private String descripcion;
    private CategoryModel categoria;
    private boolean activo;
}
