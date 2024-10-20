module dev.sprikers.moustacheshop {
    requires java.desktop;
    requires java.net.http;
    requires java.prefs;


    requires com.fasterxml.jackson.databind;
    requires com.jfoenix;
    requires javafx.controls;
    requires javafx.fxml;
    requires org.burningwave.core;
    requires org.controlsfx.controls;
    requires static lombok;


    exports dev.sprikers.moustacheshop.application;
    opens dev.sprikers.moustacheshop.application to javafx.fxml;
    exports dev.sprikers.moustacheshop.controllers;
    opens dev.sprikers.moustacheshop.controllers to javafx.fxml;


    exports dev.sprikers.moustacheshop.models to com.fasterxml.jackson.databind;
    opens dev.sprikers.moustacheshop.dto to com.fasterxml.jackson.databind;


    exports dev.sprikers.moustacheshop.components;
    opens dev.sprikers.moustacheshop.components to javafx.fxml, com.fasterxml.jackson.databind;
}