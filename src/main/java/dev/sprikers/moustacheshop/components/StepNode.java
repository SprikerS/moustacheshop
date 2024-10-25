package dev.sprikers.moustacheshop.components;

import javafx.scene.control.Button;
import javafx.scene.control.Tab;
import lombok.Getter;
import lombok.Setter;

/**
 * Clase que representa un nodo en la secuencia de pasos de un proceso de navegación.
 * Cada nodo contiene referencias a los pasos anterior, actual y siguiente,
 * así como los botones para navegar entre ellos.
 */
@Getter
@Setter
public class StepNode {

    private Tab tabPrev;
    private Tab tabCurrent;
    private Tab tabNext;

    private Button btnPrev;
    private Button btnNext;

    private StepNode next;
    private StepNode prev;

}
