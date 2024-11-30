package dev.sprikers.moustacheshop.controllers;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.concurrent.CompletableFuture;

import com.jfoenix.controls.JFXCheckBox;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.ListChangeListener;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import org.controlsfx.control.CheckComboBox;

import dev.sprikers.moustacheshop.components.Toaster;
import dev.sprikers.moustacheshop.dto.ProductRequest;
import dev.sprikers.moustacheshop.models.CategoryModel;
import dev.sprikers.moustacheshop.models.ProductModel;
import dev.sprikers.moustacheshop.services.CategoryService;
import dev.sprikers.moustacheshop.services.ProductService;
import dev.sprikers.moustacheshop.utils.AlertManager;
import dev.sprikers.moustacheshop.utils.TableProducts;
import dev.sprikers.moustacheshop.utils.TextFieldFormatter;

public class ProductController implements Initializable {

    private TableProducts tableProducts;
    private ProductModel productSelected;

    private enum ProductState {
        DEFAULT,
        EDITING,
        LOADING
    }

    private final ProductService productService = new ProductService();
    private final CategoryService categoryService = new CategoryService();
    private final ObjectProperty<ProductState> currentState = new SimpleObjectProperty<>(ProductState.DEFAULT);

    @FXML
    private Button btnClean, btnDelete, btnReload, btnSubmit;

    @FXML
    private ComboBox<CategoryModel> cbCategories;

    @FXML
    private CheckComboBox<CategoryModel> chkcbFilterCategories;

    @FXML
    private JFXCheckBox chkActive;

    @FXML
    private Label lblTotalProducts;

    @FXML
    private TextField txtDescription, txtName, txtPrice, txtStock, txtSearch;

    @FXML
    private HBox hbSpinner;

    @FXML
    private TableColumn<ProductModel, Boolean> colActive;

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
        tableProducts = new TableProducts(tblProducts, txtSearch, lblTotalProducts, btnReload, hbSpinner, chkcbFilterCategories);
        tableProducts.setColumns(colName, colPrice, colStock, colCategory, colDescription, colActive);
        tableProducts.setOnProductSelected(this::setProductSelected);
        tableProducts.loadProducts();

        initializeEventHandlers();
        loadProductCategories();
        setupBindingsControls();

        TextFieldFormatter.applyIntegerFormat(txtStock);
        TextFieldFormatter.applyDecimalFormat(txtPrice, 2, 3);
    }

    private void loadProductCategories() {
        categoryService.getAll()
            .thenAccept(categories -> Platform.runLater(() -> {

                CategoryModel defaultCategory = new CategoryModel();
                defaultCategory.setId(null);
                defaultCategory.setName("Seleccione una categoría");
                categories.addFirst(defaultCategory);

                cbCategories.getItems().addAll(categories);
                cbCategories.getSelectionModel().selectFirst();
            }))
            .exceptionally(ex -> {
                Platform.runLater(() -> AlertManager.showErrorMessage("Error al cargar las categorías: " + ex.getCause().getMessage()));
                return null;
            });
    }

    private String getSelectedCategory() {
        CategoryModel selectedCategory = cbCategories.getSelectionModel().getSelectedItem();
        return selectedCategory != null ? selectedCategory.getId() : null;
    }

    private void submitProductForm() {
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

            String categoryId = getSelectedCategory();
            String description = txtDescription.getText() != null ? txtDescription.getText().trim() : null;
            boolean active = chkActive.isSelected();

            ProductRequest productRequest = new ProductRequest(name, price, stock, description, categoryId, active);
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

    /* ----------------------------------
     * Sección de configuración de la UI
     * ---------------------------------- */

    private void setupBindingsControls() {
        btnDelete.visibleProperty().bind(currentState.isEqualTo(ProductState.EDITING));

        btnSubmit.disableProperty().bind(currentState.isEqualTo(ProductState.LOADING));
        btnSubmit.textProperty().bind(Bindings.createStringBinding(() -> switch (currentState.get()) {
            case EDITING -> "Actualizar";
            case LOADING -> "Cargando...";
            default -> "Registrar";
        }, currentState));

        cbCategories.getItems().addListener((ListChangeListener<CategoryModel>) change -> {
            if (cbCategories.getItems().isEmpty()) return;

            BooleanBinding isFormModified = txtName.textProperty().isNotEmpty()
                .or(txtStock.textProperty().isNotEmpty())
                .or(txtPrice.textProperty().isNotEmpty())
                .or(txtDescription.textProperty().isNotEmpty())
                .or(cbCategories.getSelectionModel().selectedItemProperty().isNotEqualTo(cbCategories.getItems().getFirst()));

            btnClean.visibleProperty().bind(isFormModified);
        });
    }

    private void initializeEventHandlers() {
        btnClean.setOnAction(event -> resetForm());
        btnDelete.setOnAction(event -> deleteProduct());
        btnSubmit.setOnAction(event -> submitProductForm());
    }

    private void setProductSelected(ProductModel product) {
        productSelected = product;

        if (product != null) {
            txtName.setText(product.getName());
            txtPrice.setText(String.valueOf(product.getPrice()));
            txtStock.setText(String.valueOf(product.getStock()));
            txtDescription.setText(product.getDescription());

            CategoryModel category = product.getCategory();
            cbCategories.getSelectionModel().select(category != null ? category : cbCategories.getItems().getFirst());
            chkActive.setSelected(product.isActive());

            currentState.set(ProductState.EDITING);
        } else {
            resetForm();
        }
    }

    private void resetForm() {
        productSelected = null;

        txtName.clear();
        txtPrice.clear();
        txtSearch.clear();
        txtStock.clear();
        txtDescription.clear();
        cbCategories.getSelectionModel().selectFirst();
        tblProducts.getSelectionModel().clearSelection();
        chkActive.setSelected(false);

        currentState.set(ProductState.DEFAULT);
    }

    private void submitButtonState(boolean isLoading) {
        if (isLoading) {
            currentState.set(ProductState.LOADING);
        } else {
            currentState.set(productSelected != null ? ProductState.EDITING : ProductState.DEFAULT);
        }
    }

    private void handleReload() {
        resetForm();
        tableProducts.loadProducts();
    }

}
