package dev.sprikers.moustacheshop.application;

import java.io.IOException;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.Event;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.stage.Screen;
import javafx.stage.Stage;

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
    public void start(Stage stage) throws IOException {
        boolean jwtExists = JwtPreferencesManager.isJWT();
        String viewPath = jwtExists ? PathViews.HOME : PathViews.AUTH;

        if (jwtExists) {
            try {
                authService.fetchUserInfo();
            } catch (Exception e) {
                AlertManager.showErrorMessage(e.getMessage(), true);
            }
        }

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(viewPath));
        Scene scene = new Scene(fxmlLoader.load());
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();

        centerOnScreen(stage);
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
        newStage.setScene(scene);
        newStage.setTitle(title);
        newStage.setResizable(false);
        newStage.show();

        centerOnScreen(newStage);
        Platform.runLater(() -> ((Stage) (((Node) event.getSource()).getScene().getWindow())).close());
    }

    private static void centerOnScreen(Stage stage) {
        Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
        stage.setX((screenBounds.getWidth() - stage.getWidth()) / 2);
        stage.setY((screenBounds.getHeight() - stage.getHeight()) / 2);
    }

}
