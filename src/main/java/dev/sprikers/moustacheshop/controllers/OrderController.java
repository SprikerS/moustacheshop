package dev.sprikers.moustacheshop.controllers;

import java.net.URL;
import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.util.converter.IntegerStringConverter;

import dev.sprikers.moustacheshop.components.StepNavigator;
import dev.sprikers.moustacheshop.components.Toaster;
import dev.sprikers.moustacheshop.dto.OrderProductRequest;
import dev.sprikers.moustacheshop.dto.OrderRequest;
import dev.sprikers.moustacheshop.models.ProductModel;
import dev.sprikers.moustacheshop.services.OrderService;
import dev.sprikers.moustacheshop.utils.ReniecFetch;
import dev.sprikers.moustacheshop.utils.TableProducts;
import dev.sprikers.moustacheshop.utils.TextFieldFormatter;

public class OrderController implements Initializable {

    private final ObservableList<OrderProductRequest> listProductsOrder = FXCollections.observableArrayList();
    private static final OrderService orderService = new OrderService();

    private final StepNavigator stepNavigator = new StepNavigator();

    @FXML
    private Button btnNextTwo, btnNextThree, btnPrevOne, btnPrevTwo, btnSubmit;

    @FXML
    private Button btnReloadProducts;

    @FXML
    private HBox hbProductSpinner;

    @FXML
    private Label lblProducts, lblShoppingCart, lblShoppingClient, lblShoppingDNI, lblTotalAmount;

    @FXML
    private Region btnReniec;

    @FXML
    private Tab tabOne, tabTwo, tabThree;

    @FXML
    private TableColumn<OrderProductRequest, String> colOrderName;

    @FXML
    private TableColumn<OrderProductRequest, Double> colOrderPrice;

    @FXML
    private TableColumn<OrderProductRequest, Integer> colOrderQuantity;

    @FXML
    private TableColumn<OrderProductRequest, Integer> colOrderTotal;

    @FXML
    private TableColumn<ProductModel, String> colProductName;

    @FXML
    private TableColumn<ProductModel, String> colProductPrice;

    @FXML
    private TableColumn<ProductModel, Integer> colProductStock;

    @FXML
    private TableView<ProductModel> tblProducts;

    @FXML
    private TableView<OrderProductRequest> tblOrder;

    @FXML
    private TextField txtDNI, txtMaternalSurname, txtNames, txtPaternalSurname, txtSearchProducts;

    @FXML
    private VBox stepOne, stepTwo, stepThree;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        TableProducts tableProducts = new TableProducts(tblProducts, txtSearchProducts, lblProducts, btnReloadProducts, hbProductSpinner);
        tableProducts.setColumns(colProductName, colProductPrice, colProductStock);
        tableProducts.setOnProductSelected(this::setProductSelected);
        tableProducts.loadProducts();

        ReniecFetch.configureFields(btnReniec, txtDNI, txtNames, txtPaternalSurname, txtMaternalSurname);

        stepNavigator.setSteps(List.of(stepOne, stepTwo, stepThree));
        stepNavigator.setTabs(List.of(tabOne, tabTwo, tabThree));

        stepNavigator.firstStep(btnNextTwo);
        stepNavigator.addStep(btnPrevOne, btnNextThree);
        stepNavigator.lastStep(btnPrevTwo);

