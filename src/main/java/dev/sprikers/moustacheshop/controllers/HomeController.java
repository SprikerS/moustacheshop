package dev.sprikers.moustacheshop.controllers;

import java.io.IOException;
import java.net.URL;
import java.util.*;

import javafx.concurrent.Task;
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
import dev.sprikers.moustacheshop.constants.PathSVG;
import dev.sprikers.moustacheshop.constants.PathViews;
import dev.sprikers.moustacheshop.enums.UserRole;
import dev.sprikers.moustacheshop.models.UserModel;
import dev.sprikers.moustacheshop.utils.AlertManager;
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

        SidebarButton sbHome = new SidebarButton(PathSVG.HOUSE, "Inicio", () -> setView(PathViews.HOME));
        SidebarButton sbUsers = new SidebarButton(PathSVG.USERS_ROUND, "Usuarios", () -> setView(PathViews.USERS));
        SidebarButton sbProducts = new SidebarButton(PathSVG.PACKAGE, "Productos", () -> setView(PathViews.PRODUCTS));
        SidebarButton sbSettings = new SidebarButton(PathSVG.BOLT, "Configuración", () -> setView(PathViews.SETTINGS));

        list.add(sbHome);
        list.add(sbProducts);

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
        Parent cachedRoot = pageCache.get(view);
        if (cachedRoot != null) {
            bp.setCenter(cachedRoot);
            return;
        }

        Task<Parent> loadTask = new Task<>() {
            @Override
            protected Parent call() throws Exception {
                return FXMLLoader.load(Objects.requireNonNull(getClass().getResource(view)));
            }
        };

        loadTask.setOnSucceeded(event -> {
            Parent loadedRoot = loadTask.getValue();
            pageCache.put(view, loadedRoot);
            bp.setCenter(loadedRoot);
        });

        loadTask.setOnFailed(event -> {
            Throwable error = loadTask.getException();
            AlertManager.showErrorMessage("Error loading view: " + error.getMessage());
        });

        new Thread(loadTask).start();
    }

}
