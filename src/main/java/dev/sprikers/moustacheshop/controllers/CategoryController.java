package dev.sprikers.moustacheshop.controllers;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.concurrent.CompletableFuture;

import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import dev.sprikers.moustacheshop.components.Toaster;
import dev.sprikers.moustacheshop.dto.CategoryRequest;
import dev.sprikers.moustacheshop.models.CategoryModel;
import dev.sprikers.moustacheshop.services.CategoryService;
import dev.sprikers.moustacheshop.utils.AlertManager;

public class CategoryController implements Initializable {

    private static final CategoryService categoryService = new CategoryService();
    private CategoryModel selectedCategory;

    private enum CategoryState {
        DEFAULT,
        EDITING,
        LOADING
    }

    private final ObjectProperty<CategoryState> currentState = new SimpleObjectProperty<>(CategoryState.DEFAULT);

    @FXML
    private Button btnSubmit, btnReload, btnDelete, btnClean;

    @FXML
    private TableColumn<CategoryModel, String> colID;

    @FXML
    private TableColumn<CategoryModel, String> colNombre;

    @FXML
    private TableView<CategoryModel> tblCategories;

    @FXML
    private TextField txtName;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initializeTableColumns();
        initializeEventHandlers();
        setupBindingsControls();

        loadProductCategories();
    }

    private void submitProductForm() {
        String name = txtName.getText().trim().toLowerCase();

        if (name.isEmpty()) {
            AlertManager.showErrorMessage("Por favor, complete todos los campos");
            return;
        }

        CategoryRequest categoryRequest = new CategoryRequest(name);
        saveOrUpdateProduct(categoryRequest);
    }

    private void saveOrUpdateProduct(CategoryRequest category) {
        submitButtonState(true);
        boolean isUpdating = (selectedCategory != null);

        String action = isUpdating ? "actualizar" : "registrar";
        CompletableFuture<CategoryModel> categoryFuture = isUpdating
            ? categoryService.update(category, selectedCategory.getId())
            : categoryService.register(category);

        categoryFuture
            .thenAccept(producto -> Platform.runLater(() -> {
                submitButtonState(false);
                String messageToast = isUpdating
                    ? "Categoria %s actualizado con éxito".formatted(producto.getNombre())
                    : "Categoria %s registrado con éxito".formatted(producto.getNombre());
                Toaster.showSucessOrInfo(messageToast, isUpdating);
                handleReload();
            }))
            .exceptionally(ex -> {
                Platform.runLater(() -> {
                    submitButtonState(false);
                    AlertManager.showErrorMessage("Error al %s la categoria: %s".formatted(action, ex.getCause().getMessage()));
                });
                return null;
            });
    }

    private void loadProductCategories() {
        categoryService.getAll()
            .thenAccept(categories -> Platform.runLater(() -> tblCategories.getItems().setAll(categories)))
            .exceptionally(ex -> {
                Platform.runLater(() -> AlertManager.showErrorMessage("Error al cargar las categorías: " + ex.getCause().getMessage()));
                return null;
            });
    }

    private void deleteProduct() {
        boolean confirmed = AlertManager.showConfirmation("¿Estás seguro de que deseas elimar esta categoría?", Alert.AlertType.WARNING);
        if (!confirmed) return;

        categoryService.delete(selectedCategory.getId())
            .thenRun(() -> Platform.runLater(() -> {
                Toaster.showNeutral("Categoría %s eliminada con éxito".formatted(selectedCategory.getNombre()));
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
        btnDelete.visibleProperty().bind(currentState.isEqualTo(CategoryState.EDITING));

        btnSubmit.disableProperty().bind(currentState.isEqualTo(CategoryState.LOADING));
        btnSubmit.textProperty().bind(Bindings.createStringBinding(() -> switch (currentState.get()) {
            case EDITING -> "Actualizar";
            case LOADING -> "Cargando...";
            default -> "Registrar";
        }, currentState));

        BooleanBinding isFormModified = txtName.textProperty().isNotEmpty().or(txtName.textProperty().isNotEmpty());
        btnClean.visibleProperty().bind(isFormModified);
    }

    private void initializeTableColumns() {
        colID.setCellValueFactory(new PropertyValueFactory<>("id"));
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
    }

    private void initializeEventHandlers() {
        btnClean.setOnAction(event -> resetForm());
        btnDelete.setOnAction(event -> deleteProduct());
        btnReload.setOnAction(event -> loadProductCategories());
        btnSubmit.setOnAction(event -> submitProductForm());

        tblCategories.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                CategoryModel selectedCategory = tblCategories.getSelectionModel().getSelectedItem();
                setCategorySelected(selectedCategory);
            }
        });
    }

    private void setCategorySelected(CategoryModel category) {
        selectedCategory = category;

        if (category != null) {
            txtName.setText(category.getNombre());

            currentState.set(CategoryState.EDITING);
        } else {
            resetForm();
        }
    }

    private void resetForm() {
        selectedCategory = null;

        txtName.clear();

        currentState.set(CategoryState.DEFAULT);
    }

    private void submitButtonState(boolean isLoading) {
        if (isLoading) {
            currentState.set(CategoryState.LOADING);
        } else {
            currentState.set(selectedCategory != null ? CategoryState.EDITING : CategoryState.DEFAULT);
        }
    }

    private void handleReload() {
        resetForm();
        loadProductCategories();
    }

}
