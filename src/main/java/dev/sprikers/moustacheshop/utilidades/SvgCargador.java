package dev.sprikers.moustacheshop.utilidades;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Clase de utilidad para cargar y extraer datos de archivos SVG.
 *
 * @author SprikerS
 */
public class SvgCargador {

    /**
     * Carga el contenido de un archivo SVG desde el classpath.
     *
     * @param path La ruta del archivo SVG.
     * @return El contenido del archivo SVG como una cadena.
     * @throws IllegalArgumentException si el archivo SVG no se encuentra.
     */
    private static String loadSvgFromFile(String path) {
        try (
                InputStream inputStream = Objects.requireNonNull(SvgCargador.class.getResourceAsStream(path),"SVG file not found: " + path);
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))
        ) {
            return reader.lines().collect(Collectors.joining("\n"));
        } catch (IOException e) {
            throw new RuntimeException("Failed to load SVG file: " + path, e);
        }
    }

    /**
     * Extrae los datos del atributo "d" de los elementos <path> del contenido SVG.
     *
     * @param svgContent El contenido del archivo SVG.
     * @return Una cadena con los datos de los caminos.
     */
    private static String extractPathData(String svgContent) {
        Pattern pattern = Pattern.compile("<path[^>]+d=\"([^\"]+)\"");
        Matcher matcher = pattern.matcher(svgContent);
        StringBuilder pathData = new StringBuilder();

        while (matcher.find()) {
            pathData.append(matcher.group(1)).append("\n");
        }

        return pathData.toString().trim();
    }

    /**
     * Carga y extrae los datos del camino de un archivo SVG.
     *
     * @param path La ruta del archivo SVG.
     * @return Los datos del camino extraídos del archivo SVG.
     */
    public static String loadSvgPathData(String path) {
        String svgContent = loadSvgFromFile(path);
        return extractPathData(svgContent);
    }

}
