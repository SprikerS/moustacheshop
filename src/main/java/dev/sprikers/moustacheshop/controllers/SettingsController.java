package dev.sprikers.moustacheshop.controllers;

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

public class SettingsController implements Initializable {

    private final Map<Button, Tab> buttonTabMap = new HashMap<>();

    @FXML
    private Button btnTabManage;

    @FXML
    private Button btnTabRegister;

    @FXML
    private Tab tabManage;

    @FXML
    private TabPane tabPane;

    @FXML
    private Tab tabRegister;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        initializeButtonTabMap();
    }

    private void initializeButtonTabMap() {
        buttonTabMap.put(btnTabManage, tabManage);
        buttonTabMap.put(btnTabRegister, tabRegister);

        for (Button button : buttonTabMap.keySet())
            button.setOnAction(this::handleTabButtonAction);
    }

    private void handleTabButtonAction(ActionEvent event) {
        Button selectedButton = (Button) event.getSource();
        tabPane.getSelectionModel().select(buttonTabMap.get(selectedButton));

        for (Button button : buttonTabMap.keySet())
            button.getStyleClass().remove("button-tab-pane");

        selectedButton.getStyleClass().add("button-tab-pane");
    }
}
