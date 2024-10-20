package dev.sprikers.moustacheshop.controllers;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

import com.jfoenix.controls.JFXButton;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;

import dev.sprikers.moustacheshop.helpers.PasswordToggleManager;
import dev.sprikers.moustacheshop.models.UserModel;
import dev.sprikers.moustacheshop.utils.UserSession;

public class SettingsController implements Initializable {

    private final Map<Button, Tab> buttonTabMap = new HashMap<>();
    private final UserModel user = UserSession.getInstance().getUserModel();

    @FXML
    private Button btnTabAccount, btnTabPassword;

    @FXML
    private ImageView eyeNew, eyeNewConf, eyeOld;

    @FXML
    private JFXButton btnSubmitAccount, btnSubmitPass;

    @FXML
    private PasswordField passNew, passNewConf, passOld;

    @FXML
    private Tab tabAccount, tabPassword;

    @FXML
    private TabPane tabProfile;

    @FXML
    private TextField txtDNI, txtEmail, txtMaternalSurname, txtNames, txtPaternalSurname, txtPhone, textNew, textNewConf, textOld;

    @FXML
    private ToggleButton toggleNew, toggleNewConf, toggleOld;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        initializeButtonTabMap();
        configurePasswordVisibility();
        populateUserDetails();
    }

    private void initializeButtonTabMap() {
        buttonTabMap.put(btnTabAccount, tabAccount);
        buttonTabMap.put(btnTabPassword, tabPassword);

        for (Button button : buttonTabMap.keySet())
            button.setOnAction(this::handleTabButtonAction);
    }

    private void handleTabButtonAction(ActionEvent event) {
        Button selectedButton = (Button) event.getSource();
        tabProfile.getSelectionModel().select(buttonTabMap.get(selectedButton));

        for (Button button : buttonTabMap.keySet())
            button.getStyleClass().remove("button-tab-pane");

        selectedButton.getStyleClass().add("button-tab-pane");
    }

    private void configurePasswordVisibility() {
        PasswordToggleManager.configureVisibility(passOld, textOld, toggleOld, eyeOld);
        PasswordToggleManager.configureVisibility(passNew, textNew, toggleNew, eyeNew);
        PasswordToggleManager.configureVisibility(passNewConf, textNewConf, toggleNewConf, eyeNewConf);
    }

    private void populateUserDetails() {
        txtDNI.setText(user.getDni());
        txtEmail.setText(user.getEmail());
        txtMaternalSurname.setText(user.getMaternalSurname());
        txtNames.setText(user.getNames());
        txtPaternalSurname.setText(user.getPaternalSurname());
        txtPhone.setText(user.getPhoneNumber() != null ? String.valueOf(user.getPhoneNumber()) : "");
    }

}
