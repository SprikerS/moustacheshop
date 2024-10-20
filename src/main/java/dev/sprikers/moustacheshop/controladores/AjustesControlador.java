package dev.sprikers.moustacheshop.controladores;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;

public class AjustesControlador implements Initializable {

    private final Map<Button, Tab> buttonTabMap = new HashMap<>();

    @FXML
    private Button btnTabAdministrar;

    @FXML
    private Button btnTabRegistro;

    @FXML
    private Tab tabAdministrar;

    @FXML
    private TabPane tabPanel;

    @FXML
    private Tab tabRegistro;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        inicializarMapaBotonesTabs();
    }

    private void inicializarMapaBotonesTabs() {
        buttonTabMap.put(btnTabAdministrar, tabAdministrar);
        buttonTabMap.put(btnTabRegistro, tabRegistro);

        for (Button button : buttonTabMap.keySet())
            button.setOnAction(this::botonAccion);
    }

    private void botonAccion(ActionEvent event) {
        Button botonSeleccinado = (Button) event.getSource();
        tabPanel.getSelectionModel().select(buttonTabMap.get(botonSeleccinado));

        for (Button button : buttonTabMap.keySet())
            button.getStyleClass().remove("button-tab-pane");

        botonSeleccinado.getStyleClass().add("button-tab-pane");
    }
}
