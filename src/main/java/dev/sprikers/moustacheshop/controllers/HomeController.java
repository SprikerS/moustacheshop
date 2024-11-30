package dev.sprikers.moustacheshop.controllers;

import java.io.IOException;
import java.net.URL;
import java.util.*;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import dev.sprikers.moustacheshop.application.Main;
import dev.sprikers.moustacheshop.components.SidebarButton;
import dev.sprikers.moustacheshop.components.SidebarButtonController;
import dev.sprikers.moustacheshop.constants.PathComponents;
import dev.sprikers.moustacheshop.constants.PathSVG;
import dev.sprikers.moustacheshop.constants.PathViews;
import dev.sprikers.moustacheshop.enums.UserRole;
import dev.sprikers.moustacheshop.models.SummaryModel;
import dev.sprikers.moustacheshop.models.UserBindingModel;
import dev.sprikers.moustacheshop.services.HomeService;
import dev.sprikers.moustacheshop.utils.*;

public class HomeController implements Initializable {

    private final Map<String, Parent> pageCache = new HashMap<>();
    private final UserBindingModel user = UserSession.getInstance().getUserBindingModel();
    private final List<SidebarButtonController> buttonControllers = new ArrayList<>();
    private final WindowDragHandler windowDragHandler = new WindowDragHandler();
    private static final HomeService homeService = new HomeService();

    @FXML
    private AnchorPane ap;

    @FXML
    private BorderPane bp;

    @FXML
    private Button btnLogout, btnReload;

    @FXML
    private HBox hbTitleBar;

    @FXML
    private ImageView btnClose, btnMinimize;

    @FXML
    private Label lblDate, lblTime, sbDNI, sbNames;

    @FXML
    private Label lblUserActives, lblUserAdmins, lblUserClients, lblUserEmployees, lblUserInactives, lblUsersTotal;

    @FXML
    private Label lblProductsActives, lblProductsInactives, lblProductsTotal;

    @FXML
    private Label lblOrdersActive, lblOrdersClient, lblOrdersEmployees, lblOrdersInactive, lblOrdesTotal;

    @FXML
    private VBox sidebarVBox;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        DateTimeUpdater.configureDateTimeLabels(lblDate, lblTime);

        btnLogout.setOnMouseClicked(this::logout);
        btnClose.setOnMouseClicked(event -> System.exit(0));
        btnMinimize.setOnMouseClicked(event -> ((Stage) btnMinimize.getScene().getWindow()).setIconified(true));
        btnReload.setOnAction(event -> loadSummaries());

        Platform.runLater(() -> {
            loadSidebar();
            windowDragHandler.enableWindowDragging(hbTitleBar);
        });

        bindUserInfoToSidebar();
        Platform.runLater(this::loadSummaries);
    }

    private void loadSummaries() {
        homeService.fetchSummaries()
            .thenAccept(user -> Platform.runLater(() -> setCurrentSummary(user)))
            .exceptionally(ex -> {
                AlertManager.showErrorMessage("Error loading summaries: " + ex.getMessage());
                return null;
            });
    }

    private void setCurrentSummary(SummaryModel summary) {
        if (summary != null) {
            lblUsersTotal.setText(String.valueOf(summary.getUsersTotal()));
            lblUserActives.setText(String.valueOf(summary.getUsersActive()));
            lblUserInactives.setText(String.valueOf(summary.getUsersInactive()));
            lblUserClients.setText(String.valueOf(summary.getUsersCustomers()));
            lblUserEmployees.setText(String.valueOf(summary.getUsersEmployees()));
            lblUserAdmins.setText(String.valueOf(summary.getUsersAdmins()));

            lblProductsTotal.setText(String.valueOf(summary.getProductsTotal()));
            lblProductsActives.setText(String.valueOf(summary.getProdutcsActive()));
            lblProductsInactives.setText(String.valueOf(summary.getProdutcsInactive()));

            lblOrdesTotal.setText(String.valueOf(summary.getOrdersTotal()));
            lblOrdersClient.setText(summary.getTopCustomer());
            lblOrdersEmployees.setText(summary.getTopEmployee());
        }
    }

    /* ----------------------------------
     * Sección de configuración de la UI
     * ---------------------------------- */

    private void loadSidebar() {
        List<SidebarButton> sidebarButtons = getSidebarButtons();

        int i = 0;
        for (SidebarButton sb : sidebarButtons) {
            FXMLLoader fxml = new FXMLLoader();
            fxml.setLocation(getClass().getResource(PathComponents.SIDEBAR_BUTTON));
            try {
                Button button = fxml.load();
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

        SidebarButton sbHome = new SidebarButton(PathSVG.LAYOUT_GRID, "Inicio", () -> setView(PathViews.HOME));
        SidebarButton sbOrderRecords = new SidebarButton(PathSVG.FILE_CLOCK, "Historial de ventas", () -> setView(PathViews.ORDER_RECORDS));
        SidebarButton sbOrders = new SidebarButton(PathSVG.NOTEBOOK_TEXT, "Ordenes", () -> setView(PathViews.ORDERS));
        SidebarButton sbUsers = new SidebarButton(PathSVG.USERS_ROUND, "Usuarios", () -> setView(PathViews.USERS));
        SidebarButton sbProducts = new SidebarButton(PathSVG.PACKAGE, "Productos", () -> setView(PathViews.PRODUCTS));
        SidebarButton sbSettings = new SidebarButton(PathSVG.BOLT, "Configuración", () -> setView(PathViews.SETTINGS));

        list.add(sbHome);
        list.add(sbOrderRecords);
        list.add(sbOrders);
        list.add(sbProducts);

        Set<String> allowedRoles = Set.of(UserRole.ADMIN.getRole(), UserRole.SUPERUSER.getRole());
        if (user.getRoles().stream().anyMatch(allowedRoles::contains)) {
            list.add(sbUsers);
        }

        list.add(sbSettings);
        return list;
    }

    private void logout(MouseEvent event) {
        boolean confirmed = AlertManager.showConfirmation("¿Estás seguro de que deseas cerrar sesión?", Alert.AlertType.WARNING);
        btnLogout.getParent().requestFocus();
        if (!confirmed) return;

        try {
            JwtPreferencesManager.removeJwt();
            Main.changeStage(PathViews.AUTH, event);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
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

    private void bindUserInfoToSidebar() {
        sbNames.textProperty().bind(user.getNames());
        sbDNI.textProperty().bind(user.getDni());
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
