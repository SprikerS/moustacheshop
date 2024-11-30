package dev.sprikers.moustacheshop.models;

import lombok.Data;

@Data
public class CategoryModel {
    private String id;
    private String name;

    @Override
    public String toString() {
        return name;
    }
}