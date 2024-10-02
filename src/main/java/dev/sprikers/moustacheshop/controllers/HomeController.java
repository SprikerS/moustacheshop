package dev.sprikers.moustacheshop.controllers;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.Set;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;

import dev.sprikers.moustacheshop.constants.PathViews;
import dev.sprikers.moustacheshop.enums.UserRole;
import dev.sprikers.moustacheshop.models.UserModel;
import dev.sprikers.moustacheshop.utils.UserSession;

public class HomeController implements Initializable {

    private final Map<String, Parent> pageCache = new HashMap<>();
    private final UserModel user = UserSession.getInstance().getUserModel();

    @FXML
    private Label lblDni, lblEmail, lblMaternalSurname, lblNames, lblPaternalSurname;

    @FXML
    private AnchorPane ap;

    @FXML
    private BorderPane bp;

    @FXML
    private Button btnUserModule;

    @FXML
    private VBox sidebarVBox;

    @FXML
    void viewHome() {
        bp.setCenter(ap);
    }

    @FXML
    void viewSettings() {
        loadView(PathViews.SETTINGS);
    }

    @FXML
    void viewUsers() {
        loadView(PathViews.USERS);
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        loadUserInfo();
    }

    private void loadUserInfo() {
        Set<String> allowedRoles = Set.of(UserRole.ADMIN.getRole(), UserRole.SUPERUSER.getRole());
        if (user.getRoles().stream().noneMatch(allowedRoles::contains)) {
            sidebarVBox.getChildren().remove(btnUserModule);
        }

        lblNames.setText(user.getNames());
        lblPaternalSurname.setText(user.getPaternalSurname());
        lblMaternalSurname.setText(user.getMaternalSurname());
        lblEmail.setText(user.getEmail());
        lblDni.setText(user.getDni());
    }

    private void loadView(String view) {
        Parent root = pageCache.get(view);
        if (root == null) {
            try {
                root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource(view)));
                pageCache.put(view, root);
            } catch (IOException e) {
                System.out.println("Error loading view: " + e.getMessage());
            }
        }
        bp.setCenter(root);
    }

}
