package dev.sprikers.moustacheshop.utils;

import java.util.concurrent.CompletionException;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Clase de utilidad para analizar respuestas JSON utilizando Jackson.
 *
 * @author SprikerS
 */
public class JsonParserUtils {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Analiza una respuesta JSON y la convierte en un objeto del tipo especificado.
     *
     * @param responseBody El cuerpo de la respuesta JSON como una cadena.
     * @param clazz        La clase del tipo al que se desea convertir el JSON.
     * @param <T>          El tipo de objeto a devolver.
     * @return El objeto convertido del tipo especificado.
     * @throws CompletionException si ocurre un error durante el procesamiento del JSON.
     */
    public static <T> T parseResponse(String responseBody, Class<T> clazz) {
        try {
            return objectMapper.readValue(responseBody, clazz);
        } catch (JsonProcessingException e) {
            throw new CompletionException(e);
        }
    }

    /**
     * Analiza una respuesta JSON y la convierte en un objeto del tipo especificado usando TypeReference.
     *
     * @param responseBody  El cuerpo de la respuesta JSON como una cadena.
     * @param typeReference La referencia de tipo para el tipo al que se desea convertir el JSON.
     * @param <T>           El tipo de objeto a devolver.
     * @return El objeto convertido del tipo especificado.
     * @throws CompletionException si ocurre un error durante el procesamiento del JSON.
     */
    public static <T> T parseResponse(String responseBody, TypeReference<T> typeReference) {
        try {
            return objectMapper.readValue(responseBody, typeReference);
        } catch (JsonProcessingException e) {
            throw new CompletionException(e);
        }
    }

}
