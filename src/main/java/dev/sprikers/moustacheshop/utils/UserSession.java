package dev.sprikers.moustacheshop.utils;

import javafx.application.Platform;
import lombok.Getter;
import lombok.Setter;

import dev.sprikers.moustacheshop.models.UserBindingModel;
import dev.sprikers.moustacheshop.models.UserModel;

/**
 * Clase que gestiona la sesión del usuario en la aplicación.
 * Implementa el patrón Singleton para asegurar que solo haya una instancia de la sesión de usuario.
 *
 * @author SprikerS
 */
@Getter
@Setter
public class UserSession {

    // Instancia única de UserSession
    private static UserSession instance;

    // Modelo de usuario no observable
    private UserModel userModel;

    // Modelo de enlace de usuario que contiene propiedades observables
    private final UserBindingModel userBindingModel = new UserBindingModel();

    /**
     * Constructor privado para evitar la creación de instancias adicionales.
     */
    private UserSession() {}

    /**
     * Obtiene la instancia única de la sesión de usuario.
     * Si no existe, crea una nueva instancia.
     *
     * @return Instancia de la sesión de usuario.
     */
    public static UserSession getInstance() {
        if (instance == null) {
            instance = new UserSession();
        }
        return instance;
    }

    /**
     * Establece el modelo de usuario y sincroniza las propiedades del modelo de enlace.
     * Utiliza Platform.runLater para asegurarse de que la sincronización se realice en el hilo de la interfaz de usuario.
     *
     * @param userModel El modelo de usuario que se va a establecer.
     */
    public void setUserModel(UserModel userModel) {
        this.userModel = userModel;
        Platform.runLater(() -> userBindingModel.syncFromUserModel(userModel));
    }

}
