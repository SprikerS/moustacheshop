package dev.sprikers.moustacheshop.controllers;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.regex.Pattern;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;

import dev.sprikers.moustacheshop.components.ToasterController;
import dev.sprikers.moustacheshop.dto.ChangePasswordRequest;
import dev.sprikers.moustacheshop.dto.UpdateProfileRequest;
import dev.sprikers.moustacheshop.helpers.PasswordToggleManager;
import dev.sprikers.moustacheshop.models.UserModel;
import dev.sprikers.moustacheshop.services.UserService;
import dev.sprikers.moustacheshop.utils.*;

public class SettingsController implements Initializable {

    private final Map<Button, Tab> buttonTabMap = new HashMap<>();
    private final ToasterController toaster = new ToasterController();
    private final UserModel user = UserSession.getInstance().getUserModel();
    private final UserService userService = new UserService();

    @FXML
    private Button btnTabAccount, btnTabPassword, btnSubmitAccount, btnSubmitPass;

    @FXML
    private ImageView eyeNew, eyeNewConf, eyeOld;

    @FXML
    private PasswordField txtPassNew, txtPassNewConf, txtPassOld;

    @FXML
    private Tab tabAccount, tabPassword;

    @FXML
    private TabPane tabProfile;

    @FXML
    private TextField txtDNI, txtEmail, txtMaternalSurname, txtNames, txtPaternalSurname, txtPhone, textNew, textNewConf, textOld;

    @FXML
    private ToggleButton toggleNew, toggleNewConf, toggleOld;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        initializeButtonTabMap();
        configurePasswordVisibility();
        populateUserDetails();

        TextFieldFormatter.applyIntegerFormat(txtDNI, 8);
        TextFieldFormatter.applyIntegerFormat(txtPhone, 9);

        btnSubmitAccount.setOnAction(event -> handleUpdateProfile());
        btnSubmitPass.setOnAction(event -> handleChangePassword());
    }

    private void handleUpdateProfile() {
        String dni = txtDNI.getText().trim();
        String email = txtEmail.getText().trim();
        String maternalSurname = txtMaternalSurname.getText().trim();
        String names = txtNames.getText().trim();
        String paternalSurname = txtPaternalSurname.getText().trim();

        if (dni.isEmpty() || email.isEmpty() || maternalSurname.isEmpty() || names.isEmpty() || paternalSurname.isEmpty()) {
            AlertManager.showErrorMessage("Por favor, complete todos los campos");
            return;
        }

        String valPhone = txtPhone.getText().trim();
        Integer phone = valPhone.isEmpty() ? null : Integer.parseInt(valPhone);

        UpdateProfileRequest updateProfileRequest = new UpdateProfileRequest(names, paternalSurname, maternalSurname, dni, email, phone);
        userService.update(updateProfileRequest, user.getId())
            .thenAccept(updatedUser -> {
                UserSession.getInstance().setUserModel(updatedUser);
                toaster.showSuccess("Perfil actualizado con éxito");
            })
            .exceptionally(ex -> handleException(ex, "Error al actualizar el perfil"));
    }

    private void handleChangePassword() {
        String passOld = txtPassOld.getText().trim();
        String passNew = txtPassNew.getText().trim();
        String passNewConf = txtPassNewConf.getText().trim();

        if (passOld.isEmpty() || passNew.isEmpty() || passNewConf.isEmpty())  {
            AlertManager.showErrorMessage("Por favor, complete todos los campos");
            return;
        }

        // Regex para validar la contraseña antigua
        String regex = "(?:(?=.*\\d)|(?=.*\\W+))(?![.\\n])(?=.*[A-Z])(?=.*[a-z]).*$";
        Pattern pattern = Pattern.compile(regex);

        if (!pattern.matcher(passOld).matches() || !pattern.matcher(passNew).matches() || !pattern.matcher(passNewConf).matches()) {
            AlertManager.showErrorMessage("Las contraseñas deben tener al menos una letra mayúscula, una letra minúscula y un número.");
            return;
        }

        if (!passNew.equals(passNewConf)) {
            AlertManager.showErrorMessage("La nueva contraseña y su confirmación no coinciden");
            return;
        }

        ChangePasswordRequest changePasswordRequest = new ChangePasswordRequest(user.getEmail(), passOld, passNew);
        userService.changePassword(changePasswordRequest)
            .thenRun(() -> toaster.showSuccess("Contraseña actualizada con éxito"))
            .exceptionally(ex -> handleException(ex, "Error al actualizar la contraseña"));
    }

    private void initializeButtonTabMap() {
        buttonTabMap.put(btnTabAccount, tabAccount);
        buttonTabMap.put(btnTabPassword, tabPassword);

        for (Button button : buttonTabMap.keySet())
            button.setOnAction(this::handleTabButtonAction);
    }

    private void handleTabButtonAction(ActionEvent event) {
        Button selectedButton = (Button) event.getSource();
        tabProfile.getSelectionModel().select(buttonTabMap.get(selectedButton));

        for (Button button : buttonTabMap.keySet())
            button.getStyleClass().remove("tab-active");

        selectedButton.getStyleClass().add("tab-active");
    }

    private void configurePasswordVisibility() {
        PasswordToggleManager.configureVisibility(txtPassOld, textOld, toggleOld, eyeOld);
        PasswordToggleManager.configureVisibility(txtPassNew, textNew, toggleNew, eyeNew);
        PasswordToggleManager.configureVisibility(txtPassNewConf, textNewConf, toggleNewConf, eyeNewConf);
    }

    private void populateUserDetails() {
        txtDNI.setText(user.getDni());
        txtEmail.setText(user.getEmail());
        txtMaternalSurname.setText(user.getMaternalSurname());
        txtNames.setText(user.getNames());
        txtPaternalSurname.setText(user.getPaternalSurname());
        txtPhone.setText(user.getPhoneNumber() != null ? String.valueOf(user.getPhoneNumber()) : "");
    }

    private static Void handleException(Throwable ex, String message) {
        Platform.runLater(() -> AlertManager.showErrorMessage(message + ex.getCause().getMessage()));
        return null;
    }

}
