package dev.sprikers.moustacheshop.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;

import dev.sprikers.moustacheshop.application.Main;
import dev.sprikers.moustacheshop.constants.PathViews;
import dev.sprikers.moustacheshop.services.AuthService;
import dev.sprikers.moustacheshop.utils.AlertManager;

public class AuthController {

    private final AuthService authService = new AuthService();

    @FXML
    private TextField txtEmail, txtPass;

    @FXML
    void onLogin(MouseEvent event) {
        String email = txtEmail.getText().trim().toLowerCase();
        String password = txtPass.getText().trim();

        if (email.isEmpty() || password.isEmpty()) {
            AlertManager.showErrorMessage("Por favor, complete todos los campos");
            return;
        }

        try {
            authService.login(email, password);
            Main.changeStage(PathViews.HOME, "Moustache Shop", event);
        } catch (Exception e) {
            AlertManager.showErrorMessage(e.getMessage());
        }
    }

}
