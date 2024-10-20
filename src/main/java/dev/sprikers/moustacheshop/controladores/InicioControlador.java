package dev.sprikers.moustacheshop.controladores;

import java.io.IOException;
import java.net.URL;
import java.util.*;

import com.jfoenix.controls.JFXButton;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import dev.sprikers.moustacheshop.aplicacion.Main;
import dev.sprikers.moustacheshop.componentes.BotonMenu;
import dev.sprikers.moustacheshop.componentes.BotonMenuControlador;
import dev.sprikers.moustacheshop.constantes.RutaComponentes;
import dev.sprikers.moustacheshop.constantes.RutaSVG;
import dev.sprikers.moustacheshop.constantes.RutaVistas;
import dev.sprikers.moustacheshop.utilidades.*;

public class InicioControlador implements Initializable {

    private final Map<String, Parent> pageCache = new HashMap<>();
    private final List<BotonMenuControlador> menuControladorArrayList = new ArrayList<>();
    private final VentanaArrastrar ventanaArrastrar = new VentanaArrastrar();

    @FXML
    private AnchorPane ap;

    @FXML
    private BorderPane bp;

    @FXML
    private HBox hbTituloBarra;

    @FXML
    private ImageView btnCerrar, btnMinimizar;

    @FXML
    private JFXButton btnCerrarSession;

    @FXML
    private Label lblFecha, lblHora;

    @FXML
    private VBox sidebarVBox;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        FechaHoraActualizador.configureDateTimeLabels(lblFecha, lblHora);
        Platform.runLater(() -> ventanaArrastrar.enableWindowDragging(hbTituloBarra));

        btnCerrarSession.setOnMouseClicked(this::salir);
        btnCerrar.setOnMouseClicked(event -> System.exit(0));
        btnMinimizar.setOnMouseClicked(event -> ((Stage) btnMinimizar.getScene().getWindow()).setIconified(true));

        cargarMenuLateral();
    }

    private void cargarMenuLateral() {
        List<BotonMenu> botonMenus = cargarBotones();

        int i = 0;
        for (BotonMenu sb : botonMenus) {
            FXMLLoader fxml = new FXMLLoader();
            fxml.setLocation(getClass().getResource(RutaComponentes.SIDEBAR_BUTTON));
            try {
                JFXButton jfxButton = fxml.load();
                BotonMenuControlador controller = fxml.getController();
                controller.setData(sb);
                if (i == 0) controller.activateButton();
                sidebarVBox.getChildren().add(jfxButton);
                menuControladorArrayList.add(controller);
            } catch (IOException e) {
                System.out.println("Error al cargar la vista del boton: " + e.getMessage());
            }
            i++;
        }
    }

    private List<BotonMenu> cargarBotones() {
        List<BotonMenu> lista = new ArrayList<>();

        BotonMenu sbInicio = new BotonMenu(RutaSVG.LAYOUT_GRID, "Inicio", () -> setView(RutaVistas.HOME));
        BotonMenu sbOrdenes = new BotonMenu(RutaSVG.NOTEBOOK_TEXT, "Ordenes", () -> setView(RutaVistas.ORDERS));
        BotonMenu sbUsuarios = new BotonMenu(RutaSVG.USERS_ROUND, "Usuarios", () -> setView(RutaVistas.USERS));
        BotonMenu sbProductos = new BotonMenu(RutaSVG.PACKAGE, "Productos", () -> setView(RutaVistas.PRODUCTS));
        BotonMenu sbAjustes = new BotonMenu(RutaSVG.BOLT, "Configuración", () -> setView(RutaVistas.SETTINGS));

        lista.add(sbInicio);
        lista.add(sbOrdenes);
        lista.add(sbProductos);
        lista.add(sbUsuarios);
        lista.add(sbAjustes);
        return lista;
    }

    private void salir(MouseEvent event) {
        try {
            Main.cambiarStage(RutaVistas.AUTH, event);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    private void setView(String view) {
        reiniciarBotones();

        if (view.equals(RutaVistas.HOME)) {
            bp.setCenter(ap);
        } else {
            cargarVista(view);
        }
    }

    private void reiniciarBotones() {
        for (BotonMenuControlador controller : menuControladorArrayList) {
            controller.deactivateButton();
        }
    }

    private void cargarVista(String view) {
        Parent cachedRoot = pageCache.get(view);
        if (cachedRoot != null) {
            bp.setCenter(cachedRoot);
            return;
        }

        Task<Parent> cargarTarea = new Task<>() {
            @Override
            protected Parent call() throws Exception {
                return FXMLLoader.load(Objects.requireNonNull(getClass().getResource(view)));
            }
        };

        cargarTarea.setOnSucceeded(event -> {
            Parent loadedRoot = cargarTarea.getValue();
            pageCache.put(view, loadedRoot);
            bp.setCenter(loadedRoot);
        });

        cargarTarea.setOnFailed(event -> {
            Throwable error = cargarTarea.getException();
            System.out.println("Error en la vista: " + error.getMessage());
        });

        new Thread(cargarTarea).start();
    }

}
