module dev.sprikers.moustacheshop {
    requires java.net.http;
    requires java.prefs;


    requires com.fasterxml.jackson.databind;
    requires com.jfoenix;
    requires javafx.controls;
    requires javafx.fxml;
    requires org.burningwave.core;
    requires org.controlsfx.controls;
    requires static lombok;


    exports dev.sprikers.moustacheshop.aplicacion;
    opens dev.sprikers.moustacheshop.aplicacion to javafx.fxml;
    exports dev.sprikers.moustacheshop.controladores;
    opens dev.sprikers.moustacheshop.controladores to javafx.fxml;


    exports dev.sprikers.moustacheshop.modelos to com.fasterxml.jackson.databind;


    exports dev.sprikers.moustacheshop.componentes;
    opens dev.sprikers.moustacheshop.componentes to javafx.fxml, com.fasterxml.jackson.databind;
}