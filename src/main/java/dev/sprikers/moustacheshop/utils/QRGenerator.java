package dev.sprikers.moustacheshop.utils;

import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;

/**
 * Clase de utilidad para generar imágenes de códigos QR a partir de texto.
 * Utiliza la librería ZXing para la generación del código y convierte el resultado
 * en un formato compatible con JavaFX para su visualización en la interfaz.
 *
 * @author SprikerS
 */
public class QRGenerator {

    /**
     * Genera una imagen de código QR a partir de un texto dado.
     *
     * @param text   El contenido que se codificará en el QR (por ejemplo, un enlace).
     * @param width  Ancho del código QR en píxeles.
     * @param height Altura del código QR en píxeles.
     * @return Una instancia de {@link Image} que contiene el código QR generado.
     * @throws WriterException Si ocurre un error al codificar el contenido.
     */
    public static Image fromText(String text, int width, int height) throws WriterException {
        QRCodeWriter qrCodeWriter = new QRCodeWriter();

        Map<EncodeHintType, Object> hints = new HashMap<>();
        hints.put(EncodeHintType.MARGIN, 0);

        BitMatrix bitMatrix = qrCodeWriter.encode(text, BarcodeFormat.QR_CODE, width, height, hints);
        BufferedImage bufferedImage = MatrixToImageWriter.toBufferedImage(bitMatrix);

        return SwingFXUtils.toFXImage(bufferedImage, null);
    }

}
