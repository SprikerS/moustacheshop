package dev.sprikers.moustacheshop.controllers;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXCheckBox;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import org.controlsfx.control.CheckComboBox;

import dev.sprikers.moustacheshop.helpers.PasswordToggleManager;

public class UserController implements Initializable {

    @FXML
    private CheckComboBox<String> chkcbRoles;

    @FXML
    private HBox hbProductSpinner;

    @FXML
    private ImageView imgToggleEye;

    @FXML
    private JFXButton btnClean, btnDelete, btnSubmit;

    @FXML
    private JFXCheckBox chkActive;

    @FXML
    private PasswordField txtHiddenPass;

    @FXML
    private TextField txtDNI, txtEmail, txtMaternalSurname, txtNames, txtPaternalSurname, txtPhone, txtVisiblePass;

    @FXML
    private ToggleButton toggleDisplayPass;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        PasswordToggleManager.configureVisibility(txtHiddenPass, txtVisiblePass, toggleDisplayPass, imgToggleEye);

        ObservableList<String> validRoles = FXCollections.observableArrayList(
            "Cliente",
            "Trabajador",
            "Administrador"
        );

        chkcbRoles.getItems().addAll(validRoles);
        chkcbRoles.getCheckModel().check(0);
    }

    private List<String> getSelectedRoles() {
        return new ArrayList<>(chkcbRoles.getCheckModel().getCheckedItems());
    }

}
