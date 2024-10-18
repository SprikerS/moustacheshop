package dev.sprikers.moustacheshop.utils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.control.Label;
import javafx.util.Duration;

/**
 * Clase de utilidad para manejar la visualización y actualización de fecha y hora en la interfaz.
 * Proporciona métodos para formatear y mostrar la fecha una vez, y actualizar la hora cada segundo.
 *
 * @author SprikerS
 */
public class DateTimeUpdater {

    private static final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("hh:mm:ss a");

    /**
     * Configura la fecha y hora en los componentes Label proporcionados.
     *
     * @param lblDate Componente Label donde se mostrará la fecha.
     * @param lblTime Componente Label donde se mostrará la hora.
     */
    public static void configureDateTimeLabels(Label lblDate, Label lblTime) {
        lblDate.setText(LocalDateTime.now().format(dateFormatter));
        lblTime.setText(LocalDateTime.now().format(timeFormatter));

        startClockUpdate(lblTime);
    }

    /**
     * Inicia la actualización de la hora en el componente Label proporcionado.
     *
     * @param lblTime Componente Label donde se mostrará la hora.
     */
    private static void startClockUpdate(Label lblTime) {
        Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(1), event -> {
            String currentTime = LocalDateTime.now().format(timeFormatter);
            lblTime.setText(currentTime);
        }));
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
    }

}
