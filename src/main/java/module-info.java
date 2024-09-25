module dev.sprikers.moustacheshop {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.prefs;


    exports dev.sprikers.moustacheshop.application;
    opens dev.sprikers.moustacheshop.application to javafx.fxml;
    exports dev.sprikers.moustacheshop.controllers;
    opens dev.sprikers.moustacheshop.controllers to javafx.fxml;
}