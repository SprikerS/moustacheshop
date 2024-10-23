package dev.sprikers.moustacheshop.components;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import javafx.animation.*;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

import dev.sprikers.moustacheshop.application.Main;
import dev.sprikers.moustacheshop.constants.PathComponents;
import dev.sprikers.moustacheshop.enums.ToastType;

/**
 * Controlador encargado de gestionar la visualización de notificaciones tipo toast.
 * Proporciona métodos para mostrar mensajes de éxito, información, advertencia y error.
 * Utiliza animaciones de fade-in y fade-out para la visualización de los toasts.
 *
 * @author SprikerS
 */
public class ToasterController {

    private static final int TOAST_DELAY = 2500;
    private static final int FADE_IN_DELAY = 500;
    private static final int FADE_OUT_DELAY = 500;

    private final Map<ToastType, Image> imageCache = new HashMap<>();

    @FXML
    private ImageView imgType;

    @FXML
    private Label lblTitle, lblMessage;

    /**
     * Muestra un mensaje de éxito en formato *toast*.
     *
     * @param text El mensaje de éxito a mostrar.
     */
    public void showSuccess(String text) {
        showToast(text, ToastType.SUCCESS);
    }

    /**
     * Muestra un mensaje de información en formato *toast*.
     *
     * @param text El mensaje informativo a mostrar.
     */
    public void showInfo(String text) {
        showToast(text, ToastType.INFO);
    }

    /**
     * Muestra un mensaje de advertencia en formato *toast*.
     *
     * @param text El mensaje de advertencia a mostrar.
     */
    public void showWarning(String text) {
        showToast(text, ToastType.WARNING);
    }

    /**
     * Muestra un mensaje de error en formato *toast*.
     *
     * @param text El mensaje de error a mostrar.
     */
    public void showError(String text) {
        showToast(text, ToastType.ERROR);
    }

    /**
     * Configura el contenido del toast con el mensaje y el tipo proporcionado.
     * Establece el título, mensaje e imagen correspondiente al tipo de toast.
     *
     * @param message   El mensaje a mostrar en el toast.
     * @param toastType El tipo de toast (éxito, información, advertencia, error).
     */
    private void setToast(String message, ToastType toastType) {
        lblTitle.setText(toastType.getTitle());
        lblMessage.setText(message);

        Image image = imageCache.computeIfAbsent(toastType, type -> new Image(
            Objects.requireNonNull(getClass().getResourceAsStream(type.getImagePath()))
        ));
        imgType.setImage(image);
    }

    /**
     * Muestra el toast con el mensaje y tipo proporcionado.
     * Aplica animaciones de fade-in y fade-out y lo posiciona en pantalla.
     *
     * @param message El mensaje a mostrar en el toast.
     * @param type    El tipo de toast a mostrar.
     */
    private void showToast(String message, ToastType type) {
        Stage dialog = new Stage();
        dialog.initOwner(Main.getApplicationStage());
        dialog.initStyle(StageStyle.TRANSPARENT);
        dialog.setResizable(false);
        positionToast(dialog);

        try {
            FXMLLoader fxml = new FXMLLoader();
            fxml.setLocation(ToasterController.class.getResource(PathComponents.TOASTER));

            HBox root = fxml.load();
            ToasterController controller = fxml.getController();
            controller.setToast(message, type);

            Scene scene = new Scene(root);
            scene.setFill(Color.TRANSPARENT);
            dialog.setScene(scene);

            // Animaciones de fade-in y fade-out
            FadeTransition fadeIn = createFadeTransition(root, FADE_IN_DELAY, true);
            FadeTransition fadeOut = createFadeTransition(root, FADE_OUT_DELAY, false);

            // Transición que incluye pausa entre las animaciones
            PauseTransition pause = new PauseTransition(Duration.millis(TOAST_DELAY));
            SequentialTransition transition = new SequentialTransition(fadeIn, pause, fadeOut);
            transition.setOnFinished(ae -> dialog.close());

            dialog.show();
            transition.play();
        } catch (Exception ex) {
            System.out.println("Error al mostrar el toast: " + ex.getMessage());
        }
    }

    /**
     * Calcula y establece la posición del toast en la esquina inferior derecha de la ventana principal.
     *
     * @param dialog La ventana del toast a posicionar.
     */
    private void positionToast(Stage dialog) {
        double dialogX = dialog.getOwner().getX();
        double dialogY = dialog.getOwner().getY();
        double dialogW = dialog.getOwner().getWidth();
        double dialogH = dialog.getOwner().getHeight();

        double toastWidth = 300;  // Ancho del toast
        double toastHeight = 81;  // Alto del toast
        double marginRight = 20;  // Margen a la derecha
        double marginBottom = 51; // Margen inferior

        double posX = dialogX + dialogW - toastWidth - marginRight;
        double posY = dialogY + dialogH - toastHeight - marginBottom;
        dialog.setX(posX);
        dialog.setY(posY);
    }

    /**
     * Crea una transición de desvanecimiento (fade) para el nodo especificado.
     *
     * @param node     El nodo al que se aplicará la transición de fade.
     * @param duration La duración en milisegundos de la transición.
     * @param fadeIn   Indica si la transición es de entrada (fade-in) o salida (fade-out).
     * @return Un objeto FadeTransition configurado.
     */
    private FadeTransition createFadeTransition(Node node, int duration, boolean fadeIn) {
        FadeTransition fadeTransition = new FadeTransition(Duration.millis(duration), node);
        fadeTransition.setFromValue(fadeIn ? 0.0 : 1.0);
        fadeTransition.setToValue(fadeIn ? 1.0 : 0.0);
        return fadeTransition;
    }

}
