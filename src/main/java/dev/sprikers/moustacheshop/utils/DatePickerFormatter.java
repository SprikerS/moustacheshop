package dev.sprikers.moustacheshop.utils;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

import javafx.scene.control.DateCell;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.util.StringConverter;

/**
 * Clase de utilidad para formatear y manejar la selección de fechas en un componente DatePicker.
 * Incluye la funcionalidad para:
 * - Deshabilitar fechas futuras.
 * - Formatear la fecha seleccionada a un formato personalizado.
 * - Actualizar la visualización de la fecha en un Label con un formato largo.
 *
 * @author SprikerS
 */
public class DatePickerFormatter {

    public static final DateTimeFormatter orderFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd / MM / yyyy");
    private static final DateTimeFormatter longDateFormatter = DateTimeFormatter.ofPattern("EEEE, d 'de' MMMM 'de' yyyy", Locale.forLanguageTag("es-ES"));

    /**
     * Configura el DatePicker para:
     * - Deshabilitar fechas posteriores al día actual.
     * - Establecer un formato personalizado para la fecha seleccionada.
     * - Actualizar un Label con la fecha en un formato largo.
     * - Establecer la fecha actual como valor inicial.
     *
     * @param datePicker Componente DatePicker donde el usuario seleccionará la fecha.
     * @param labelDate Componente Label donde se mostrará la fecha en formato largo.
     */
    public static void configureDatePicker(DatePicker datePicker, Label labelDate) {
        datePicker.setDayCellFactory(picker -> new DateCell() {
            @Override
            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                if (date.isAfter(LocalDate.now())) {
                    setDisable(true);
                }
            }
        });

        datePicker.setConverter(new StringConverter<>() {
            @Override
            public String toString(LocalDate date) {
                return date != null ? date.format(dateFormatter) : "";
            }

            @Override
            public LocalDate fromString(String string) {
                return string != null && !string.isEmpty() ? LocalDate.parse(string, dateFormatter) : null;
            }
        });

        datePicker.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                String formattedDate = newValue.format(longDateFormatter);
                formattedDate = formattedDate.substring(0, 1).toUpperCase() + formattedDate.substring(1);
                labelDate.setText(formattedDate);
            } else {
                labelDate.setText("");
            }
        });

        datePicker.setValue(LocalDate.now());
    }

}
