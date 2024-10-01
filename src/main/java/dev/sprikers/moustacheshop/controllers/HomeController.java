package dev.sprikers.moustacheshop.controllers;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;

import dev.sprikers.moustacheshop.application.Main;
import dev.sprikers.moustacheshop.constants.PathViews;
import dev.sprikers.moustacheshop.models.UserModel;
import dev.sprikers.moustacheshop.services.AuthService;
import dev.sprikers.moustacheshop.utils.JwtPreferencesManager;
import dev.sprikers.moustacheshop.utils.UserSession;

public class HomeController implements Initializable {

    private final AuthService authService = new AuthService();

    @FXML
    private Label lblDni, lblEmail, lblMaternalSurname, lblNames, lblPaternalSurname;

    @FXML
    void btnLogout(MouseEvent event) {
        try {
            JwtPreferencesManager.removeJwt();
            Main.changeStage(PathViews.AUTH, "Moustache Shop", event);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        loadUserInfo();
    }

    private void loadUserInfo() {
        try {
            authService.loadUserInfo();
            UserModel user = UserSession.getInstance().getUserModel();

            lblNames.setText(user.getNames());
            lblPaternalSurname.setText(user.getPaternalSurname());
            lblMaternalSurname.setText(user.getMaternalSurname());
            lblEmail.setText(user.getEmail());
            lblDni.setText(user.getDni());
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}
