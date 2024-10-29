package dev.sprikers.moustacheshop.utils;

import java.util.List;

import javafx.application.Platform;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;

import dev.sprikers.moustacheshop.components.Toaster;
import dev.sprikers.moustacheshop.interfaces.ProductSelectionHandler;
import dev.sprikers.moustacheshop.models.ProductModel;
import dev.sprikers.moustacheshop.services.ProductService;

/**
 * Clase de utilidad para manejar la visualización y gestión de productos en una tabla.
 * Proporciona métodos para configurar columnas, cargar productos, y manejar eventos de selección de productos.
 *
 * @author SprikerS
 */
public class TableProducts {

    private static final ProductService productService = new ProductService();
    private final SpinnerLoader spinner = new SpinnerLoader();

    private final TableView<ProductModel> table;
    private final TextField txtSearch;
    private final Label total;
    private final Button btnReload;
    private final HBox hbSpinner;

    private TableColumn<ProductModel, String> colName;
    private TableColumn<ProductModel, String> colPrice;
    private TableColumn<ProductModel, Integer> colStock;

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
     */
    public TableProducts(TableView<ProductModel> table, TextField search, Label label, Button reload, HBox container) {
        this.table = table;
        this.txtSearch = search;
        this.total = label;
        this.btnReload = reload;
        this.hbSpinner = container;

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
        TableColumn<ProductModel, String> price,
        TableColumn<ProductModel, Integer> stock
    ) {
        this.colName = name;
        this.colPrice = price;
        this.colStock = stock;

        initializeTableColumns();
    }

    /**
     * Inicializa las columnas de la tabla con los valores correspondientes de los productos.
     */
    private void initializeTableColumns() {
        colName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colPrice.setCellValueFactory(new PropertyValueFactory<>("price"));
        colStock.setCellValueFactory(new PropertyValueFactory<>("stock"));
    }

    /**
     * Inicializa los manejadores de eventos para los componentes de la interfaz.
     */
    private void initializeEventHandlers() {
        btnReload.setOnMouseClicked(event -> {
            refreshTable = true;
            loadProducts();
        });
        txtSearch.setOnKeyReleased(event -> handleSearch());
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
     * Carga la lista de productos desde el servicio y actualiza la tabla.
     * Muestra un spinner de carga mientras se realiza la petición.
     */
    public void loadProducts() {
        toggleSpinner(true);
        table.getSelectionModel().clearSelection();
        total.setText("0");

        productService.allProducts()
            .thenAccept(this::updateProductList)
            .exceptionally(this::handleLoadError);
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
     * Maneja la búsqueda de productos en la tabla.
     */
    private void handleSearch() {
        String searchText = txtSearch.getText().trim().toLowerCase();
        if (searchText.isEmpty()) setProductsList(productsList);
        else searchProduct(searchText);
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

    /**
     * Busca productos por nombre en la lista de productos.
     *
     * @param name Nombre del producto a buscar.
     */
    private void searchProduct(String name) {
        try {
            List<ProductModel> results = productsList.stream()
                .filter(product -> product.getName().toLowerCase().contains(name))
                .toList();
            setProductsList(results);
        } catch (Exception e) {
            Toaster.showError("Error al buscar el producto");
        }
    }

}
