package dev.sprikers.moustacheshop.aplicacion;

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
import javafx.stage.StageStyle;
import org.burningwave.core.assembler.StaticComponentContainer;

import dev.sprikers.moustacheshop.constantes.RutaImagenes;
import dev.sprikers.moustacheshop.constantes.RutaVistas;

public class Main extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void init() {
        StaticComponentContainer.JVMInfo.getVersion();
    }

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(RutaVistas.AUTH));
        configurarStage(stage, fxmlLoader);
    }

    public static void cambiarStage(String fxmlFile, Event event) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource(fxmlFile));
        Stage newStage = new Stage();
        configurarStage(newStage, fxmlLoader);

        Platform.runLater(() -> ((Stage) (((Node) event.getSource()).getScene().getWindow())).close());
    }

    private static void configurarStage(Stage stage, FXMLLoader fxmlLoader) throws IOException {
        Scene scene = new Scene(fxmlLoader.load());
        stage.initStyle(StageStyle.UNDECORATED);
        stage.getIcons().addAll(cargarIconos());
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();

        centrarPantalla(stage);
    }

    private static List<Image> cargarIconos() {
        List<Image> icons = new ArrayList<>();
        for (String logoPath : RutaImagenes.ALL_LOGOS) {
            icons.add(new Image(Objects.requireNonNull(Main.class.getResourceAsStream(logoPath))));
        }
        return icons;
    }

    private static void centrarPantalla(Stage stage) {
        Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
        stage.setX((screenBounds.getWidth() - stage.getWidth()) / 2);
        stage.setY((screenBounds.getHeight() - stage.getHeight()) / 2);
    }

}
