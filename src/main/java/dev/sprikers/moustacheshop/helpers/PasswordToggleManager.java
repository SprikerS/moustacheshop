package dev.sprikers.moustacheshop.helpers;

import java.util.Objects;

import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import dev.sprikers.moustacheshop.constants.PathImages;

/**
 * Clase encargada de gestionar la visibilidad de los campos de contraseña
 * en los controladores de la aplicación.
 *
 * @author SprikerS
 */
public class PasswordToggleManager {

    /**
     * Configura la visibilidad de los campos de contraseña y los componentes
     * relacionados, como el botón de alternar y el ícono.
     *
     * @param hiddenPass    El campo de contraseña (PasswordField) que se ocultará.
     * @param visiblePass   El campo de texto (TextField) que será visible.
     * @param toggleButton  El ToggleButton que permite alternar la visibilidad.
     * @param imageView     El ImageView que muestra el ícono de ojo.
     */
    public static void configureVisibility(PasswordField hiddenPass, TextField visiblePass, ToggleButton toggleButton, ImageView imageView) {
        // Sincronizar los textos de los campos
        hiddenPass.textProperty().bindBidirectional(visiblePass.textProperty());

        // Mostrar el botón solo si hay texto en el campo
        hiddenPass.textProperty().addListener((obs, oldText, newText) -> toggleButton.setVisible(!newText.isEmpty()));

        // Listener para el botón de toggle
        toggleButton.selectedProperty().addListener((obs, oldValue, newValue) -> toggleVisibility(newValue, hiddenPass, visiblePass, imageView));
    }

    /**
     * Alterna la visibilidad de los campos de contraseña y texto, y actualiza
     * el ícono correspondiente.
     *
     * @param isVisible     Indica si el campo de texto debe ser visible.
     * @param hiddenPass    El campo de contraseña (PasswordField) que se ocultará o mostrará.
     * @param visiblePass   El campo de texto (TextField) que se ocultará o mostrará.
     * @param imageView     El ImageView que muestra el ícono de ojo.
     */
    private static void toggleVisibility(boolean isVisible, PasswordField hiddenPass, TextField visiblePass, ImageView imageView) {
        visiblePass.setVisible(isVisible);
        hiddenPass.setVisible(!isVisible);

        String imagePath = isVisible ? PathImages.EYE : PathImages.EYE_OFF;
        imageView.setImage(new Image(Objects.requireNonNull(PasswordToggleManager.class.getResourceAsStream(imagePath))));
    }

}
