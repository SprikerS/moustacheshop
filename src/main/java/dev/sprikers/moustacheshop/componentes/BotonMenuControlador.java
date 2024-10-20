package dev.sprikers.moustacheshop.componentes;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.shape.SVGPath;

import dev.sprikers.moustacheshop.utilidades.SvgCargador;

public class BotonMenuControlador {

    @FXML
    private Button button;

    @FXML
    private SVGPath svgIcon;

    public void setData(BotonMenu botonMenu) {
        String svgPathData = SvgCargador.loadSvgPathData(botonMenu.getIcon());
        svgIcon.setContent(svgPathData);

        button.setGraphic(svgIcon);
        button.setText(botonMenu.getText());
        button.setOnMouseClicked(event -> handleButtonClick(botonMenu));
    }

    private void handleButtonClick(BotonMenu botonMenu) {
        botonMenu.getAction().run();
        activateButton();
    }

    public void activateButton() {
        button.getStyleClass().add("primary");
    }

    public void deactivateButton() {
        button.getStyleClass().remove("primary");
    }

}
