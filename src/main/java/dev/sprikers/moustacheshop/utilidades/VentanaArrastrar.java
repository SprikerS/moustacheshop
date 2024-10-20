package dev.sprikers.moustacheshop.utilidades;

import javafx.scene.Node;
import javafx.stage.Stage;

/**
 * Clase que permite arrastrar una ventana de JavaFX sin bordes.
 * Se puede usar en cualquier nodo de la interfaz.
 *
 * @author SprikerS
 */
public class VentanaArrastrar {

    private double xOffset = 0;
    private double yOffset = 0;

    /**
     * Metodo que habilita el arrastre de la ventana, sin interferir con controles interactivos.
     * @param node El nodo raíz donde se habilita el arrastre (por ejemplo, un HBox).
     */
    public void enableWindowDragging(Node node) {
        Stage stage = (Stage) node.getScene().getWindow();

        node.setOnMousePressed(event -> {
            if (isDraggable(event.getTarget())) {
                xOffset = event.getSceneX();
                yOffset = event.getSceneY();
                stage.setOpacity(0.7);
            }
        });

        node.setOnMouseDragged(event -> {
            if (isDraggable(event.getTarget())) {
                stage.setX(event.getScreenX() - xOffset);
                stage.setY(event.getScreenY() - yOffset);
            }
        });

        node.setOnMouseReleased(event -> stage.setOpacity(1.0));
    }

    /**
     * Comprueba si el objeto sobre el que se hizo clic es "draggable", es decir, no es un control interactivo.
     * @param target El objeto sobre el cual se hizo clic.
     * @return true si no es un control interactivo, false si es un control como un botón.
     */
    private boolean isDraggable(Object target) {
        if (target instanceof Node clickedNode) {
            return clickedNode.getOnMouseClicked() == null && clickedNode.getOnMousePressed() == null;
        }
        return true;
    }

}
