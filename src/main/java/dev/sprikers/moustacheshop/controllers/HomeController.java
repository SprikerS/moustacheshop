package dev.sprikers.moustacheshop.controllers;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;

import dev.sprikers.moustacheshop.constants.PathViews;
import dev.sprikers.moustacheshop.models.UserModel;
import dev.sprikers.moustacheshop.services.AuthService;
import dev.sprikers.moustacheshop.utils.UserSession;

public class HomeController implements Initializable {

    private final AuthService authService = new AuthService();

    @FXML
    private Label lblDni, lblEmail, lblMaternalSurname, lblNames, lblPaternalSurname;

    @FXML
    private AnchorPane ap;

    @FXML
    private BorderPane bp;

    @FXML
    void viewHome(MouseEvent event) {
        System.out.println("center home page");
        bp.setCenter(ap);
    }

    @FXML
    void viewSettings(MouseEvent event) {
        System.out.println("center settings page");
        loadPage(PathViews.SETTINGS);
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        loadUserInfo();
    }

    private void loadPage(String page) {
        Parent root = null;

        try {
            root = FXMLLoader.load(getClass().getResource(page));
            bp.setCenter(root);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
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
