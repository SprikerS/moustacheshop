package dev.sprikers.moustacheshop.controladores;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import org.controlsfx.control.CheckComboBox;

public class UsuarioControlador implements Initializable {

    @FXML
    private CheckComboBox<String> chkcbRoles;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        ObservableList<String> rolesValidos = FXCollections.observableArrayList(
            "Cliente",
            "Trabajador",
            "Administrador"
        );

        chkcbRoles.getItems().addAll(rolesValidos);
        chkcbRoles.getCheckModel().check(0);
    }

}
