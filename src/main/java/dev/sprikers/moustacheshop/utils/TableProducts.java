package dev.sprikers.moustacheshop.utils;

import java.util.List;

import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.ListChangeListener;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import org.controlsfx.control.CheckComboBox;

import dev.sprikers.moustacheshop.components.Toaster;
import dev.sprikers.moustacheshop.interfaces.ProductSelectionHandler;
import dev.sprikers.moustacheshop.models.CategoryModel;
import dev.sprikers.moustacheshop.models.ProductModel;
import dev.sprikers.moustacheshop.services.CategoryService;
import dev.sprikers.moustacheshop.services.ProductService;

/**
 * Clase de utilidad para manejar la visualización y gestión de productos en una tabla.
 * Proporciona métodos para configurar columnas, cargar productos, y manejar eventos de selección de productos.
 *
 * @author SprikerS
 */
public class TableProducts {

    private static final ProductService productService = new ProductService();
    private static final CategoryService categoryService = new CategoryService();
    private final SpinnerLoader spinner = new SpinnerLoader();

    private final TableView<ProductModel> table;
    private final TextField txtSearch;
    private final Label total;
    private final Button btnReload;
    private final HBox hbSpinner;
    private final CheckComboBox<CategoryModel> filterCategories;

    private TableColumn<ProductModel, String> colName;
    private TableColumn<ProductModel, Double> colPrice;
    private TableColumn<ProductModel, Integer> colStock;
    private TableColumn<ProductModel, String> colCategory;
    private TableColumn<ProductModel, String> colDescription;
    private TableColumn<ProductModel, Boolean> colActive;

    private List<ProductModel> productsList;
    private ProductModel lastSelectedProduct;
    private ProductSelectionHandler onProductSelected;
    private boolean refreshTable;

    /**
     * Crea una instancia de TableProducts con los componentes de la interfaz proporcionados.
     *
     * @param table     Tabla donde se mostrarán los productos.
     * @param search    Campo de texto para buscar productos.
     * @param label     Etiqueta donde se mostrará el total de productos.
     * @param reload    Botón para recargar la lista de productos.
     * @param container Contenedor del spinner de carga.
     * @param filter    Filtro de categorías.
     */
    public TableProducts(
        TableView<ProductModel> table,
        TextField search,
        Label label,
        Button reload,
        HBox container,
        CheckComboBox<CategoryModel> filter
    ) {
        this.table = table;
        this.txtSearch = search;
        this.total = label;
        this.btnReload = reload;
        this.hbSpinner = container;
        this.filterCategories = filter;

        Region region = (Region) container.getChildren().getFirst();
        spinner.setNode(region);

        initializeEventHandlers();
    }

    /**
     * Configura las columnas de la tabla con las proporcionadas.
     *
     * @param name  Columna de nombre del producto.
     * @param price Columna de precio del producto.
     * @param stock Columna de stock del producto.
     */
    public void setColumns(
        TableColumn<ProductModel, String> name,
        TableColumn<ProductModel, Double> price,
        TableColumn<ProductModel, Integer> stock,
        TableColumn<ProductModel, String> category,
        TableColumn<ProductModel, String> description,
        TableColumn<ProductModel, Boolean> active
    ) {
        this.colName = name;
        this.colPrice = price;
        this.colStock = stock;
        this.colCategory = category;
        this.colDescription = description;
        this.colActive = active;

        initializeTableColumns();
    }

    /**
     * Inicializa las columnas de la tabla con los valores correspondientes de los productos.
     */
    private void initializeTableColumns() {
        colName.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colPrice.setCellValueFactory(new PropertyValueFactory<>("precio"));
        colStock.setCellValueFactory(new PropertyValueFactory<>("stock"));
        colCategory.setCellValueFactory(cellData -> {
            CategoryModel category = cellData.getValue().getCategoria();
            return new SimpleStringProperty(category != null ? category.getNombre() : null);
        });
        colDescription.setCellValueFactory(new PropertyValueFactory<>("descripcion"));
        colActive.setCellValueFactory(new PropertyValueFactory<>("activo"));
    }

    /**
     * Inicializa los manejadores de eventos para los componentes de la interfaz.
     */
    private void initializeEventHandlers() {
        btnReload.setOnMouseClicked(event -> {
            refreshTable = true;
            loadProducts();
            txtSearch.clear();
        });
        txtSearch.setOnKeyReleased(event -> handleSearch());
        filterCategories.getCheckModel().getCheckedItems().addListener((ListChangeListener<CategoryModel>) c -> handleSearch());
    }

