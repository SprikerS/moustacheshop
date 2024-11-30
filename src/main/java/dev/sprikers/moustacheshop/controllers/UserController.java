package dev.sprikers.moustacheshop.controllers;

import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import com.jfoenix.controls.JFXCheckBox;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import org.controlsfx.control.CheckComboBox;

import dev.sprikers.moustacheshop.components.Toaster;
import dev.sprikers.moustacheshop.dto.UserRequest;
import dev.sprikers.moustacheshop.enums.UserRole;
import dev.sprikers.moustacheshop.helpers.PasswordToggleManager;
import dev.sprikers.moustacheshop.models.UserModel;
import dev.sprikers.moustacheshop.services.UserService;
import dev.sprikers.moustacheshop.utils.AlertManager;
import dev.sprikers.moustacheshop.utils.ReniecFetch;
import dev.sprikers.moustacheshop.utils.TextFieldFormatter;

public class UserController implements Initializable {

    private static final UserService userService = new UserService();

    @FXML
    private Button btnClean, btnDelete, btnSubmit;

    @FXML
    private CheckComboBox<UserRole> chkcbRoles;

    @FXML
    private HBox hbProductSpinner;

    @FXML
    private ImageView btnToggleEye;

    @FXML
    private JFXCheckBox chkActive;

    @FXML
    private PasswordField txtHiddenPass;

    @FXML
    private Region btnReniec;

    @FXML
    private TableColumn<UserModel, Boolean> colActive;

    @FXML
    private TableColumn<UserModel, Integer> colDNI;

    @FXML
    private TableColumn<UserModel, String> colEmail;

    @FXML
    private TableColumn<UserModel, String> colNames;

    @FXML
    private TableColumn<UserModel, Integer> colPhone;

    @FXML
    private TableColumn<UserModel, String> colRoles;

    @FXML
    private TableColumn<UserModel, String> colSurMater;

    @FXML
    private TableColumn<UserModel, String> colSurPater;

    @FXML
    private TableView<UserModel> tblUsers;

    @FXML
    private TextField txtDNI, txtEmail, txtMaternalSurname, txtNames, txtPaternalSurname, txtPhone, txtVisiblePass;

    @FXML
    private ToggleButton toggleDisplayPass;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        PasswordToggleManager.configureVisibility(txtHiddenPass, txtVisiblePass, toggleDisplayPass, btnToggleEye);
        ReniecFetch.configureFields(btnReniec, txtDNI, txtNames, txtPaternalSurname, txtMaternalSurname);
        TextFieldFormatter.applyIntegerFormat(txtPhone, 9);

        Platform.runLater(() -> {
            for (UserRole role : UserRole.values()) {
                if (role != UserRole.SUPERUSER) chkcbRoles.getItems().add(role);
            }

            chkcbRoles.getCheckModel().check(0);
        });

        initializeTableColumns();
        initializeEventHandlers();

        fetchUserList();
    }

    private List<String> getSelectedRoles() {
        return chkcbRoles.getCheckModel().getCheckedItems().stream()
            .map(UserRole::getRole)
            .collect(Collectors.toList());
    }

    private void handleSubmit() {
        String dni = txtDNI.getText().trim();
        String names = txtNames.getText().trim();
        String paternalSurname = txtPaternalSurname.getText().trim();
        String maternalSurname = txtMaternalSurname.getText().trim();
        String email = txtEmail.getText().trim();
        String password = txtHiddenPass.getText().trim();
        String phoneText = txtPhone.getText().trim();
        Integer phone = phoneText.isEmpty() ? null : Integer.parseInt(phoneText);
        List<String> roles = getSelectedRoles();

        if (dni.isEmpty() || names.isEmpty() || paternalSurname.isEmpty() || maternalSurname.isEmpty() || email.isEmpty() || password.isEmpty() || roles.isEmpty()) {
            Toaster.showWarning("Por favor, complete todos los campos");
            return;
        }

        UserRequest userRequest = new UserRequest(dni, names, paternalSurname, maternalSurname, email, password, phone, roles);
        saveOrUpdateUser(userRequest);
    }

    private void saveOrUpdateUser(UserRequest userRequest) {
        CompletableFuture<UserModel> userFuture = userService.register(userRequest);

        userFuture
            .thenAccept(user -> Platform.runLater(() -> {
                String messageToast = "Usuario %s registrado".formatted(user.getNames());
                Toaster.showSuccess(messageToast);
            }))
            .exceptionally(ex -> {
                Platform.runLater(() -> AlertManager.showErrorMessage("Error al registrar el usuario: %s".formatted(ex.getCause().getMessage())));
                return null;
            });
    }

    private void fetchUserList(){
        userService.allUsers()
            .thenAccept(users -> {
                setUsersList(users);
                System.out.println(users);
            })
            .exceptionally(ex -> {
                Platform.runLater(() -> AlertManager.showErrorMessage("Error al obtener la lista de usuarios: %s".formatted(ex.getCause().getMessage())));
                return null;
            });
    }

    /* ----------------------------------
     * Sección de configuración de la UI
     * ---------------------------------- */

    private void initializeTableColumns() {
        colDNI.setCellValueFactory(new PropertyValueFactory<>("dni"));
        colNames.setCellValueFactory(new PropertyValueFactory<>("names"));
        colSurPater.setCellValueFactory(new PropertyValueFactory<>("paternalSurname"));
        colSurMater.setCellValueFactory(new PropertyValueFactory<>("maternalSurname"));
        colEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        colPhone.setCellValueFactory(new PropertyValueFactory<>("phoneNumber"));
        colActive.setCellValueFactory(new PropertyValueFactory<>("active"));

        colRoles.setCellValueFactory(cellData -> {
            List<String> roles = cellData.getValue().getRoles();
            if (roles.isEmpty()) return new SimpleStringProperty("");

            String lastRole = roles.getLast();
            String translatedRole = Arrays.stream(UserRole.values())
                .filter(role -> role.getRole().equals(lastRole))
                .map(UserRole::getText)
                .findFirst()
                .orElse(lastRole);
            return new SimpleStringProperty(translatedRole);
        });
    }

    private void initializeEventHandlers() {
        btnSubmit.setOnMouseClicked(event -> handleSubmit());
    }

    private void setUsersList(List<UserModel> users) {
        tblUsers.getItems().setAll(users);
    }

}
