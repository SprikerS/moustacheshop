package dev.sprikers.moustacheshop.exceptions;

import java.io.IOException;
import java.net.ConnectException;

import dev.sprikers.moustacheshop.interfaces.ApiCallable;

public class ApiExceptionHandler {
    public static <T> T handleApiCall(ApiCallable<T> callable) throws Exception {
        try {
            return callable.call();
        } catch (InterruptedException e) {
            throw new IOException("La petición fue interrumpida");
        } catch (ConnectException e) {
            throw new IOException("No se puede conectar al servidor, verifique su conexión a internet o si el servidor está en ejecución");
        } catch (IOException e) {
            throw new IOException("Ocurrió un error al comunicarse con el servidor");
        }
    }
}
