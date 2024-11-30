package dev.sprikers.moustacheshop.controllers;

import java.io.IOException;
import java.net.URL;
import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import com.jfoenix.controls.JFXCheckBox;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.DatePicker;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.util.converter.IntegerStringConverter;
import org.controlsfx.control.CheckComboBox;

import dev.sprikers.moustacheshop.components.StepNavigator;
import dev.sprikers.moustacheshop.components.Toaster;
import dev.sprikers.moustacheshop.constants.PathViews;
import dev.sprikers.moustacheshop.dto.OrderProductRequest;
import dev.sprikers.moustacheshop.dto.OrderRequest;
import dev.sprikers.moustacheshop.models.CategoryModel;
import dev.sprikers.moustacheshop.models.OrderModel;
import dev.sprikers.moustacheshop.models.ProductModel;
import dev.sprikers.moustacheshop.services.OrderService;
import dev.sprikers.moustacheshop.utils.*;

public class OrderController implements Initializable {

    private final ObservableList<OrderProductRequest> listProductsOrder = FXCollections.observableArrayList();
    private static final OrderService orderService = new OrderService();
    private OrderModel currentOrder;

    private final StepNavigator stepNavigator = new StepNavigator();

    @FXML
    private Button btnNextTwo, btnNextThree, btnPrevOne, btnPrevTwo, btnSaveOrder, btnSubmit;

    @FXML
    private Button btnReloadProducts;

    @FXML
    private CheckComboBox<CategoryModel> chkcbFilterCategories;

    @FXML
    private DatePicker dateOrder;

    @FXML
    private JFXCheckBox chkConfirmOrden;

    @FXML
    private HBox hbProductSpinner;

    @FXML
    private Label lblOrderDate, lblProducts, lblShoppingCart, lblShoppingClient, lblShoppingDate, lblShoppingDNI, lblTotalAmount;

    @FXML
    private ImageView qrImageView;

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
    private TableColumn<ProductModel, Boolean> colProductActive;

    @FXML
    private TableColumn<ProductModel, String> colProductCategory;

    @FXML
    private TableColumn<ProductModel, String> colProductDescription;

    @FXML
    private TableColumn<ProductModel, String> colProductName;

    @FXML
    private TableColumn<ProductModel, Double> colProductPrice;

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
        TableProducts tableProducts = new TableProducts(tblProducts, txtSearchProducts, lblProducts, btnReloadProducts, hbProductSpinner, chkcbFilterCategories);
        tableProducts.setColumns(colProductName, colProductPrice, colProductStock, colProductCategory, colProductDescription, colProductActive);
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
        colOrderName.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colOrderPrice.setCellValueFactory(new PropertyValueFactory<>("precio"));
        colOrderQuantity.setCellValueFactory(new PropertyValueFactory<>("cantidad"));
        colOrderTotal.setCellValueFactory(new PropertyValueFactory<>("total"));

