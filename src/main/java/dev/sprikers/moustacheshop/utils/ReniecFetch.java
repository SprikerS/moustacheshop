package dev.sprikers.moustacheshop.utils;

import javafx.application.Platform;
import javafx.scene.control.TextField;
import javafx.scene.layout.Region;

import dev.sprikers.moustacheshop.components.Toaster;
import dev.sprikers.moustacheshop.models.ReniecModel;
import dev.sprikers.moustacheshop.services.UserService;

/**
 * Clase de utilidad para manejar la obtención de datos de RENIEC y la actualización de campos de texto.
 * Proporciona métodos para configurar los campos y obtener datos de usuario desde un servicio.
 *
 * @author SprikerS
 */
public class ReniecFetch {

    private static final UserService userService = new UserService();

    private Region btnReniec;
    private TextField txtDNI, txtNames, txtPaternalSurname, txtMaternalSurname;

    private ReniecFetch() {}

    /**
     * Configura los campos y el botón para realizar la consulta a RENIEC.
     *
     * @param button Componente de botón que inicia la búsqueda.
     * @param dni Campo de texto para el DNI del usuario.
     * @param names Campo de texto para los nombres del usuario.
     * @param paternal Campo de texto para el apellido paterno del usuario.
     * @param maternal Campo de texto para el apellido materno del usuario.
     */
    public static void configureFields(Region button, TextField dni, TextField names, TextField paternal, TextField maternal) {
        ReniecFetch instance = new ReniecFetch();
        instance.btnReniec = button;
        instance.txtDNI = dni;
        instance.txtNames = names;
        instance.txtPaternalSurname = paternal;
        instance.txtMaternalSurname = maternal;

        instance.initialize();
    }

    /**
     * Inicializa la configuración del botón y los campos de texto.
     */
    private void initialize() {
        SpinnerLoader spinner = new SpinnerLoader();
        spinner.setNode(btnReniec);

        btnReniec.setOnMouseClicked(event -> handleFetchReniec(spinner));
        btnReniec.visibleProperty().bind(txtDNI.textProperty().length().isEqualTo(8));

        TextFieldFormatter.applyIntegerFormat(txtDNI, 8);
    }

    /**
     * Maneja la solicitud de datos a RENIEC cuando se hace clic en el botón.
     *
     * @param spinner Spinner para mostrar la carga.
     */
    private void handleFetchReniec(SpinnerLoader spinner) {
        spinner.start();
        String dni = txtDNI.getText().trim();

        userService.reniec(dni)
            .thenAccept(reniec -> populateFieldsReniec(reniec, spinner))
            .exceptionally(ex -> {
                AlertManager.showErrorMessage("No se pudo obtener los datos de reniec: %s".formatted(ex.getCause().getMessage()));
                return null;
            });
    }

    /**
     * Rellena los campos de texto con la información obtenida de RENIEC.
     *
     * @param reniec Datos obtenidos de RENIEC.
     * @param spinner Spinner para detener la carga.
     */
    private void populateFieldsReniec(ReniecModel reniec, SpinnerLoader spinner) {
        spinner.stop();
        Toaster.showInfo("Datos obtenidos de la RENIEC con éxito");
        Platform.runLater(() -> {
            txtMaternalSurname.setText(reniec.getApellidoMaterno());
            txtNames.setText(reniec.getNombres());
            txtPaternalSurname.setText(reniec.getApellidoPaterno());
        });
    }

}
