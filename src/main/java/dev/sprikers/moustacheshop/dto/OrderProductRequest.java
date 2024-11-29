package dev.sprikers.moustacheshop.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

import dev.sprikers.moustacheshop.models.CategoryModel;
import dev.sprikers.moustacheshop.models.ProductModel;

@Data
@EqualsAndHashCode(callSuper = true)
public class OrderProductRequest extends ProductModel {
    private int quantity;
    private double total;

    public OrderProductRequest(String id, String name, double price, int stock, String description, CategoryModel category, int quantity, double total) {
        super(id, name, price, stock, description, category);
        this.quantity = quantity;
        this.total = total;
    }
}