        initializeOrderTableColumns();
        initializeEventHandlers();
        setupBindingsLabels();
    }

    private void initializeOrderTableColumns() {
        colOrderName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colOrderPrice.setCellValueFactory(new PropertyValueFactory<>("price"));
        colOrderQuantity.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        colOrderTotal.setCellValueFactory(new PropertyValueFactory<>("total"));

        tblOrder.setItems(listProductsOrder);
        setupQuantityColumnWithFilter();
    }

    private void initializeEventHandlers() {
        btnNextThree.setOnAction(event -> createOrder());
    }

    private void setupQuantityColumnWithFilter() {
        colOrderQuantity.setCellFactory(column -> new TextFieldTableCell<>(new IntegerStringConverter()) {
            @Override
            public void startEdit() {
                super.startEdit();

                TextField textField = (TextField) getGraphic();
                if (textField != null) {
                    TextFieldFormatter.applyIntegerFormat(textField);
                    textField.getStyleClass().add("text-field-custom");
                }
            }
        });

        colOrderQuantity.setOnEditCommit(event -> {
            OrderProductRequest orderProduct = event.getRowValue();
            Integer newQuantity = event.getNewValue();
            String errorMessage = null;

            if (newQuantity == null) {
                errorMessage = "Debe ingresar un valor numérico";
            } else if (newQuantity.equals(orderProduct.getQuantity())) {
                return;
            } else if (newQuantity <= 0) {
                errorMessage = "La cantidad mínima debe ser 1";
            } else if (newQuantity > orderProduct.getStock()) {
                errorMessage = "La cantidad no debe superar el stock";
            }

            if (errorMessage != null) {
                Toaster.showError(errorMessage);
                tblOrder.refresh();
                return;
            }

            orderProduct.setQuantity(newQuantity);
            orderProduct.setTotal(Math.round(newQuantity * orderProduct.getPrice() * 100.0) / 100.0);

            tblOrder.refresh();
            listProductsOrder.set(listProductsOrder.indexOf(orderProduct), orderProduct);
        });
    }

    private void setProductSelected(ProductModel product) {
        Optional<OrderProductRequest> existingProduct = listProductsOrder.stream()
            .filter(orderProduct -> orderProduct.getId().equals(product.getId()))
            .findFirst();

        if (existingProduct.isPresent()) {
            Toaster.showWarning("Producto %s ya está en el carrito".formatted(product.getName()));
            return;
        }

        OrderProductRequest orderProduct = new OrderProductRequest(product.getId(), product.getName(), product.getPrice(), product.getStock(), 1, product.getPrice());
        listProductsOrder.add(orderProduct);
        tblOrder.refresh();
    }

    private void setupBindingsLabels() {
        bindLabelToSum(lblShoppingCart, () -> String.valueOf(listProductsOrder.stream().mapToInt(OrderProductRequest::getQuantity).sum()));
        bindLabelToSum(lblTotalAmount, () -> String.format("%.2f", listProductsOrder.stream().mapToDouble(OrderProductRequest::getTotal).sum()));
        lblShoppingClient.textProperty().bind(Bindings.format("%s %s %s", txtNames.textProperty(), txtPaternalSurname.textProperty(), txtMaternalSurname.textProperty()));
        lblShoppingDNI.textProperty().bind(txtDNI.textProperty());
    }

    private void bindLabelToSum(Label label, Supplier<String> valueSupplier) {
        label.textProperty().bind(Bindings.createStringBinding(valueSupplier::get, listProductsOrder));
    }

    private void createOrder() {
        String dni = txtDNI.getText().trim();
        String names = txtNames.getText().trim();
        String paternalSurname = txtPaternalSurname.getText().trim();
        String maternalSurname = txtMaternalSurname.getText().trim();

        List<Map<String, Object>> productsOrder = listProductsOrder.stream()
            .map(product -> {
                Map<String, Object> map = new HashMap<>();
                map.put("productId", product.getId());
                map.put("quantity", product.getQuantity());
                return map;
            })
            .collect(Collectors.toList());

        OrderRequest orderRequest = new OrderRequest("2024-09-26", dni, names, paternalSurname, maternalSurname, productsOrder);
        orderService.create(orderRequest)
            .thenAccept(response -> {
                Toaster.showSuccess("Orden registrada con éxito");
            })
            .exceptionally(ex -> {
                Toaster.showError("No se pudo crear la orden: %s".formatted(ex.getCause().getMessage()));
                ex.printStackTrace();
                return null;
            });
    }
}
