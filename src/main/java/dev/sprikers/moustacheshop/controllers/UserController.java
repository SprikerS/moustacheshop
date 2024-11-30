package dev.sprikers.moustacheshop.controllers;

import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import com.jfoenix.controls.JFXCheckBox;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
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
import dev.sprikers.moustacheshop.dto.UserUpdateRequest;
import dev.sprikers.moustacheshop.enums.UserRole;
import dev.sprikers.moustacheshop.helpers.PasswordToggleManager;
import dev.sprikers.moustacheshop.models.UserModel;
import dev.sprikers.moustacheshop.services.UserService;
import dev.sprikers.moustacheshop.utils.AlertManager;
import dev.sprikers.moustacheshop.utils.ReniecFetch;
import dev.sprikers.moustacheshop.utils.TextFieldFormatter;

public class UserController implements Initializable {

    private static final UserService userService = new UserService();

    private UserModel selectedUser;
    private enum UserState {
        DEFAULT,
        EDITING,
        LOADING
    }

    private final ObjectProperty<UserState> currentState = new SimpleObjectProperty<>(UserState.DEFAULT);

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
        });

        initializeTableColumns();
        initializeEventHandlers();
        setupBindingsControls();

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
        String phoneText = txtPhone.getText() != null ? txtPhone.getText().trim() : null;
        Integer phone = (phoneText != null && !phoneText.isEmpty()) ? Integer.parseInt(phoneText) : null;
        List<String> roles = getSelectedRoles();

        if (selectedUser == null) {
            if (dni.isEmpty() || names.isEmpty() || paternalSurname.isEmpty() || maternalSurname.isEmpty() || email.isEmpty() || password.isEmpty() || roles.isEmpty()) {
                Toaster.showWarning("Por favor, complete todos los campos");
                return;
            }
        }

        UserRequest userRequest = new UserRequest(dni, names, paternalSurname, maternalSurname, email, password, phone, roles);
        saveOrUpdateUser(userRequest);
    }

    private void saveOrUpdateUser(UserRequest userRequest) {
        submitButtonState(true);
        boolean isUpdating = (selectedUser != null);
        UserUpdateRequest userUpdateRequest = new UserUpdateRequest(getSelectedRoles(), chkActive.isSelected());

        String action = isUpdating ? "actualizar" : "registrar";
        CompletableFuture<UserModel> userFuture = isUpdating
            ? userService.updateProfile(userUpdateRequest, selectedUser.getId())
            : userService.register(userRequest);

        userFuture
            .thenAccept(user -> Platform.runLater(() -> {
                submitButtonState(false);
                String messageToast = isUpdating
                    ? "Usuario %s actualizado con éxito".formatted(user.getNames())
                    : "Usuario %s registrado con éxito".formatted(user.getNames());
                Toaster.showSucessOrInfo(messageToast, isUpdating);
                handleReload();
            }))
            .exceptionally(ex -> {
                Platform.runLater(() -> {
                    submitButtonState(false);
                    AlertManager.showErrorMessage("Error al %s el usuario: %s".formatted(action, ex.getCause().getMessage()));
                });
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

        tblUsers.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                UserModel selectedUser = tblUsers.getSelectionModel().getSelectedItem();
                if (selectedUser != null) setUserSelected(selectedUser);
            }
        });
    }

    private void initializeEventHandlers() {
        btnClean.setOnAction(event -> resetForm());
        btnSubmit.setOnMouseClicked(event -> handleSubmit());
    }

    private void setupBindingsControls() {
        btnDelete.visibleProperty().bind(currentState.isEqualTo(UserState.EDITING));

        btnSubmit.disableProperty().bind(currentState.isEqualTo(UserState.LOADING));
        btnSubmit.textProperty().bind(Bindings.createStringBinding(() -> switch (currentState.get()) {
            case EDITING -> "Actualizar";
            case LOADING -> "Cargando...";
            default -> "Registrar";
        }, currentState));

        BooleanBinding isFormModified = txtDNI.textProperty().isNotEmpty()
            .or(txtNames.textProperty().isNotEmpty())
            .or(txtPaternalSurname.textProperty().isNotEmpty())
            .or(txtMaternalSurname.textProperty().isNotEmpty())
            .or(txtEmail.textProperty().isNotEmpty())
            .or(txtHiddenPass.textProperty().isNotEmpty())
            .or(txtPhone.textProperty().isNotEmpty())
            .or(chkActive.selectedProperty())
            .or(Bindings.createBooleanBinding(
                () -> !chkcbRoles.getCheckModel().getCheckedItems().isEmpty(),
                chkcbRoles.getCheckModel().getCheckedItems())
            );
        btnClean.visibleProperty().bind(isFormModified);
    }

    private void setUserSelected(UserModel user) {
        selectedUser = user;

        if (user != null) {
            txtDNI.setText(user.getDni());
            txtNames.setText(user.getNames());
            txtPaternalSurname.setText(user.getPaternalSurname());
            txtMaternalSurname.setText(user.getMaternalSurname());
            txtEmail.setText(user.getEmail());
            txtPhone.setText(user.getPhoneNumber() != null ? user.getPhoneNumber().toString() : null);
            chkActive.setSelected(user.isActive());

            chkcbRoles.getCheckModel().clearChecks();
            for (String role : user.getRoles()) {
                Arrays.stream(UserRole.values())
                    .filter(r -> r.getRole().equals(role))
                    .findFirst().ifPresent(userRole -> chkcbRoles.getCheckModel().check(userRole));
            }

            currentState.set(UserState.EDITING);
        } else {
            resetForm();
        }
    }

    private void resetForm() {
        selectedUser = null;

        txtDNI.clear();
        txtNames.clear();
        txtPaternalSurname.clear();
        txtMaternalSurname.clear();
        txtEmail.clear();
        txtHiddenPass.clear();
        chkcbRoles.getCheckModel().clearChecks();
        txtPhone.clear();
        tblUsers.getSelectionModel().clearSelection();
        chkActive.setSelected(false);

        currentState.set(UserState.DEFAULT);
    }

    private void submitButtonState(boolean isLoading) {
        if (isLoading) {
            currentState.set(UserState.LOADING);
        } else {
            currentState.set(selectedUser != null ? UserState.EDITING : UserState.DEFAULT);
        }
    }

    private void setUsersList(List<UserModel> users) {
        tblUsers.getItems().setAll(users);
    }

    private void handleReload() {
        resetForm();
        fetchUserList();
    }

}
