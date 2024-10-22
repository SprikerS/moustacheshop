package dev.sprikers.moustacheshop.utils;

import java.awt.Toolkit;
import java.util.function.UnaryOperator;

import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;

/**
 * Clase de utilidad para aplicar formatos específicos a campos de texto (TextField) en JavaFX.
 * Proporciona métodos para aplicar filtros numéricos y decimales con validaciones personalizadas.
 *
 * @author SprikerS
 */
public class TextFieldFormatter {

    /**
     * Aplica un filtro que permite solo números enteros, limitando la longitud de la entrada.
     *
     * @param textField El campo de texto al que se aplicará el filtro.
     * @param maxLength La cantidad máxima de dígitos permitidos.
     */
    public static void applyNumericFormat(TextField textField, int maxLength) {
        UnaryOperator<TextFormatter.Change> numericFilter = change -> {
            String newText = change.getControlNewText();
            if (!isValidPart(newText, maxLength)) {
                soundAlert();
                return null;
            }

            return change;
        };

        textField.setTextFormatter(new TextFormatter<>(numericFilter));
    }

    /**
     * Aplica un filtro que permite solo números decimales, limitando la longitud de la parte entera.
     *
     * @param textField El campo de texto al que se aplicará el filtro.
     * @param maxDecimalLength La cantidad máxima de dígitos decimales permitidos.
     */
    public static void applyDecimalFormat(TextField textField, int maxDecimalLength) {
        applyDecimalFormat(textField, maxDecimalLength, Integer.MAX_VALUE);
    }

    /**
     * Aplica un filtro que permite solo números decimales, limitando la longitud de la parte entera y decimal.
     *
     * @param textField        El campo de texto al que se aplicará el filtro.
     * @param maxDecimalLength La cantidad máxima de dígitos decimales permitidos.
     * @param maxIntegerLength La cantidad máxima de dígitos enteros permitidos.
     */
    public static void applyDecimalFormat(TextField textField, int maxDecimalLength, int maxIntegerLength) {
        UnaryOperator<TextFormatter.Change> decimalFilter = change -> {
            String newText = change.getControlNewText();
            String[] parts = newText.split("\\.");

            // Validar si la entrada comienza con un punto o si contiene más de un punto decimal
            if (newText.startsWith(".") || newText.chars().filter(ch -> ch == '.').count() > 1) {
                soundAlert();
                return null;
            }

            // Validar parte entera: debe ser un número y no puede estar vacío
            boolean isValid = isValidPart(parts[0], maxIntegerLength);

            // Validar parte decimal (si existe)
            if (parts.length == 2)
                isValid = isValid && isValidPart(parts[1], maxDecimalLength);

            // Si la entrada no es válida, reproducir un sonido de alerta y no aplicar el cambio
            if (!isValid) {
                soundAlert();
                return null;
            }

            return change;
        };

        textField.setTextFormatter(new TextFormatter<>(decimalFilter));
    }

    /**
     * Valida si una parte de un número es válida.
     *
     * @param part La parte del número a validar.
     * @param maxLength La longitud máxima permitida.
     * @return {@code true} si la parte es válida, {@code false} en caso contrario.
     */
    private static boolean isValidPart(String part, int maxLength) {
        return isDigits(part) && part.length() <= maxLength;
    }

    /**
     * Valida si una cadena contiene solo dígitos.
     *
     * @param str La cadena a validar.
     * @return {@code true} si la cadena contiene solo dígitos, {@code false} en caso contrario.
     */
    private static boolean isDigits(String str) {
        return str.chars().allMatch(Character::isDigit);
    }

    /**
     * Reproduce un sonido de alerta.
     */
    private static void soundAlert() {
        Toolkit.getDefaultToolkit().beep();
    }

}
