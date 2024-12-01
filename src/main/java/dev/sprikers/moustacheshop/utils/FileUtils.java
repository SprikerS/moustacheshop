package dev.sprikers.moustacheshop.utils;

import java.awt.*;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Clase de utilidad para la gestión de archivos y carpetas en el sistema de archivos local.
 * Proporciona métodos para crear carpetas, guardar archivos y garantizar la existencia de directorios base.
 *
 * @author SprikerS
 */
public class FileUtils {

    // Nombre de la carpeta principal en donde se almacenarán los archivos
    private static final String FOLDER_BASE = "moustacheshop";

    /**
     * Obtiene la ruta del directorio base de la aplicación dentro de la carpeta "Documents" del usuario.
     * Si el directorio base no existe, se crea automáticamente.
     *
     * @return Ruta del directorio base.
     */
    private static Path getBaseFolderPath() {
        String userHome = System.getProperty("user.home");
        Path basePath = Paths.get(userHome, "Documents", FOLDER_BASE);
        ensureFolderExists(basePath);
        return basePath;
    }

    /**
     * Garantiza que un directorio especificado exista en el sistema de archivos.
     * Si no existe, intenta crearlo.
     *
     * @param folderPath Ruta del directorio a verificar o crear.
     * @throws RuntimeException si ocurre un error al crear el directorio.
     */
    private static void ensureFolderExists(Path folderPath) {
        if (Files.exists(folderPath))
            return;

        try {
            Files.createDirectories(folderPath);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Crea una subcarpeta dentro del directorio base de la aplicación.
     * Si la subcarpeta ya existe, no realiza ninguna acción.
     *
     * @param folderName Nombre de la subcarpeta a crear.
     * @return Ruta de la subcarpeta creada.
     * @throws IllegalArgumentException si el nombre de la subcarpeta es nulo o está vacío.
     */
    public static Path createSubfolder(String folderName) {
        if (folderName == null || folderName.isEmpty())
            throw new IllegalArgumentException("El nombre de la carpeta no puede ser nulo o vacío");

        Path subfolderPath = getBaseFolderPath().resolve(folderName);
        ensureFolderExists(subfolderPath);
        return subfolderPath;
    }

    /**
     * Guarda un archivo en el directorio base o en una subcarpeta específica.
     * Si la subcarpeta no existe, se crea automáticamente.
     *
     * @param folderName Nombre de la subcarpeta donde se guardará el archivo. Si es nulo, se usará el directorio base.
     * @param fileName   Nombre del archivo a guardar.
     * @param content    Contenido del archivo en forma de arreglo de bytes.
     * @throws IOException              si ocurre un error al escribir el archivo.
     * @throws IllegalArgumentException si el nombre del archivo o el contenido son nulos o vacíos.
     */
    public static void saveFile(String folderName, String fileName, byte[] content) throws IOException {
        if (fileName == null || fileName.isEmpty())
            throw new IllegalArgumentException("El nombre del archivo no puede ser nulo o vacío");
        if (content == null || content.length == 0)
            throw new IllegalArgumentException("El contenido del archivo no puede ser nulo o vacío");

        Path targetFolder = folderName == null ? getBaseFolderPath() : createSubfolder(folderName);
        Path filePath = targetFolder.resolve(fileName);

        try (FileOutputStream fos = new FileOutputStream(filePath.toFile())) {
            fos.write(content);
        }
    }

    /**
     * Abre un archivo previamente guardado en el directorio base o en una subcarpeta específica.
     * Si la subcarpeta no existe, se intentará crear automáticamente.
     *
     * @param folderName Nombre de la subcarpeta donde se encuentra el archivo. Si es nulo, se usará el directorio base.
     * @param fileName   Nombre del archivo a abrir.
     * @throws IllegalArgumentException si el nombre del archivo es nulo o vacío, o si el archivo no existe.
     */
    public static void openSavedFile(String folderName, String fileName) {
        if (fileName == null || fileName.isEmpty()) {
            throw new IllegalArgumentException("El nombre del archivo no puede ser nulo o vacío");
        }

        Path targetFolder = folderName == null ? getBaseFolderPath() : createSubfolder(folderName);
        Path filePath = targetFolder.resolve(fileName);

        File file = filePath.toFile();

        if (!file.exists()) {
            throw new IllegalArgumentException("El archivo no existe: " + file.getAbsolutePath());
        }

        try {
            if (Desktop.isDesktopSupported()) {
                Desktop desktop = Desktop.getDesktop();
                desktop.open(file);
            } else {
                System.err.println("El entorno no soporta operaciones de escritorio.");
            }
        } catch (IOException e) {
            System.err.println("Error al abrir el archivo: " + e.getMessage());
        }
    }

}
