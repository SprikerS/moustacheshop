package dev.sprikers.moustacheshop.controllers;

import java.net.URL;
import java.util.Collections;
import java.util.List;
import java.util.ResourceBundle;

import com.jfoenix.controls.JFXTextField;
import com.jfoenix.controls.JFXTreeTableView;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.input.MouseEvent;

import dev.sprikers.moustacheshop.dto.ProductRequest;
import dev.sprikers.moustacheshop.models.ProductModel;
import dev.sprikers.moustacheshop.services.ProductService;
import dev.sprikers.moustacheshop.utils.AlertManager;

public class ProductsController implements Initializable {

    private final ProductService productService = new ProductService();

    @FXML
    private JFXTextField txtName, txtPrice, txtStock;

    @FXML
    private TreeTableColumn<ProductModel, String> colName;

    @FXML
    private TreeTableColumn<ProductModel, Double> colPrice;

    @FXML
    private TreeTableColumn<ProductModel, Integer> colStock;

    @FXML
    private JFXTreeTableView<?> ttvProducts;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        List<ProductModel> products = allProducts();
        if (products.isEmpty()) {
            System.out.println("No hay productos disponibles.");
        } else {
            System.out.println("Productos cargados: " + products);
        }
    }

    private List<ProductModel> allProducts() {
        try {
            return productService.allProducts();
        } catch (Exception e) {
            // AlertManager.showErrorMessage("Error al cargar la lista de productos: " + e.getMessage());
            System.out.println("Error al cargar la lista de productos: " + e.getMessage());
            return Collections.emptyList();
        }
    }

    @FXML
    void onRegister(MouseEvent event) {
        register();
    }

    private void register() {
        try {
            String name = txtName.getText().trim().toLowerCase();
            String valPrice = txtPrice.getText().trim();
            String valStock = txtStock.getText().trim();

            if (name.isEmpty() || valPrice.isEmpty() || valStock.isEmpty()) {
                AlertManager.showErrorMessage("Por favor, complete todos los campos");
                return;
            }

            double price = Double.parseDouble(valPrice);
            int stock = Integer.parseInt(valStock);

            if (price <= 0 || stock <= 0) {
                AlertManager.showErrorMessage("El campo precio o stock tienen que ser mayor a 0");
                return;
            }

            ProductRequest productRequest = new ProductRequest(name, price, stock);
            productService.register(productRequest);
            this.clearTexstField();
        } catch (NumberFormatException e) {
            AlertManager.showErrorMessage("El precio o stock no tienen un formato válido.");
        } catch (Exception e) {
            AlertManager.showErrorMessage("Error al registrar el producto: " + e.getMessage());
        }
    }

    private void clearTexstField() {
        txtName.clear();
        txtPrice.clear();
        txtStock.clear();
    }
}
