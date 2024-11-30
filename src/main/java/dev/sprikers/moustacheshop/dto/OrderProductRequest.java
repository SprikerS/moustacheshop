package dev.sprikers.moustacheshop.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

import dev.sprikers.moustacheshop.models.CategoryModel;
import dev.sprikers.moustacheshop.models.ProductModel;

@Data
@EqualsAndHashCode(callSuper = true)
public class OrderProductRequest extends ProductModel {
    private int cantidad;
    private double total;

    public OrderProductRequest(
        String id,
        String nombre,
        double precio,
        int stock,
        String descripcion,
        CategoryModel categoria,
        boolean activo,
        int cantidad,
        double total
    ) {
        super(id, nombre, precio, stock, descripcion, categoria, activo);
        this.cantidad = cantidad;
        this.total = total;
    }
}
