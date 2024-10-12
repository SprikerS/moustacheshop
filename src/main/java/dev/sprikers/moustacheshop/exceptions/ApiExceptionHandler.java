package dev.sprikers.moustacheshop.exceptions;

import java.io.IOException;
import java.net.ConnectException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

import dev.sprikers.moustacheshop.interfaces.ApiCallable;
import dev.sprikers.moustacheshop.interfaces.AsyncApiCallable;

public class ApiExceptionHandler {

    public static <T> T handleApiCall(ApiCallable<T> callable) throws Exception {
        try {
            return callable.call();
        } catch (Exception e) {
            Throwable cause = e.getCause() != null ? e.getCause() : e;
            throw new Exception(handleCommonExceptions(cause), e);
        }
    }

    public static <T> CompletableFuture<T> handleApiCallAsync(AsyncApiCallable<T> callable) {
        return callable.call().exceptionally(ex -> {
            Throwable cause = ex.getCause() != null ? ex.getCause() : ex;
            String errorMessage = handleCommonExceptions(cause);
            throw new CompletionException(new Exception(errorMessage, cause));
        });
    }

    private static String handleCommonExceptions(Throwable cause) {
        return switch (cause) {
            case ApiException apiException -> apiException.getMessage();
            case ConnectException ignored -> "No se puede conectar al servidor, verifique su conexión a internet o si el servidor está en ejecución";
            case IOException ignored -> "Ocurrió un error al comunicarse con el servidor";
            case InterruptedException ignored -> "La petición fue interrumpida";
            case null -> "Error desconocido: No hay información adicional";
            default -> "Error desconocido: " + cause.getMessage();
        };
    }

}
