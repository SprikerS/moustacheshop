package dev.sprikers.moustacheshop.controllers;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;

import dev.sprikers.moustacheshop.utils.DatePickerFormatter;

public class OrderRecordsController implements Initializable {

    @FXML
    private DatePicker dateFromPicker;

    @FXML
    private DatePicker dateToPicker;

    @FXML
    private Label lblFromDate;

    @FXML
    private Label lblToDate;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        DatePickerFormatter.configureDatePicker(dateFromPicker, lblFromDate);
        DatePickerFormatter.configureDatePicker(dateToPicker, lblToDate);
    }

}
