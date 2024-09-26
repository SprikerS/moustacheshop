package dev.sprikers.moustacheshop.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;

import dev.sprikers.moustacheshop.application.Main;
import dev.sprikers.moustacheshop.constants.PathViews;
import dev.sprikers.moustacheshop.services.AuthService;

public class AuthController {

    private final AuthService authService = new AuthService();

    @FXML
    private TextField txtEmail, txtPass;

    @FXML
    void onLogin(MouseEvent event) {
        String email = txtEmail.getText().trim().toLowerCase();
        String password = txtPass.getText().trim();

        if (email.isEmpty() || password.isEmpty()) {
            showErrorMessage("Por favor, complete todos los campos");
            return;
        }

        try {
            authService.login(email, password);
            Main.changeStage(PathViews.HOME, "Moustache Shop", event);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    private void showErrorMessage(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error de Autenticación");
        alert.setHeaderText("No se pudo iniciar sesión");
        alert.setContentText(message);
        alert.showAndWait();
    }

}
