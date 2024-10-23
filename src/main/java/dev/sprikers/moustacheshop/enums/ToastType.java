package dev.sprikers.moustacheshop.enums;

import lombok.Getter;

import dev.sprikers.moustacheshop.constants.PathImages;

/**
 * Enumeración que representa los diferentes tipos de toast disponibles en la aplicación.
 * Cada tipo de toast contiene un título y la ruta a la imagen correspondiente.
 *
 * @author SprikerS
 */
@Getter
public enum ToastType {
    SUCCESS("Éxito", PathImages.TOAST_OK),
    INFO("Información", PathImages.TOAST_INFO),
    WARNING("Advertencia", PathImages.TOAST_WARN),
    ERROR("Error", PathImages.TOAST_CANCEL);

    private final String title;
    private final String imagePath;

    /**
     * Constructor para inicializar el título y la ruta de imagen del tipo de *toast*.
     *
     * @param title     El título que se mostrará en el *toast*.
     * @param imagePath La ruta a la imagen que se usará en el *toast*.
     */
    ToastType(String title, String imagePath) {
        this.title = title;
        this.imagePath = imagePath;
    }

}
