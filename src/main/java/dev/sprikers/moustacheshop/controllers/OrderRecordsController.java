package dev.sprikers.moustacheshop.controllers;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import dev.sprikers.moustacheshop.components.Toaster;
import dev.sprikers.moustacheshop.models.OrderDetailModel;
import dev.sprikers.moustacheshop.models.OrderModel;
import dev.sprikers.moustacheshop.models.UserModel;
import dev.sprikers.moustacheshop.services.OrderService;
import dev.sprikers.moustacheshop.utils.DatePickerFormatter;

public class OrderRecordsController implements Initializable {

    private static final OrderService orderService = new OrderService();

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

        initializeTableColumns();
        loadOrders();
    }

    private void loadOrders() {
        orderService.getAll()
            .thenAccept(orders -> {
                tblOrders.getItems().clear();
                tblOrders.getItems().addAll(orders);
                System.out.println(orders);
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

    private SimpleStringProperty getCustomerProperty(OrderModel order, String property) {
        UserModel customer = order.getCustomer();

        return switch (property) {
            case "dni" -> new SimpleStringProperty(customer.getDni());
            case "names" -> new SimpleStringProperty(customer.getNames());
            case "surnames" -> new SimpleStringProperty(customer.getSurnames());
            default -> new SimpleStringProperty("");
        };
    }

    private ObservableValue<Integer> getQuantityProperty(OrderModel order) {
        ArrayList<OrderDetailModel> details = order.getDetails();
        int quantity = details.stream().mapToInt(OrderDetailModel::getQuantity).sum();
        return new SimpleIntegerProperty(quantity).asObject();
    }

    private ObservableValue<Double> getTotalProperty(OrderModel order) {
        ArrayList<OrderDetailModel> details = order.getDetails();
        double total = details.stream().mapToDouble(OrderDetailModel::getTotal).sum();
        return new SimpleDoubleProperty(total).asObject();
    }

}