        tblOrder.setItems(listProductsOrder);
        setupQuantityColumnWithFilter();
    }

    private void initializeEventHandlers() {
        btnNextThree.addEventHandler(ActionEvent.ACTION, event -> createOrder());

        BooleanBinding isListEmpty = Bindings.isEmpty(listProductsOrder);
        btnNextThree.disableProperty().bind(chkConfirmOrden.selectedProperty().not().or(isListEmpty));

        btnNextTwo.disableProperty()
            .bind(txtDNI.textProperty().length().lessThan(8)
            .or(txtNames.textProperty().isEmpty())
            .or(txtMaternalSurname.textProperty().isEmpty())
            .or(txtPaternalSurname.textProperty().isEmpty())
        );

        btnSaveOrder.setOnAction(event -> saveOrder());
        btnSubmit.setOnAction(event -> resetView());

        DatePickerFormatter.configureDatePicker(dateOrder, lblOrderDate);
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
            } else if (newQuantity.equals(orderProduct.getCantidad())) {
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

            orderProduct.setCantidad(newQuantity);
            orderProduct.setTotal(Math.round(newQuantity * orderProduct.getPrecio() * 100.0) / 100.0);

            tblOrder.refresh();
            listProductsOrder.set(listProductsOrder.indexOf(orderProduct), orderProduct);
        });
    }

    private void setProductSelected(ProductModel product) {
        Optional<OrderProductRequest> existingProduct = listProductsOrder.stream()
            .filter(orderProduct -> orderProduct.getId().equals(product.getId()))
            .findFirst();

        if (existingProduct.isPresent()) {
            Toaster.showWarning("Producto %s ya está en el carrito".formatted(product.getNombre()));
            return;
        }

        OrderProductRequest orderProduct = new OrderProductRequest(product.getId(), product.getNombre(), product.getPrecio(), product.getStock(), product.getDescripcion(), product.getCategoria(), product.isActivo(), 1, product.getPrecio());
        listProductsOrder.add(orderProduct);
        tblOrder.refresh();
    }

    private void setupBindingsLabels() {
        bindLabelToSum(lblShoppingCart, () -> String.valueOf(listProductsOrder.stream().mapToInt(OrderProductRequest::getCantidad).sum()));
        bindLabelToSum(lblTotalAmount, () -> String.format("%.2f", listProductsOrder.stream().mapToDouble(OrderProductRequest::getTotal).sum()));
        lblShoppingClient.textProperty().bind(Bindings.format("%s %s %s", txtNames.textProperty(), txtPaternalSurname.textProperty(), txtMaternalSurname.textProperty()));
        lblShoppingDNI.textProperty().bind(txtDNI.textProperty());
        lblShoppingDate.textProperty().bind(lblOrderDate.textProperty());
    }

    private void bindLabelToSum(Label label, Supplier<String> valueSupplier) {
        label.textProperty().bind(Bindings.createStringBinding(valueSupplier::get, listProductsOrder));
    }

    private void createOrder() {
        String dni = txtDNI.getText().trim();
        String names = txtNames.getText().trim();
        String paternalSurname = txtPaternalSurname.getText().trim();
        String maternalSurname = txtMaternalSurname.getText().trim();
        String orderDate = dateOrder.getValue().format(DatePickerFormatter.orderFormatter);

        if (dni.isEmpty() || names.isEmpty() || paternalSurname.isEmpty() || maternalSurname.isEmpty()) {
            Toaster.showError("Por favor complete todos los campos obligatorios.");
            return;
        }

        List<Map<String, Object>> productsOrder = listProductsOrder.stream()
            .map(product -> {
                Map<String, Object> map = new HashMap<>();
                map.put("productoId", product.getId());
                map.put("cantidad", product.getCantidad());
                return map;
            })
            .collect(Collectors.toList());

        OrderRequest orderRequest = new OrderRequest(orderDate, dni, names, paternalSurname, maternalSurname, productsOrder);
        orderService.create(orderRequest)
            .thenAccept(this::processOrder)
            .exceptionally(ex -> {
                Toaster.showError("No se pudo crear la orden: %s".formatted(ex.getCause().getMessage()));
                return null;
            });
    }

    private void processOrder(OrderModel order) {
        currentOrder = order;
        Toaster.showSuccess("Orden registrada con éxito");

        String qrLink = orderService.generateReportLink(order.getId());
        try {
            Image qrImage = QRGenerator.fromText(qrLink, 150, 150);
            qrImageView.setImage(qrImage);
        } catch (Exception e) {
            Toaster.showError("No se pudo generar el QR: " + e.getMessage());
        }
    }

    private void saveOrder() {
        if (currentOrder == null) {
            Toaster.showError("No hay ninguna orden para guardar");
            return;
        }

        orderService.fetchReport(currentOrder.getId())
            .thenAccept(fileContent -> {
                String fileName = "%s.pdf".formatted(currentOrder.getId());
                try {
                    FileUtils.saveFile("reports", fileName, fileContent);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                Toaster.showSuccess("Reporte guardado con éxito");
            })
            .exceptionally(ex -> {
                Toaster.showError("No se pudo obtener el reporte: %s".formatted(ex.getCause().getMessage()));
                return null;
            });
    }

    private void resetView() {
        HomeController.getInstance().forceReloadView(PathViews.ORDERS);
    }

}
