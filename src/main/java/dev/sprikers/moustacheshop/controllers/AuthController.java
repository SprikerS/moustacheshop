package dev.sprikers.moustacheshop.controllers;

import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;

import com.jfoenix.controls.JFXCheckBox;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;

import dev.sprikers.moustacheshop.application.Main;
import dev.sprikers.moustacheshop.constants.PathImages;
import dev.sprikers.moustacheshop.constants.PathViews;
import dev.sprikers.moustacheshop.models.UserModel;
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

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        txtHiddenPass.textProperty().bindBidirectional(txtVisiblePass.textProperty());

        toggleDisplayPass.selectedProperty().addListener((obs, oldValue, newValue) -> togglePasswordVisibility(newValue));

        txtHiddenPass.textProperty().addListener((obs, oldText, newText) -> {
            toggleDisplayPass.setVisible(!newText.isEmpty());
        });
    }

    private void togglePasswordVisibility(boolean isVisible) {
        txtVisiblePass.setVisible(isVisible);
        txtHiddenPass.setVisible(!isVisible);

        String imagePath = isVisible ? PathImages.EYE : PathImages.EYE_OFF;
        imgToggleEye.setImage(new Image(Objects.requireNonNull(getClass().getResourceAsStream(imagePath))));
    }

    @FXML
    void onLogin(MouseEvent event) {
        String email = txtEmail.getText().trim().toLowerCase();
        String password = txtHiddenPass.getText().trim();

        if (email.isEmpty() || password.isEmpty()) {
            AlertManager.showErrorMessage("Por favor, complete todos los campos");
            return;
        }

        try {
            UserModel user = authService.login(email, password);
            if (chkRemember.isSelected())
                JwtPreferencesManager.setJwt(user.getToken());

            Main.changeStage(PathViews.HOME, "Moustache Shop", event);
        } catch (Exception e) {
            AlertManager.showErrorMessage(e.getMessage());
        }
    }

}
