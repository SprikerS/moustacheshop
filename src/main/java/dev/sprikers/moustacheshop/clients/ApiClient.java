package dev.sprikers.moustacheshop.clients;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import com.fasterxml.jackson.databind.ObjectMapper;

import dev.sprikers.moustacheshop.exceptions.ApiExceptionHandler;
import dev.sprikers.moustacheshop.utils.JwtPreferencesManager;

public class ApiClient {

    private static HttpClient httpClient;

    public ApiClient() {
        httpClient = HttpClient.newBuilder().version(HttpClient.Version.HTTP_2).build();
    }

    private String getBearerToken() {
        return JwtPreferencesManager.getJwt() != null ? JwtPreferencesManager.getJwt() : "";
    }

    public HttpResponse<String> post(String endpoint, Object requestBody) throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        String json = objectMapper.writeValueAsString(requestBody);

        return ApiExceptionHandler.handleApiCall(() -> {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(endpoint))
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + getBearerToken())
                    .POST(HttpRequest.BodyPublishers.ofString(json))
                    .build();
            return httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        });
    }

    public HttpResponse<String> get(String endpoint) throws Exception {
        return ApiExceptionHandler.handleApiCall(() -> {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(endpoint))
                    .header("Authorization", "Bearer " + getBearerToken())
                    .GET()
                    .build();
            return httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        });
    }

}
