package dev.sprikers.moustacheshop.utils;

import javafx.animation.Interpolator;
import javafx.animation.PauseTransition;
import javafx.animation.RotateTransition;
import javafx.scene.Node;
import javafx.util.Duration;

/**
 * Clase de utilidad para manejar la animación de un spinner en la interfaz de usuario,
 * permitiendo iniciar, detener y aplicar efectos visuales durante la carga de datos.
 * Añade y elimina dinámicamente estilos al nodo para indicar el estado de la operación.
 *
 * <p>Uso típico:</p>
 * <pre>
 *     SpinnerLoader spinner = new SpinnerLoader();
 *     spinner.setNode(region);
 *     spinner.start();
 *     // ... operación asíncrona ...
 *     spinner.stop();
 * </pre>
 *
 * @author SprikerS
 */
public class SpinnerLoader {

    private static final String ACTIVE_CLASS = "spinner-active";
    private static final String SUCCESS_CLASS = "spinner-success";

    private RotateTransition spinnerAnimation;
    private Node node;

    /**
     * Configura el nodo donde se aplicará la animación del spinner.
     *
     * @param node Nodo sobre el cual se animará el spinner.
     */
    public void setNode(Node node) {
        this.node = node;
        if (node != null) setupSpinnerAnimation();
    }

    /**
     * Inicia la animación del spinner, añadiendo la clase CSS "spinner-active"
     * para aplicar el estilo de carga y deshabilitando el nodo durante la animación.
     */
    public void start() {
        if (node != null && spinnerAnimation != null) {
            node.getStyleClass().add(ACTIVE_CLASS);
            node.setDisable(true);
            spinnerAnimation.play();
        }
    }

    /**
     * Detiene la animación del spinner, removiendo la clase "spinner-active" y añadiendo la clase
     * "spinner-success" para indicar la finalización de la operación.
     * La clase "spinner-success" se elimina después de 3 segundos.
     */
    public void stop() {
        if (node != null && spinnerAnimation != null) {
            spinnerAnimation.stop();
            node.getStyleClass().remove(ACTIVE_CLASS);
            node.getStyleClass().add(SUCCESS_CLASS);

            PauseTransition pause = new PauseTransition(Duration.seconds(3));
            pause.setOnFinished(event -> {
                node.getStyleClass().remove(SUCCESS_CLASS);
                node.setDisable(false);
            });
            pause.play();
        }
    }

    /**
     * Configura la animación de rotación del spinner.
     * La animación rota el nodo 360 grados en 1 segundo y se repite indefinidamente hasta que se detenga.
     */
    private void setupSpinnerAnimation() {
        spinnerAnimation = new RotateTransition(Duration.seconds(1), node);
        spinnerAnimation.setByAngle(360);
        spinnerAnimation.setCycleCount(RotateTransition.INDEFINITE);
        spinnerAnimation.setInterpolator(Interpolator.LINEAR);
    }

}
