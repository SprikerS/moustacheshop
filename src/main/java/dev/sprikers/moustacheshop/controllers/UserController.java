package dev.sprikers.moustacheshop.controllers;

import java.net.URL;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import com.jfoenix.controls.JFXCheckBox;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import org.controlsfx.control.CheckComboBox;

import dev.sprikers.moustacheshop.components.Toaster;
import dev.sprikers.moustacheshop.dto.UserRequest;
import dev.sprikers.moustacheshop.enums.UserRole;
import dev.sprikers.moustacheshop.helpers.PasswordToggleManager;
import dev.sprikers.moustacheshop.models.ReniecModel;
import dev.sprikers.moustacheshop.models.UserModel;
import dev.sprikers.moustacheshop.services.UserService;
import dev.sprikers.moustacheshop.utils.AlertManager;
import dev.sprikers.moustacheshop.utils.SpinnerLoader;
import dev.sprikers.moustacheshop.utils.TextFieldFormatter;

public class UserController implements Initializable {

    private final UserService userService = new UserService();

    private final Map<String, String> roleTextToValueMap = new LinkedHashMap<>();
    private final SpinnerLoader spinnerReniec = new SpinnerLoader();

    @FXML
    private Button btnClean, btnDelete, btnSubmit;

    @FXML
    private CheckComboBox<String> chkcbRoles;

    @FXML
    private HBox hbProductSpinner;

    @FXML
    private ImageView btnToggleEye;

    @FXML
    private JFXCheckBox chkActive;

    @FXML
    private PasswordField txtHiddenPass;

    @FXML
    private Region btnReniec;

    @FXML
    private TextField txtDNI, txtEmail, txtMaternalSurname, txtNames, txtPaternalSurname, txtPhone, txtVisiblePass;

    @FXML
    private ToggleButton toggleDisplayPass;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        spinnerReniec.setNode(btnReniec);
        PasswordToggleManager.configureVisibility(txtHiddenPass, txtVisiblePass, toggleDisplayPass, btnToggleEye);

        Platform.runLater(() -> {
            for (UserRole role : UserRole.values()) {
                if (role != UserRole.SUPERUSER) roleTextToValueMap.put(role.getText(), role.getRole());
            }

            chkcbRoles.getItems().addAll(roleTextToValueMap.keySet());
            chkcbRoles.getCheckModel().check(0);
        });

        TextFieldFormatter.applyIntegerFormat(txtDNI, 8);
        TextFieldFormatter.applyIntegerFormat(txtPhone, 9);

        btnReniec.visibleProperty().bind(txtDNI.textProperty().length().isEqualTo(8));

        btnReniec.setOnMouseClicked(event -> handleFetchReniec());
        btnSubmit.setOnMouseClicked(event -> handleSubmit());
    }

    private List<String> getSelectedRoles() {
        return chkcbRoles.getCheckModel().getCheckedItems().stream()
            .map(roleTextToValueMap::get)
            .collect(Collectors.toList());
    }

    private void handleFetchReniec() {
        spinnerReniec.start();
        String dni = txtDNI.getText().trim();
        userService.reniec(dni)
            .thenAccept(this::populateFieldsReniec)
            .exceptionally(ex -> {
                AlertManager.showErrorMessage("No se pudo obtener los datos de reniec: %s".formatted(ex.getCause().getMessage()));
                return null;
            });
    }

    private void populateFieldsReniec(ReniecModel reniec) {
        spinnerReniec.stop();
        Toaster.showInfo("Datos obtenidos de la RENIEC con éxito");
        Platform.runLater(() -> {
            txtMaternalSurname.setText(reniec.getMaternalSurname());
            txtNames.setText(reniec.getNames());
            txtPaternalSurname.setText(reniec.getPaternalSurname());
            if (txtEmail.getText().isEmpty()) txtEmail.requestFocus();
        });
    }

    private void handleSubmit() {
        String dni = txtDNI.getText().trim();
        String names = txtNames.getText().trim();
        String paternalSurname = txtPaternalSurname.getText().trim();
        String maternalSurname = txtMaternalSurname.getText().trim();
        String email = txtEmail.getText().trim();
        String password = txtHiddenPass.getText().trim();
        String phoneText = txtPhone.getText().trim();
        Integer phone = phoneText.isEmpty() ? null : Integer.parseInt(phoneText);
        List<String> roles = getSelectedRoles();

        if (dni.isEmpty() || names.isEmpty() || paternalSurname.isEmpty() || maternalSurname.isEmpty() || email.isEmpty() || password.isEmpty() || roles.isEmpty()) {
            Toaster.showWarning("Por favor, complete todos los campos");
            return;
        }

        UserRequest userRequest = new UserRequest(dni, names, paternalSurname, maternalSurname, email, password, phone, roles);
        saveOrUpdateUser(userRequest);
    }

    private void saveOrUpdateUser(UserRequest userRequest) {
        CompletableFuture<UserModel> userFuture = userService.register(userRequest);

        userFuture
            .thenAccept(user -> Platform.runLater(() -> {
                String messageToast = "Usuario %s registrado".formatted(user.getNames());
                Toaster.showSuccess(messageToast);
            }))
            .exceptionally(ex -> {
                Platform.runLater(() -> AlertManager.showErrorMessage("Error al registrar el usuario: %s".formatted(ex.getCause().getMessage())));
                return null;
            });
    }

}
