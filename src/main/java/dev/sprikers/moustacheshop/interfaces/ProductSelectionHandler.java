package dev.sprikers.moustacheshop.interfaces;

import dev.sprikers.moustacheshop.models.ProductModel;

@FunctionalInterface
public interface ProductSelectionHandler {
    void onProductSelected(ProductModel product);
}
