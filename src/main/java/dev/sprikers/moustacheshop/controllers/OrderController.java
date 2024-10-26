package dev.sprikers.moustacheshop.controllers;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Tab;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import dev.sprikers.moustacheshop.components.StepNavigator;

public class OrderController implements Initializable {

    private final StepNavigator stepNavigator = new StepNavigator();

    @FXML
    private Button btnNextTwo, btnNextThree, btnPrevOne, btnPrevTwo, btnSubmit;

    @FXML
    private Button btnCleanCustomer, btnFetchCustomer, btnReloadProducts;

    @FXML
    private HBox hbProductSpinner;

    @FXML
    private Tab tabOne, tabTwo, tabThree;

    @FXML
    private TextField txtDNI, txtMaternalSurname, txtNames, txtPaternalSurname, txtSearchCustomer, txtSearchProducts;

    @FXML
    private VBox stepOne, stepTwo, stepThree;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        stepNavigator.setSteps(List.of(stepOne, stepTwo, stepThree));
        stepNavigator.setTabs(List.of(tabOne, tabTwo, tabThree));

        stepNavigator.firstStep(btnNextTwo);
        stepNavigator.addStep(btnPrevOne, btnNextThree);
        stepNavigator.lastStep(btnPrevTwo);
    }

}
