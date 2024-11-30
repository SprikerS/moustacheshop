package dev.sprikers.moustacheshop.controllers;

import java.net.URL;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import dev.sprikers.moustacheshop.components.Toaster;
import dev.sprikers.moustacheshop.models.OrderDetailModel;
import dev.sprikers.moustacheshop.models.OrderModel;
import dev.sprikers.moustacheshop.models.UserModel;
import dev.sprikers.moustacheshop.services.OrderService;
import dev.sprikers.moustacheshop.utils.DatePickerFormatter;

public class OrderRecordsController implements Initializable {

    private static final OrderService orderService = new OrderService();

    private List<OrderModel> orders;

    @FXML
    private Button btnReload;

    @FXML
    private DatePicker dateFromPicker;

    @FXML
    private DatePicker dateToPicker;

    @FXML
    private Label lblFromDate;

    @FXML
    private Label lblToDate;

    @FXML
    private TableColumn<OrderModel, String> colDNI;

    @FXML
    private TableColumn<OrderModel, String> colNames;

    @FXML
    private TableColumn<OrderModel, String> colOrderDate;

    @FXML
    private TableColumn<OrderModel, Integer> colQuantity;

    @FXML
    private TableColumn<OrderModel, String> colSurnames;

    @FXML
    private TableColumn<OrderModel, Double> colTotal;

    @FXML
    private TableView<OrderModel> tblOrders;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        DatePickerFormatter.configureDatePicker(dateFromPicker, lblFromDate);
        DatePickerFormatter.configureDatePicker(dateToPicker, lblToDate);

        LocalDate monthAgo = LocalDate.now().minusMonths(1);
        dateFromPicker.setValue(monthAgo);

        initializeTableColumns();
        initializeEventHandlers();

        fetchOrderRecords();
    }

    private void fetchOrderRecords() {
        orderService.getAll()
            .thenAccept(orders -> {
                this.orders = orders;
                setOrdersList(orders);
            })
            .exceptionally(ex -> {
                Toaster.showError("No se pudo obtener la lista de reportes: %s".formatted(ex.getCause().getMessage()));
                return null;
        });
    }

    /* ----------------------------------
     * Sección de configuración de la UI
     * ---------------------------------- */

    private void initializeTableColumns() {
        colOrderDate.setCellValueFactory(new PropertyValueFactory<>("orderDate"));
        colDNI.setCellValueFactory(order -> getCustomerProperty(order.getValue(), "dni"));
        colNames.setCellValueFactory(order -> getCustomerProperty(order.getValue(), "names"));
        colSurnames.setCellValueFactory(order -> getCustomerProperty(order.getValue(), "surnames"));

        colQuantity.setCellValueFactory(order -> getQuantityProperty(order.getValue()));
        colTotal.setCellValueFactory(order -> getTotalProperty(order.getValue()));
    }

    private void initializeEventHandlers() {
        dateFromPicker.valueProperty().addListener((obs, oldDate, newDate) -> filterOrders());
        dateToPicker.valueProperty().addListener((obs, oldDate, newDate) -> filterOrders());
        btnReload.setOnAction(event -> fetchOrderRecords());
    }

    private void filterOrders() {
        LocalDate startDate = dateFromPicker.getValue();
        LocalDate endDate = dateToPicker.getValue();

        if (startDate != null && endDate != null) {
            List<OrderModel> filteredOrders = orders.stream()
                .filter(order -> {
                    LocalDate orderDate = LocalDate.parse(order.getFecha());
                    return !orderDate.isBefore(startDate) && !orderDate.isAfter(endDate);
                })
                .collect(Collectors.toList());

            setOrdersList(filteredOrders);
        }
    }

    private void setOrdersList(List<OrderModel> orders) {
        tblOrders.getItems().setAll(orders);
    }

    private SimpleStringProperty getCustomerProperty(OrderModel order, String property) {
        UserModel customer = order.getCliente();

        return switch (property) {
            case "dni" -> new SimpleStringProperty(customer.getDni());
            case "names" -> new SimpleStringProperty(customer.getNombres());
            case "surnames" -> new SimpleStringProperty(customer.getSurnames());
            default -> new SimpleStringProperty("");
        };
    }

    private ObservableValue<Integer> getQuantityProperty(OrderModel order) {
        ArrayList<OrderDetailModel> details = order.getDetalles();
        int quantity = details.stream().mapToInt(OrderDetailModel::getCantidad).sum();
        return new SimpleIntegerProperty(quantity).asObject();
    }

    private ObservableValue<Double> getTotalProperty(OrderModel order) {
        ArrayList<OrderDetailModel> details = order.getDetalles();
        double total = details.stream().mapToDouble(OrderDetailModel::getTotal).sum();
        return new SimpleDoubleProperty(total).asObject();
    }

}
