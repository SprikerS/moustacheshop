package dev.sprikers.moustacheshop.models;

import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import lombok.Getter;

/**
 * Clase que representa un modelo de enlace de usuario con propiedades observables.
 * Facilita la sincronización de datos del modelo de usuario a la interfaz de usuario,
 * permitiendo que los cambios en los datos se reflejen automáticamente en los componentes
 * de la interfaz mediante JavaFX.
 *
 * @author SprikerS
 */
@Getter
public class UserBindingModel {

    /**
     * Propiedades observables del modelo de usuario.
     */
    private final StringProperty id = new SimpleStringProperty();
    private final StringProperty names = new SimpleStringProperty();
    private final StringProperty paternalSurname = new SimpleStringProperty();
    private final StringProperty maternalSurname = new SimpleStringProperty();
    private final StringProperty dni = new SimpleStringProperty();
    private final StringProperty email = new SimpleStringProperty();
    private final ObjectProperty<Integer> phoneNumber = new SimpleObjectProperty<>();
    private final ObservableList<String> roles = FXCollections.observableArrayList();
    private final StringProperty token = new SimpleStringProperty();
    private final BooleanProperty active = new SimpleBooleanProperty();

    /**
     * Sincroniza las propiedades observables con los datos del modelo de usuario proporcionado.
     * Este metodo actualiza cada propiedad observable con la información correspondiente del UserModel.
     *
     * @param userModel El modelo de usuario que contiene los datos a sincronizar.
     */
    public void syncFromUserModel(UserModel userModel) {
        id.set(userModel.getId());
        names.set(userModel.getNames());
        paternalSurname.set(userModel.getPaternalSurname());
        maternalSurname.set(userModel.getMaternalSurname());
        dni.set(userModel.getDni());
        email.set(userModel.getEmail());
        phoneNumber.set(userModel.getPhoneNumber());
        roles.setAll(userModel.getRoles());
        token.set(userModel.getToken());
        active.set(userModel.isActive());
    }

}
