package dev.sprikers.moustacheshop.components;

import javafx.animation.*;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.SVGPath;
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

    @FXML
    private HBox toaster;

    @FXML
    private Label lblMessage;

    @FXML
    private SVGPath svgData;

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
     * Muestra un mensaje neutro en formato *toast*.
     *
     * @param text El mensaje neutro a mostrar.
     */
    public void showNeutral(String text) {
        showToast(text, ToastType.NEUTRAL);
    }

    /**
     * Muestra un mensaje de éxito o información en formato *toast*.
     * Si el parámetro `update` es verdadero, se mostrará un mensaje de información.
     * De lo contrario, se mostrará un mensaje de éxito.
     *
     * @param text   El mensaje a mostrar.
     * @param update Indica si se mostrará un mensaje de información o éxito.
     */
    public void showSucessOrInfo(String text, boolean update) {
        if (update) {
            showInfo(text);
        } else {
            showSuccess(text);
        }
    }

    /**
     * Configura el contenido del toast con el mensaje y el tipo proporcionado.
     * Establece el título, mensaje e imagen correspondiente al tipo de toast.
     *
     * @param message   El mensaje a mostrar en el toast.
     * @param toastType El tipo de toast (éxito, información, advertencia, error).
     */
    private void setToast(String message, ToastType toastType) {
        lblMessage.setText(message);
        svgData.setContent(toastType.getSvgPathData());
        toaster.getStyleClass().add(toastType.getCssClass());
    }

    /**
     * Muestra el toast con el mensaje y tipo proporcionado.
     * Aplica animaciones de fade-in y fade-out y lo posiciona en pantalla.
     *
     * @param message El mensaje a mostrar en el toast.
     * @param type    El tipo de toast a mostrar.
     */
    private void showToast(String message, ToastType type) {
        Platform.runLater(() -> {
            Stage dialog = createAndPositionToastStage();

            try {
                FXMLLoader fxml = new FXMLLoader();
                fxml.setLocation(ToasterController.class.getResource(PathComponents.TOASTER));

                HBox root = fxml.load();
                ToasterController controller = fxml.getController();
                controller.setToast(message, type);

                Scene scene = new Scene(root);
                scene.setFill(Color.TRANSPARENT);
                dialog.setScene(scene);

                // Obtener la animación de transición completa
                SequentialTransition transition = createToastFadeTransition(root);
                transition.setOnFinished(ae -> dialog.close());

                dialog.show();
                transition.play();
            } catch (Exception ex) {
                System.out.println("Error al mostrar el toast: " + ex.getMessage());
            }
        });
    }

    /**
     * Crea un nuevo escenario para el toast y lo posiciona en la esquina inferior derecha de la ventana principal.
     *
     * @return El escenario del toast configurado.
     */
    private Stage createAndPositionToastStage() {
        Stage dialog = new Stage();
        dialog.initOwner(Main.getApplicationStage());
        dialog.initStyle(StageStyle.TRANSPARENT);
        dialog.setResizable(false);

        double dialogX = dialog.getOwner().getX();
        double dialogY = dialog.getOwner().getY();
        double dialogW = dialog.getOwner().getWidth();
        double dialogH = dialog.getOwner().getHeight();

        double toastWidth = 308;  // Ancho del toast
        double toastHeight = 70;  // Alto del toast
        double marginRight = 20;  // Margen a la derecha
        double marginBottom = 51; // Margen inferior

        double posX = dialogX + dialogW - toastWidth - marginRight;
        double posY = dialogY + dialogH - toastHeight - marginBottom;
        dialog.setX(posX);
        dialog.setY(posY);

        return dialog;
    }

    /**
     * Crea una secuencia de transiciones para el toast especificado.
     * La secuencia incluye una transición de fade-in, una pausa y una transición de fade-out.
     *
     * @param node El nodo que se animará con las transiciones de fade.
     * @return Una secuencia de transiciones configurada.
     */
    private SequentialTransition createToastFadeTransition(Node node) {
        FadeTransition fadeIn = createFadeTransition(node, FADE_IN_DELAY, true);
        FadeTransition fadeOut = createFadeTransition(node, FADE_OUT_DELAY, false);
        PauseTransition pause = new PauseTransition(Duration.millis(TOAST_DELAY));

        return new SequentialTransition(fadeIn, pause, fadeOut);
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