    /**
     * Configura el manejador que se invocará al seleccionar un producto en la tabla.
     *
     * @param handler Manejador de eventos para la selección de productos.
     */
    public void setOnProductSelected(ProductSelectionHandler handler) {
        this.onProductSelected = handler;
        table.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                ProductModel selectedProduct = table.getSelectionModel().getSelectedItem();
                if (selectedProduct != null && !selectedProduct.equals(lastSelectedProduct)) {
                    lastSelectedProduct = selectedProduct;
                    onProductSelected.onProductSelected(selectedProduct);
                }
            }
        });
    }

    /**
     * Carga la lista completa de productos desde el servicio y actualiza la tabla.
     * También recarga las categorías disponibles en el filtro.
     *
     * <p>Comportamiento:</p>
     * <ul>
     *   <li>Muestra un spinner de carga mientras se obtienen los datos.</li>
     *   <li>Limpia la selección actual de la tabla.</li>
     *   <li>Establece el contador de productos en cero temporalmente.</li>
     *   <li>Maneja errores durante la carga mostrando mensajes al usuario.</li>
     * </ul>
     */
    public void loadProducts() {
        toggleSpinner(true);
        table.getSelectionModel().clearSelection();
        total.setText("0");

        productService.allProducts()
            .thenAccept(this::updateProductList)
            .exceptionally(this::handleLoadError);

        loadCategories();
    }

    /**
     * Carga la lista de categorías desde el servicio y actualiza el filtro de categorías.
     *
     * <p>Comportamiento:</p>
     * <ul>
     *   <li>Asigna las categorías obtenidas al filtro de categorías en la interfaz.</li>
     *   <li>Limpia cualquier selección previa en el filtro.</li>
     *   <li>Maneja errores durante la carga mostrando un mensaje al usuario.</li>
     * </ul>
     */
    public void loadCategories() {
        categoryService.getAll()
            .thenApply(categories -> {
                Platform.runLater(() -> {
                    filterCategories.getItems().setAll(categories);
                    filterCategories.getCheckModel().clearChecks();
                });
                return categories;
            })
            .exceptionally(ex -> {
                Platform.runLater(() -> AlertManager.showErrorMessage("Error al cargar las categorías: " + ex.getCause().getMessage()));
                return null;
            });
    }

    /**
     * Actualiza la lista de productos en la tabla con los productos proporcionados.
     *
     * @param products Lista de productos a mostrar en la tabla.
     */
    private void updateProductList(List<ProductModel> products) {
        Platform.runLater(() -> {
            productsList = products;
            setProductsList(products);
            toggleSpinner(false);
            if (refreshTable) {
                Toaster.showInfo("Lista de productos actualizada");
                refreshTable = false;
            }
        });
    }

    /**
     * Maneja los errores al cargar la lista de productos.
     *
     * @param ex Excepción lanzada durante la carga de productos.
     * @return Valor nulo.
     */
    private Void handleLoadError(Throwable ex) {
        Platform.runLater(() -> {
            Toaster.showError("Error al cargar la lista de productos");
            toggleSpinner(false);
        });
        return null;
    }

    /**
     * Muestra u oculta el spinner de carga y deshabilita el botón de recarga.
     *
     * @param start Indica si se debe iniciar o detener el spinner.
     */
    private void toggleSpinner(boolean start) {
        btnReload.setDisable(start);
        hbSpinner.setVisible(start);
        if (start) spinner.start();
        else spinner.stop(true);
    }

    /**
     * Filtra la lista de productos en la tabla según el texto ingresado en el campo de búsqueda
     * y las categorías seleccionadas en el filtro
     *
     * <p>Comportamiento:</p>
     * <ul>
     *   <li>Si el campo de búsqueda está vacío, aplica solo el filtro de categorías</li>
     *   <li>Si no hay categorías seleccionadas, aplica solo el filtro por texto</li>
     *   <li>Si ambos filtros están activos, combina los criterios</li>
     *   <li>Si no hay filtros activos, muestra todos los productos</li>
     * </ul>
     *
     * <p>La tabla se actualiza dinámicamente con los resultados del filtro</p>
     */
    private void handleSearch() {
        String searchText = txtSearch.getText().trim().toLowerCase();
        List<CategoryModel> selectedCategories = filterCategories.getCheckModel().getCheckedItems();

        List<ProductModel> filteredProducts = productsList.stream()
            .filter(product -> searchText.isEmpty() || product.getNombre().toLowerCase().contains(searchText))
            .filter(product -> selectedCategories.isEmpty() || selectedCategories.contains(product.getCategoria()))
            .toList();

        setProductsList(filteredProducts);
    }

    /**
     * Actualiza la lista de productos en la tabla con los productos proporcionados.
     * Muestra el total de productos en la etiqueta correspondiente.
     *
     * @param products Lista de productos a mostrar en la tabla.
     */
    private void setProductsList(List<ProductModel> products) {
        table.getItems().setAll(products);
        total.setText(String.valueOf(products.size()));
    }

}
