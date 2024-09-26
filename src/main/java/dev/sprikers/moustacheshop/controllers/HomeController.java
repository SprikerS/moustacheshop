package dev.sprikers.moustacheshop.controllers;

import javafx.fxml.FXML;
import javafx.scene.input.MouseEvent;

import dev.sprikers.moustacheshop.application.Main;
import dev.sprikers.moustacheshop.constants.PathViews;
import dev.sprikers.moustacheshop.utils.JwtPreferencesManager;

public class HomeController {

    @FXML
    void btnLogout(MouseEvent event) {
        try {
            JwtPreferencesManager.removeJwt();
            Main.changeStage(PathViews.AUTH, "Moustache Shop", event);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

}
