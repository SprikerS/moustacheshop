package dev.sprikers.moustacheshop.components;

import java.util.List;

import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import lombok.Setter;

/**
 * Clase para gestionar la navegación a través de pasos en una interfaz de usuario.
 * Permite moverse entre diferentes pestañas en un TabPane, actualizando la visualización
 * de pasos según el progreso del usuario.
 *
 * @author SprikerS
 */
public class StepNavigator {

    private TabPane tabPane;

    @Setter
    private List<Node> steps;

    @Setter
    private List<Tab> tabs;

    private StepNode head;
    private StepNode tail;

    /**
     * Inicializa el primer paso del proceso, configurando el botón "Next"
     * para avanzar al siguiente paso.
     *
     * @param btnNext Botón que permite avanzar al siguiente paso.
     */
    public void firstStep(Button btnNext) {
        StepNode firstStep = new StepNode();
        firstStep.setTabCurrent(tabs.getFirst());
        firstStep.setTabNext(tabs.get(1));
        firstStep.setBtnNext(btnNext);

        btnNext.setOnAction(e -> moveToNext(firstStep));

        head = firstStep;
        tail = firstStep;
        tabPane = tabs.getFirst().getTabPane();
    }

    /**
     * Agrega un nuevo paso a la lista de pasos, configurando los botones "Previous" y "Next"
     * para navegar entre pasos.
     *
     * @param btnPrev Botón que permite retroceder al paso anterior.
     * @param btnNext Botón que permite avanzar al siguiente paso.
     */
    public void addStep(Button btnPrev, Button btnNext) {
        int currentIndex = (head == null ? 0 : (tail == null ? 1 : tabs.indexOf(tail.getTabCurrent()) + 1));
        if (currentIndex <= 0 || currentIndex >= tabs.size() - 1) return;

        StepNode current = new StepNode();
        current.setTabCurrent(tabs.get(currentIndex));
        current.setTabPrev(tabs.get(currentIndex - 1));
        current.setTabNext(tabs.get(currentIndex + 1));
        current.setBtnPrev(btnPrev);
        current.setBtnNext(btnNext);

        btnNext.setOnAction(e -> moveToNext(current));
        btnPrev.setOnAction(e -> moveToPrev(current));

        if (head == null) {
            head = current;
        } else {
            tail.setNext(current);
            current.setPrev(tail);
        }
        tail = current;
    }

    /**
     * Inicializa el último paso del proceso, configurando el botón "Previous"
     * para retroceder al paso anterior.
     *
     * @param btnPrev Botón que permite retroceder al paso anterior.
     */
    public void lastStep(Button btnPrev) {
        StepNode lastStep = new StepNode();
        lastStep.setTabCurrent(tabs.getLast());
        lastStep.setTabPrev(tabs.get(tabs.size() - 2));
        lastStep.setBtnPrev(btnPrev);

        btnPrev.setOnAction(e -> moveToPrev(lastStep));

        tail.setNext(lastStep);
        lastStep.setPrev(tail);
        tail = lastStep;
    }

    /**
     * Mueve la selección al siguiente paso en la interfaz, actualizando la visualización
     * del TabPane y los estilos de los pasos.
     *
     * @param current El paso actual del que se desea avanzar.
     */
    private void moveToNext(StepNode current) {
        if (current.getNext() != null) {
            tabPane.getSelectionModel().select(current.getNext().getTabCurrent());
            updateStep(current, true);
        }
    }

    /**
     * Mueve la selección al paso anterior en la interfaz, actualizando la visualización
     * del TabPane y los estilos de los pasos.
     *
     * @param current El paso actual del que se desea retroceder.
     */
    private void moveToPrev(StepNode current) {
        if (current.getPrev() != null) {
            tabPane.getSelectionModel().select(current.getPrev().getTabCurrent());
            updateStep(current, false);
        }
    }

    /**
     * Actualiza los estilos de los pasos según la dirección de navegación (siguiente o anterior).
     *
     * @param stepNode El paso que se está actualizando.
     * @param isNext Booleano que indica si se navega hacia el siguiente paso.
     */
    private void updateStep(StepNode stepNode, boolean isNext) {
        int current = tabs.indexOf(stepNode.getTabCurrent());
        int navigate = tabs.indexOf(isNext ? stepNode.getNext().getTabCurrent() : stepNode.getPrev().getTabCurrent());

        if (isNext) {
            steps.get(current).getStyleClass().add("step-success");
            steps.get(navigate).getStyleClass().add("step-current");
        } else {
            steps.get(current).getStyleClass().remove("step-current");
            steps.get(navigate).getStyleClass().remove("step-success");
        }
    }

}
