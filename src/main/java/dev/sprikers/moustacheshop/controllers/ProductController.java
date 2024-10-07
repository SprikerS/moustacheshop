package dev.sprikers.moustacheshop.controllers;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import dev.sprikers.moustacheshop.dto.ProductRequest;
import dev.sprikers.moustacheshop.models.ProductModel;
import dev.sprikers.moustacheshop.services.ProductService;
import dev.sprikers.moustacheshop.utils.AlertManager;

public class ProductController implements Initializable {

    private final ProductService productService = new ProductService();
    private List<ProductModel> products;
    private ProductModel productSelected;

    @FXML
    private JFXTextField txtName, txtPrice, txtStock, txtSearch;

    @FXML
    private Label lblTotalProducts;

    @FXML
    private JFXButton btnClean, btnDelete, btnSubmit;

    @FXML
    private Button btnReload, btnSearch;

    @FXML
    private TableColumn<ProductModel, String> colName;

    @FXML
    private TableColumn<ProductModel, String> colPrice;

    @FXML
    private TableColumn<ProductModel, Integer> colStock;

    @FXML
    private TableView<ProductModel> tblProducts;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        initializeTableColumns();
        initializeEventHandlers();
        loadProducts();
        btnClean.setVisible(false);
        btnDelete.setVisible(false);
    }

    private void initializeTableColumns() {
        colName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colPrice.setCellValueFactory(new PropertyValueFactory<>("price"));
        colStock.setCellValueFactory(new PropertyValueFactory<>("stock"));
    }

    private void initializeEventHandlers() {
        btnClean.setOnAction(event -> resetForm());
        btnDelete.setOnAction(event -> handleDelete());
        btnReload.setOnAction(event -> handleReload());
        btnSearch.setOnAction(event -> handleSearch());
        btnSubmit.setOnAction(event -> handleSubmit());

        tblProducts.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                ProductModel product = tblProducts.getSelectionModel().getSelectedItem();
                setProductSelected(product);
            }
        });

        txtSearch.setOnKeyReleased(event -> {
            handleSearch();
        });
    }

    private void loadProducts() {
        try {
            products = productService.allProducts();
            tblProducts.getItems().clear();
            tblProducts.getItems().addAll(products);
            lblTotalProducts.setText(String.valueOf(products.size()));
        } catch (Exception e) {
            AlertManager.showErrorMessage("Error al cargar los productos: " + e.getMessage());
        }
    }

    private void searchProduct(String name) {
        try {
            List<ProductModel> results = products.stream()
                    .filter(product -> product.getName().toLowerCase().contains(name.toLowerCase()))
                    .toList();
            tblProducts.getItems().clear();
            tblProducts.getItems().addAll(results);
            lblTotalProducts.setText(String.valueOf(results.size()));
        } catch (Exception e) {
            AlertManager.showErrorMessage("Error al cargar los productos: " + e.getMessage());
        }
    }

    private void handleSubmit() {
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
            if (productSelected != null) {
                productService.update(productRequest, productSelected.getId());
            } else {
                productService.register(productRequest);
            }

            handleReload();
        } catch (NumberFormatException e) {
            AlertManager.showErrorMessage("El precio o stock no tienen un formato válido.");
        } catch (Exception e) {
            AlertManager.showErrorMessage("Error al registrar el producto: " + e.getMessage());
        }
    }

    private void handleSearch() {
        String searchText = txtSearch.getText().trim().toLowerCase();
        if (searchText.isEmpty()) {
            loadProducts();
        } else {
            searchProduct(searchText);
        }
    }

    private void handleDelete() {
        try {
            productService.delete(productSelected.getId());
            handleReload();
        } catch (Exception e) {
            AlertManager.showErrorMessage(e.getMessage());
        }
    }

    private void handleReload() {
        resetForm();
        loadProducts();
    }

    private void setProductSelected(ProductModel product) {
        productSelected = product;
        txtName.setText(product.getName());
        txtPrice.setText(product.getPrice());
        txtStock.setText(String.valueOf(product.getStock()));

        btnClean.setVisible(true);
        btnDelete.setVisible(true);
        btnSubmit.setText("Actualizar");
    }

    private void resetForm() {
        productSelected = null;
        txtName.clear();
        txtPrice.clear();
        txtStock.clear();
        tblProducts.getSelectionModel().clearSelection();

        btnClean.setVisible(false);
        btnDelete.setVisible(false);
        btnSubmit.setText("Registrar");
    }

}
