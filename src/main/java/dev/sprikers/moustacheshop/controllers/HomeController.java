package dev.sprikers.moustacheshop.controllers;

import java.io.IOException;
import java.net.URL;
import java.util.*;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import dev.sprikers.moustacheshop.components.SidebarButton;
import dev.sprikers.moustacheshop.components.SidebarButtonController;
import dev.sprikers.moustacheshop.constants.PathComponents;
import dev.sprikers.moustacheshop.constants.PathImages;
import dev.sprikers.moustacheshop.constants.PathViews;
import dev.sprikers.moustacheshop.enums.UserRole;
import dev.sprikers.moustacheshop.models.UserModel;
import dev.sprikers.moustacheshop.utils.UserSession;

public class HomeController implements Initializable {

    private final Map<String, Parent> pageCache = new HashMap<>();
    private final UserModel user = UserSession.getInstance().getUserModel();
    private final List<SidebarButtonController> buttonControllers = new ArrayList<>();

    @FXML
    private Label lblDni, lblEmail, lblMaternalSurname, lblNames, lblPaternalSurname;

    @FXML
    private AnchorPane ap;

    @FXML
    private BorderPane bp;

    @FXML
    private VBox sidebarVBox;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        loadSidebar();
        loadUserInfo();
    }

    private void loadSidebar() {
        List<SidebarButton> sidebarButtons = getSidebarButtons();

        int i = 0;
        for (SidebarButton sb : sidebarButtons) {
            FXMLLoader fxml = new FXMLLoader();
            fxml.setLocation(getClass().getResource(PathComponents.SIDEBAR_BUTTON));
            try {
                HBox button = fxml.load();
                SidebarButtonController controller = fxml.getController();
                controller.setData(sb);
                if (i == 0) controller.activateButton();
                sidebarVBox.getChildren().add(button);
                buttonControllers.add(controller);
            } catch (IOException e) {
                System.out.println("Error loading sidebar button: " + e.getMessage());
            }
            i++;
        }
    }

    private List<SidebarButton> getSidebarButtons() {
        List<SidebarButton> list = new ArrayList<>();

        SidebarButton sbHome = new SidebarButton(PathImages.HOUSE, "Inicio", () -> setView(PathViews.HOME));
        SidebarButton sbUsers = new SidebarButton(PathImages.USERS, "Usuarios", () -> setView(PathViews.USERS));
        SidebarButton sbSettings = new SidebarButton(PathImages.SETTINGS, "Configuración", () -> setView(PathViews.SETTINGS));

        list.add(sbHome);

        Set<String> allowedRoles = Set.of(UserRole.ADMIN.getRole(), UserRole.SUPERUSER.getRole());
        if (user.getRoles().stream().anyMatch(allowedRoles::contains)) {
            list.add(sbUsers);
        }

        list.add(sbSettings);
        return list;
    }

    private void setView(String view) {
        resetActiveButtons();

        if (view.equals(PathViews.HOME)) {
            bp.setCenter(ap);
        } else {
            loadView(view);
        }
    }

    private void resetActiveButtons() {
        for (SidebarButtonController controller : buttonControllers) {
            controller.deactivateButton();
        }
    }

    private void loadUserInfo() {
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
