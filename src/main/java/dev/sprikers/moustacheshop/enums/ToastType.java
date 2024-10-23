package dev.sprikers.moustacheshop.enums;

import lombok.Getter;

/**
 * Enumeración que define los tipos de toast que se pueden mostrar en la aplicación.
 * Cada tipo de toast tiene asociado una clase CSS y un icono SVG.
 *
 * @author SprikerS
 */
@Getter
public enum ToastType {
    SUCCESS("success", "M10 18a8 8 0 100-16 8 8 0 000 16zm3.857-9.809a.75.75 0 00-1.214-.882l-3.483 4.79-1.88-1.88a.75.75 0 10-1.06 1.061l2.5 2.5a.75.75 0 001.137-.089l4-5.5z"),
    INFO("info", "M18 10a8 8 0 11-16 0 8 8 0 0116 0zm-7-4a1 1 0 11-2 0 1 1 0 012 0zM9 9a.75.75 0 000 1.5h.253a.25.25 0 01.244.304l-.459 2.066A1.75 1.75 0 0010.747 15H11a.75.75 0 000-1.5h-.253a.25.25 0 01-.244-.304l.459-2.066A1.75 1.75 0 009.253 9H9z"),
    WARNING("warning", "M9.401 3.003c1.155-2 4.043-2 5.197 0l7.355 12.748c1.154 2-.29 4.5-2.599 4.5H4.645c-2.309 0-3.752-2.5-2.598-4.5L9.4 3.003zM12 8.25a.75.75 0 01.75.75v3.75a.75.75 0 01-1.5 0V9a.75.75 0 01.75-.75zm0 8.25a.75.75 0 100-1.5.75.75 0 000 1.5z"),
    ERROR("error", "M18 10a8 8 0 11-16 0 8 8 0 0116 0zm-8-5a.75.75 0 01.75.75v4.5a.75.75 0 01-1.5 0v-4.5A.75.75 0 0110 5zm0 10a1 1 0 100-2 1 1 0 000 2z");

    private final String cssClass;
    private final String svgPathData;

    /**
     * Constructor de la enumeración ToastType
     *
     * @param cssClass    La clase CSS asociada al tipo de toast
     * @param svgPathData El icono SVG asociado al tipo de toast
     */
    ToastType(String cssClass, String svgPathData) {
        this.cssClass = cssClass;
        this.svgPathData = svgPathData;
    }

}