package dev.sprikers.moustacheshop.controllers;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.CompletableFuture;

import com.jfoenix.controls.JFXButton;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;

import dev.sprikers.moustacheshop.dto.ProductRequest;
import dev.sprikers.moustacheshop.models.ProductModel;
import dev.sprikers.moustacheshop.services.ProductService;
import dev.sprikers.moustacheshop.utils.AlertManager;

public class ProductController implements Initializable {

    private final ProductService productService = new ProductService();
    private List<ProductModel> products;
    private ProductModel productSelected;

    @FXML
    private Label lblTotalProducts;

    @FXML
    private JFXButton btnClean, btnDelete, btnSubmit;

    @FXML
    private Button btnReload;

    @FXML
    private TextField txtName, txtPrice, txtStock, txtSearch;

    @FXML
    private HBox hbSpinner;

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
        btnSubmit.setOnAction(event -> handleSubmit());

        tblProducts.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                ProductModel product = tblProducts.getSelectionModel().getSelectedItem();
                if (product != null && !product.equals(productSelected)) {
                    setProductSelected(product);
                }
            }
        });

        txtSearch.setOnKeyReleased(event -> handleSearch());
    }

    private void loadProducts() {
        hbSpinner.setVisible(true);
        lblTotalProducts.setText("0");

        productService.allProducts()
            .thenAccept(products -> {
                this.products = products;
                Platform.runLater(() -> {
                    setProductsList(products);
                    hbSpinner.setVisible(false);
                });
            })
            .exceptionally(ex -> {
                Platform.runLater(() -> AlertManager.showErrorMessage("Error al cargar los productos: " + ex.getCause().getMessage()));
                return null;
            });
    }

    private void setProductsList(List<ProductModel> products) {
        tblProducts.getItems().clear();
        tblProducts.getItems().addAll(products);
        lblTotalProducts.setText(String.valueOf(products.size()));
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
            AlertManager.showErrorMessage("Error al buscar el producto: " + e.getMessage());
        }
    }

    private void handleSubmit() {
        String name = txtName.getText().trim().toLowerCase();
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

            ProductRequest productRequest = new ProductRequest(name, price, stock);
            saveOrUpdateProduct(productRequest);
        } catch (NumberFormatException e) {
            AlertManager.showErrorMessage("El precio o stock no tienen un formato válido.");
        }
    }

    private void saveOrUpdateProduct(ProductRequest productRequest) {
        submitButtonState(true);
        CompletableFuture<ProductModel> productFuture;
        String action = (productSelected != null) ? "actualizar" : "registrar";

        productFuture = (productSelected != null)
            ? productService.update(productRequest, productSelected.getId())
            : productService.register(productRequest);

        productFuture
            .thenAccept(producto -> Platform.runLater(() -> {
                submitButtonState(false);
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

    private void submitButtonState(boolean isLoading) {
        btnClean.setVisible(false);
        btnDelete.setVisible(false);

        String buttonText = isLoading ? "Cargando..." : productSelected != null ? "Actualizar" : "Registrar";
        btnSubmit.setText(buttonText);
        btnSubmit.setDisable(isLoading);
    }

    private void handleSearch() {
        String searchText = txtSearch.getText().trim().toLowerCase();
        if (searchText.isEmpty()) {
            setProductsList(products);
        } else {
            searchProduct(searchText);
        }
    }

    private void handleDelete() {
        boolean confirmed = AlertManager.showConfirmation("¿Estás seguro de que deseas eliminar este producto?", Alert.AlertType.WARNING);
        if (!confirmed) return;

        productService.delete(productSelected.getId())
            .thenRun(() -> Platform.runLater(this::handleReload))
            .exceptionally(ex -> {
                Platform.runLater(() -> AlertManager.showErrorMessage("Error al eliminar el producto: " + ex.getCause().getMessage()));
                return null;
            });
    }

    private void handleReload() {
        resetForm();
        loadProducts();
    }

    private void setProductSelected(ProductModel product) {
        productSelected = product;
        txtName.setText(product.getName());
        txtPrice.setText(String.valueOf(product.getPrice()));
        txtStock.setText(String.valueOf(product.getStock()));

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
        tblProducts.getSelectionModel().clearSelection();

        btnClean.setVisible(false);
        btnDelete.setVisible(false);
        btnSubmit.setText("Registrar");
        lblTotalProducts.requestFocus();
    }

}
