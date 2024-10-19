package dev.sprikers.moustacheshop.controllers;

import java.net.URL;
import java.util.ResourceBundle;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXCheckBox;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.image.ImageView;

import dev.sprikers.moustacheshop.application.Main;
import dev.sprikers.moustacheshop.constants.PathViews;
import dev.sprikers.moustacheshop.helpers.PasswordToggleManager;
import dev.sprikers.moustacheshop.services.AuthService;
import dev.sprikers.moustacheshop.utils.AlertManager;
import dev.sprikers.moustacheshop.utils.JwtPreferencesManager;

public class AuthController implements Initializable {

    private final AuthService authService = new AuthService();

    @FXML
    private TextField txtEmail, txtVisiblePass;

    @FXML
    private PasswordField txtHiddenPass;

    @FXML
    private JFXCheckBox chkRemember;

    @FXML
    private ToggleButton toggleDisplayPass;

    @FXML
    private ImageView imgToggleEye;

    @FXML
    private JFXButton btnLogin;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        PasswordToggleManager.configureVisibility(txtHiddenPass, txtVisiblePass, toggleDisplayPass, imgToggleEye);
        btnLogin.setOnAction(this::handleLogin);
    }

    private void handleLogin(ActionEvent event) {
        String email = txtEmail.getText().trim().toLowerCase();
        String password = txtHiddenPass.getText().trim();

        if (email.isEmpty() || password.isEmpty()) {
            AlertManager.showErrorMessage("Por favor, complete todos los campos");
            return;
        }

        updateLoginButtonState(true);

        authService.loginAsync(email, password)
            .thenAccept(user -> {
                if (chkRemember.isSelected())
                    JwtPreferencesManager.setJwt(user.getToken());

                Platform.runLater(() -> navigateToHome(event));
            })
            .exceptionally(ex -> {
                Platform.runLater(() -> {
                    updateLoginButtonState(false);
                    AlertManager.showErrorMessage("Error al iniciar sesión: \n" + ex.getCause().getMessage());
                });
                return null;
            });
    }

    private void navigateToHome(ActionEvent event) {
        try {
            Main.changeStage(PathViews.HOME, event);
        } catch (Exception e) {
            AlertManager.showErrorMessage(e.getMessage());
        }
    }

    private void updateLoginButtonState(boolean isLoading) {
        String buttonText = isLoading ? "Cargando..." : "Iniciar sesión";
        btnLogin.setText(buttonText);
        btnLogin.setDisable(isLoading);
    }

}
