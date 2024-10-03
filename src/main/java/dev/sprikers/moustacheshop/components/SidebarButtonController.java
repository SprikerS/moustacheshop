package dev.sprikers.moustacheshop.components;

import java.util.Objects;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;

public class SidebarButtonController {

    @FXML
    private Pane active;

    @FXML
    private Button button;

    @FXML
    private HBox container;

    @FXML
    private ImageView image;

    public void setData(SidebarButton sidebarButton) {
        Image icon = new Image(Objects.requireNonNull(getClass().getResourceAsStream(sidebarButton.getIcon())));
        image.setImage(icon);

        active.setVisible(sidebarButton.isActive());
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
        active.setVisible(true);
        container.getStyleClass().add("active");
    }

    public void deactivateButton() {
        active.setVisible(false);
        container.getStyleClass().remove("active");
    }
}
