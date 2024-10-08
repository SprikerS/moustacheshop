package dev.sprikers.moustacheshop.components;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.shape.SVGPath;

import dev.sprikers.moustacheshop.utils.SvgLoader;

public class SidebarButtonController {

    @FXML
    private Button button;

    @FXML
    private HBox container;

    @FXML
    private SVGPath svgIcon;

    public void setData(SidebarButton sidebarButton) {
        String svgPathData = SvgLoader.loadSvgPathData(sidebarButton.getIcon());
        svgIcon.setContent(svgPathData);

        button.setGraphic(svgIcon);
        button.setText(sidebarButton.getText());

        container.setOnMouseClicked(event -> handleButtonClick(sidebarButton));
        button.setOnMouseClicked(event -> {
            event.consume();
            handleButtonClick(sidebarButton);
        });
    }

    private void handleButtonClick(SidebarButton sidebarButton) {
        sidebarButton.getAction().run();
        activateButton();
    }

    public void activateButton() {
        button.getStyleClass().add("primary");
    }

    public void deactivateButton() {
        button.getStyleClass().remove("primary");
    }

}
