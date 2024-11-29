package dev.sprikers.moustacheshop.controllers;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.concurrent.CompletableFuture;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;

import dev.sprikers.moustacheshop.components.Toaster;
import dev.sprikers.moustacheshop.dto.ProductRequest;
import dev.sprikers.moustacheshop.models.ProductModel;
import dev.sprikers.moustacheshop.services.ProductService;
import dev.sprikers.moustacheshop.utils.AlertManager;
import dev.sprikers.moustacheshop.utils.TableProducts;
import dev.sprikers.moustacheshop.utils.TextFieldFormatter;

public class ProductController implements Initializable {

    private TableProducts tableProducts;
    private ProductModel productSelected;

    private final ProductService productService = new ProductService();

    @FXML
    private Button btnClean, btnDelete, btnReload, btnSubmit;

    @FXML
    private Label lblTotalProducts;

    @FXML
    private TextField txtDescription, txtName, txtPrice, txtStock, txtSearch;

    @FXML
    private HBox hbSpinner;

    @FXML
    private TableColumn<ProductModel, String> colCategory;

    @FXML
    private TableColumn<ProductModel, String> colDescription;

    @FXML
    private TableColumn<ProductModel, String> colName;

    @FXML
    private TableColumn<ProductModel, Double> colPrice;

    @FXML
    private TableColumn<ProductModel, Integer> colStock;

    @FXML
    private TableView<ProductModel> tblProducts;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        tableProducts = new TableProducts(tblProducts, txtSearch, lblTotalProducts, btnReload, hbSpinner);
        tableProducts.setColumns(colName, colPrice, colStock, colCategory, colDescription);
        tableProducts.setOnProductSelected(this::setProductSelected);
        tableProducts.loadProducts();

        initializeEventHandlers();
        btnClean.setVisible(false);
        btnDelete.setVisible(false);

        TextFieldFormatter.applyIntegerFormat(txtStock);
        TextFieldFormatter.applyDecimalFormat(txtPrice, 2, 3);
    }

    private void initializeEventHandlers() {
        btnClean.setOnAction(event -> resetForm());
        btnDelete.setOnAction(event -> deleteProduct());
        btnSubmit.setOnAction(event -> submitProductForm());
    }

    private void submitProductForm() {
        String name = txtName.getText().trim().toLowerCase();
        String description = txtDescription.getText().trim();
        String valPrice = txtPrice.getText().trim();
        String valStock = txtStock.getText().trim();

        if (name.isEmpty() || valPrice.isEmpty() || valStock.isEmpty()) {
            AlertManager.showErrorMessage("Por favor, complete todos los campos");
            return;
        }

        try {
            double price = Double.parseDouble(valPrice);
            int stock = Integer.parseInt(valStock);

            if (price <= 0 || stock <= 0) {
                AlertManager.showErrorMessage("El campo precio o stock tienen que ser mayor a 0");
                return;
            }

            ProductRequest productRequest = new ProductRequest(name, price, stock, description);
            saveOrUpdateProduct(productRequest);
        } catch (NumberFormatException e) {
            AlertManager.showErrorMessage("El precio o stock no tienen un formato válido.");
        }
    }

    private void saveOrUpdateProduct(ProductRequest productRequest) {
        submitButtonState(true);
        boolean isUpdating = (productSelected != null);

        String action = isUpdating ? "actualizar" : "registrar";
        CompletableFuture<ProductModel> productFuture = isUpdating
            ? productService.update(productRequest, productSelected.getId())
            : productService.register(productRequest);

        productFuture
            .thenAccept(producto -> Platform.runLater(() -> {
                submitButtonState(false);
                String messageToast = isUpdating
                    ? "Producto %s actualizado con éxito".formatted(producto.getName())
                    : "Producto %s registrado con éxito".formatted(producto.getName());
                Toaster.showSucessOrInfo(messageToast, isUpdating);
                handleReload();
            }))
            .exceptionally(ex -> {
                Platform.runLater(() -> {
                    submitButtonState(false);
                    AlertManager.showErrorMessage("Error al %s el producto: %s".formatted(action, ex.getCause().getMessage()));
                });
                return null;
            });
    }

    private void deleteProduct() {
        boolean confirmed = AlertManager.showConfirmation("¿Estás seguro de que deseas eliminar este producto?", Alert.AlertType.WARNING);
        if (!confirmed) return;

        productService.delete(productSelected.getId())
            .thenRun(() -> Platform.runLater(() -> {
                Toaster.showNeutral("Producto %s eliminado con éxito".formatted(productSelected.getName()));
                handleReload();
            }))
            .exceptionally(ex -> {
                Platform.runLater(() -> AlertManager.showErrorMessage("Error al eliminar el producto: " + ex.getCause().getMessage()));
                return null;
            });
    }

    private void setProductSelected(ProductModel product) {
        productSelected = product;
        txtName.setText(product.getName());
        txtPrice.setText(String.valueOf(product.getPrice()));
        txtStock.setText(String.valueOf(product.getStock()));
        txtDescription.setText(product.getDescription());

        btnClean.setVisible(true);
        btnDelete.setVisible(true);
        btnSubmit.setText("Actualizar");
    }

    private void resetForm() {
        productSelected = null;
        txtName.clear();
        txtPrice.clear();
        txtSearch.clear();
        txtStock.clear();
        txtDescription.clear();
        tblProducts.getSelectionModel().clearSelection();

        btnClean.setVisible(false);
        btnDelete.setVisible(false);
        btnSubmit.setText("Registrar");
        lblTotalProducts.requestFocus();
    }

    private void submitButtonState(boolean isLoading) {
        btnClean.setVisible(false);
        btnDelete.setVisible(false);

        String buttonText = isLoading ? "Cargando..." : productSelected != null ? "Actualizar" : "Registrar";
        btnSubmit.setText(buttonText);
        btnSubmit.setDisable(isLoading);
    }

    private void handleReload() {
        resetForm();
        tableProducts.loadProducts();
    }

}
