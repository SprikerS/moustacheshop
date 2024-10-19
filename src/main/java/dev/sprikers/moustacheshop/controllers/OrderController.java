package dev.sprikers.moustacheshop.controllers;

import com.jfoenix.controls.JFXButton;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;

public class OrderController {

    @FXML
    private JFXButton btnCleanCustomer, btnFetchCustomer, btnOrderSubmit, btnReloadProducts;

    @FXML
    private HBox hbProductSpinner;

    @FXML
    private TextField txtDNI, txtMaternalSurname, txtNames, txtPaternalSurname, txtSearchCustomer, txtSearchProducts;

}
