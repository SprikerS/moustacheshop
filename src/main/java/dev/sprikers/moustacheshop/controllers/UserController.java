package dev.sprikers.moustacheshop.controllers;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import com.jfoenix.controls.JFXCheckBox;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import org.controlsfx.control.CheckComboBox;

import dev.sprikers.moustacheshop.components.ToasterController;
import dev.sprikers.moustacheshop.helpers.PasswordToggleManager;
import dev.sprikers.moustacheshop.models.ReniecModel;
import dev.sprikers.moustacheshop.services.UserService;
import dev.sprikers.moustacheshop.utils.AlertManager;
import dev.sprikers.moustacheshop.utils.TextFieldFormatter;

public class UserController implements Initializable {

    private final ToasterController toaster = new ToasterController();
    private final UserService userService = new UserService();

    @FXML
    private Button btnClean, btnDelete, btnSubmit;

    @FXML
    private CheckComboBox<String> chkcbRoles;

    @FXML
    private HBox hbProductSpinner;

    @FXML
    private ImageView imgToggleEye, imgBtnFetchReniec;

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

        TextFieldFormatter.applyIntegerFormat(txtDNI, 8);
        TextFieldFormatter.applyIntegerFormat(txtPhone, 9);

        imgBtnFetchReniec.visibleProperty().bind(txtDNI.textProperty().length().isEqualTo(8));

        imgBtnFetchReniec.setOnMouseClicked(event -> handleFetchReniec());
    }

    private List<String> getSelectedRoles() {
        return new ArrayList<>(chkcbRoles.getCheckModel().getCheckedItems());
    }

    private void handleFetchReniec() {
        String dni = txtDNI.getText().trim();
        userService.reniec(dni)
            .thenAccept(this::populateFieldsReniec)
            .exceptionally(ex -> {
                AlertManager.showErrorMessage("No se pudo obtener los datos de reniec: %s".formatted(ex.getCause().getMessage()));
                return null;
            });
    }

    private void populateFieldsReniec(ReniecModel reniec) {
        toaster.showInfo("Datos obtenidos de la RENIEC con éxito");
        Platform.runLater(() -> {
            txtMaternalSurname.setText(reniec.getMaternalSurname());
            txtNames.setText(reniec.getNames());
            txtPaternalSurname.setText(reniec.getPaternalSurname());
            txtEmail.requestFocus();
        });
    }

}
