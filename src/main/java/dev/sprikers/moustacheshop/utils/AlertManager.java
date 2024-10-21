package dev.sprikers.moustacheshop.utils;

import java.awt.Toolkit;
import java.util.Optional;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

import dev.sprikers.moustacheshop.application.Main;

/**
 * Clase de utilidad para mostrar mensajes de alerta en la interfaz.
 * Proporciona métodos para mostrar mensajes de error, éxito, información y advertencia.
 * También proporciona un metodo para mostrar un mensaje de confirmación.
 *
 * @autor SprikerS
 */
public class AlertManager {

    public static void showErrorMessage(String message, boolean exit) {
        showAlert(Alert.AlertType.ERROR, message, exit);
    }

    public static void showErrorMessage(String message) {
        showAlert(Alert.AlertType.ERROR, message, false);
    }

    public static void showSuccessMessage(String message) {
        showAlert(Alert.AlertType.INFORMATION, message, false);
    }

    public static void showInfoMessage(String message) {
        showAlert(Alert.AlertType.INFORMATION, message, false);
    }

    public static void showWarningMessage(String message) {
        showAlert(Alert.AlertType.WARNING, message, false);
    }

    /**
     * Muestra un mensaje de alerta en la interfaz.
     *
     * @param alertType Tipo de alerta a mostrar.
     * @param message   Mensaje a mostrar en la alerta.
     * @param exit      Indica si la aplicación debe cerrarse después de mostrar la alerta.
     */
    private static void showAlert(Alert.AlertType alertType, String message, boolean exit) {
        Toolkit.getDefaultToolkit().beep();

        Alert alert = new Alert(alertType);
        alert.initOwner(Main.getApplicationStage());

        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();

        if (exit) System.exit(0);
    }

    /**
     * Muestra un mensaje de confirmación en la interfaz.
     *
     * @param contentText Mensaje a mostrar en la alerta.
     * @param alertType   Tipo de alerta a mostrar.
     * @return true si el usuario confirma la acción, false en caso contrario.
     */
    public static boolean showConfirmation(String contentText, Alert.AlertType alertType) {
        Toolkit.getDefaultToolkit().beep();

        Alert confirmationAlert = new Alert(alertType);
        confirmationAlert.initOwner(Main.getApplicationStage());

        confirmationAlert.setHeaderText(null);
        confirmationAlert.setContentText(contentText);

        ButtonType yesButton = new ButtonType("Sí");
        ButtonType noButton = new ButtonType("No");
        confirmationAlert.getButtonTypes().setAll(yesButton, noButton);

        Optional<ButtonType> result = confirmationAlert.showAndWait();
        return result.isPresent() && result.get() == yesButton;
    }

}
