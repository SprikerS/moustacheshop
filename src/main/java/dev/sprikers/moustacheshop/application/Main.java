package dev.sprikers.moustacheshop.application;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.Event;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Screen;
import javafx.stage.Stage;
import org.burningwave.core.assembler.StaticComponentContainer;

import dev.sprikers.moustacheshop.constants.PathImages;
import dev.sprikers.moustacheshop.constants.PathViews;
import dev.sprikers.moustacheshop.services.AuthService;
import dev.sprikers.moustacheshop.utils.AlertManager;
import dev.sprikers.moustacheshop.utils.JwtPreferencesManager;

public class Main extends Application {

    private static final AuthService authService = new AuthService();

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void init() {
        StaticComponentContainer.JVMInfo.getVersion();
    }

    @Override
    public void start(Stage stage) throws IOException {
        String viewPath = determineViewPath();

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(viewPath));
        Scene scene = new Scene(fxmlLoader.load());

        stage.getIcons().addAll(loadIcons());
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();

        centerOnScreen(stage);
    }

    private String determineViewPath() {
        if (!JwtPreferencesManager.isJWT())
            return PathViews.AUTH;

        try {
            authService.fetchUserInfo();
            return PathViews.HOME;
        } catch (Exception e) {
            handleAuthException(e);
            return PathViews.AUTH;
        }
    }

    private void handleAuthException(Exception e) {
        String errorMessage = e.getMessage();
        boolean isAuthException = errorMessage.contains("Unauthorized");

        if (isAuthException) {
            JwtPreferencesManager.removeJwt();
            AlertManager.showErrorMessage("Su sesión ha expirado, por favor inicie sesión nuevamente");
        } else {
            AlertManager.showErrorMessage(errorMessage, true);
        }
    }

    public static void changeScene(String fxmlFile, Event event) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource(fxmlFile));
        Scene newScene = new Scene(fxmlLoader.load());

        Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        currentStage.setScene(newScene);
    }

    public static void changeStage(String fxmlFile, String title, Event event) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource(fxmlFile));
        Stage newStage = new Stage();
        Scene scene = new Scene(fxmlLoader.load());

        newStage.getIcons().addAll(loadIcons());
        newStage.setScene(scene);
        newStage.setTitle(title);
        newStage.setResizable(false);
        newStage.show();

        centerOnScreen(newStage);
        Platform.runLater(() -> ((Stage) (((Node) event.getSource()).getScene().getWindow())).close());
    }

    private static List<Image> loadIcons() {
        List<Image> icons = new ArrayList<>();
        for (String logoPath : PathImages.ALL_LOGOS) {
            icons.add(new Image(Objects.requireNonNull(Main.class.getResourceAsStream(logoPath))));
        }
        return icons;
    }

    private static void centerOnScreen(Stage stage) {
        Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
        stage.setX((screenBounds.getWidth() - stage.getWidth()) / 2);
        stage.setY((screenBounds.getHeight() - stage.getHeight()) / 2);
    }

}
