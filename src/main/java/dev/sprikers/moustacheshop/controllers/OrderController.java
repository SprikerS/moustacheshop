package dev.sprikers.moustacheshop.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;

public class OrderController {

    @FXML
    private Button btnCleanCustomer, btnFetchCustomer, btnOrderSubmit, btnReloadProducts;

    @FXML
    private HBox hbProductSpinner;

    @FXML
    private TextField txtDNI, txtMaternalSurname, txtNames, txtPaternalSurname, txtSearchCustomer, txtSearchProducts;

}
