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
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

import dev.sprikers.moustacheshop.components.StepNavigator;
import dev.sprikers.moustacheshop.utils.ReniecFetch;

public class OrderController implements Initializable {

    private final StepNavigator stepNavigator = new StepNavigator();

    @FXML
    private Button btnNextTwo, btnNextThree, btnPrevOne, btnPrevTwo, btnSubmit;

    @FXML
    private Button btnReloadProducts;

    @FXML
    private HBox hbProductSpinner;

    @FXML
    private Region btnReniec;

    @FXML
    private Tab tabOne, tabTwo, tabThree;

    @FXML
    private TextField txtDNI, txtMaternalSurname, txtNames, txtPaternalSurname, txtSearchProducts;

    @FXML
    private VBox stepOne, stepTwo, stepThree;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        ReniecFetch.configureFields(btnReniec, txtDNI, txtNames, txtPaternalSurname, txtMaternalSurname);

        stepNavigator.setSteps(List.of(stepOne, stepTwo, stepThree));
        stepNavigator.setTabs(List.of(tabOne, tabTwo, tabThree));

        stepNavigator.firstStep(btnNextTwo);
        stepNavigator.addStep(btnPrevOne, btnNextThree);
        stepNavigator.lastStep(btnPrevTwo);
    }

}
