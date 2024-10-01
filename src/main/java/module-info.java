module dev.sprikers.moustacheshop {
    requires java.net.http;
    requires java.prefs;


    requires com.fasterxml.jackson.databind;
    requires javafx.controls;
    requires javafx.fxml;
    requires static lombok;


    exports dev.sprikers.moustacheshop.application;
    opens dev.sprikers.moustacheshop.application to javafx.fxml;
    exports dev.sprikers.moustacheshop.controllers;
    opens dev.sprikers.moustacheshop.controllers to javafx.fxml;


    exports dev.sprikers.moustacheshop.models to com.fasterxml.jackson.databind;
    opens dev.sprikers.moustacheshop.dto to com.fasterxml.jackson.databind;
}