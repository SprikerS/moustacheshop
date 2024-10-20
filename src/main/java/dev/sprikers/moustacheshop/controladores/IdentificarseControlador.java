package dev.sprikers.moustacheshop.controladores;

import java.net.URL;
import java.util.ResourceBundle;

import com.jfoenix.controls.JFXButton;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

import dev.sprikers.moustacheshop.aplicacion.Main;
import dev.sprikers.moustacheshop.constantes.RutaVistas;
import dev.sprikers.moustacheshop.utilidades.VentanaArrastrar;

public class IdentificarseControlador implements Initializable {

    private final VentanaArrastrar ventanaArrastrar = new VentanaArrastrar();

    @FXML
    private HBox hbTituloBarra;

    @FXML
    private ImageView btnCerrar, btnMinimizar;

    @FXML
    private JFXButton btnCerrarSession;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        Platform.runLater(() -> ventanaArrastrar.enableWindowDragging(hbTituloBarra));

        btnCerrarSession.setOnAction(this::navegarCasa);
        btnCerrar.setOnMouseClicked(event -> System.exit(0));
        btnMinimizar.setOnMouseClicked(event -> ((Stage) btnMinimizar.getScene().getWindow()).setIconified(true));
    }

    private void navegarCasa(ActionEvent event) {
        try {
            Main.cambiarStage(RutaVistas.HOME, event);
        } catch (Exception e) {
            System.out.println("Error al navegar a la vista de inicio: " + e.getMessage());
        }
    }

}
