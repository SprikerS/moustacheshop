package dev.sprikers.moustacheshop.clients;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.concurrent.CompletableFuture;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import dev.sprikers.moustacheshop.exceptions.ApiExceptionHandler;
import dev.sprikers.moustacheshop.utils.JwtPreferencesManager;

public class ApiClient {

    private static final HttpClient httpClient = HttpClient.newBuilder().version(HttpClient.Version.HTTP_2).build();
    private static final ObjectMapper objectMapper = new ObjectMapper();

    private String getBearerToken() {
        return JwtPreferencesManager.getJwt() != null ? JwtPreferencesManager.getJwt() : "";
    }

    private HttpRequest buildHttpRequest(String endpoint, String method, Object requestBody) {
        HttpRequest.Builder requestBuilder = HttpRequest.newBuilder()
            .uri(URI.create(endpoint))
            .header("Authorization", "Bearer " + getBearerToken());

        if (requestBody != null) {
            String body;
            try {
                body = objectMapper.writeValueAsString(requestBody);
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
            requestBuilder
                .header("Content-Type", "application/json")
                .method(method, HttpRequest.BodyPublishers.ofString(body));
        } else {
            requestBuilder.method(method, HttpRequest.BodyPublishers.noBody());
        }

        return requestBuilder.build();
    }

    private HttpResponse<String> sendRequest(HttpRequest request) throws Exception {
        return ApiExceptionHandler.handleApiCall(() -> {
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            return handleResponse(response);
        });
    }

    private CompletableFuture<HttpResponse<String>> sendRequestAsync(HttpRequest request) {
        return httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString()).thenCompose(this::handleResponseAsync);
    }

    private Exception buildApiException(HttpResponse<String> response) throws JsonProcessingException {
        JsonNode errorResponse = objectMapper.readTree(response.body());
        return new Exception(errorResponse.get("message").asText());
    }

    private HttpResponse<String> handleResponse(HttpResponse<String> response) throws Exception {
        int statusCode = response.statusCode();
        if (statusCode < 200 || statusCode >= 300) throw buildApiException(response);
        return response;
    }

    private CompletableFuture<HttpResponse<String>> handleResponseAsync(HttpResponse<String> response) {
        int statusCode = response.statusCode();
        if (statusCode < 200 || statusCode >= 300) {
            try {
                return CompletableFuture.failedFuture(buildApiException(response));
            } catch (JsonProcessingException e) {
                return CompletableFuture.failedFuture(new RuntimeException("Error processing JSON response", e));
            }
        }
        return CompletableFuture.completedFuture(response);
    }

    /**
     * Metodos para enviar peticiones HTTP de manera síncrona y asíncrona
     *
    **/

    private HttpResponse<String> sendSyncRequest(String endpoint, String method, Object requestBody) throws Exception {
        HttpRequest request = buildHttpRequest(endpoint, method, requestBody);
        return sendRequest(request);
    }

    private CompletableFuture<HttpResponse<String>> sendAsyncRequest(String endpoint, String method, Object requestBody) {
        try {
            HttpRequest request = buildHttpRequest(endpoint, method, requestBody);
            return sendRequestAsync(request);
        } catch (Exception e) {
            return CompletableFuture.failedFuture(e);
        }
    }

    /**
     * Metodos para realizar peticiones GET, POST, PATCH y DELETE de manera síncrona
     *
    **/

    public HttpResponse<String> get(String endpoint) throws Exception {
        return sendSyncRequest(endpoint, "GET", null);
    }

    public HttpResponse<String> post(String endpoint, Object requestBody) throws Exception {
        return sendSyncRequest(endpoint, "POST", requestBody);
    }

    public HttpResponse<String> patch(String endpoint, Object requestBody) throws Exception {
        return sendSyncRequest(endpoint, "PATCH", requestBody);
    }

    public HttpResponse<String> delete(String endpoint) throws Exception {
        return sendSyncRequest(endpoint, "DELETE", null);
    }

    /**
     * Metodos para realizar peticiones GET, POST, PATCH y DELETE de manera asíncrona
     *
    **/

    public CompletableFuture<HttpResponse<String>> getAsync(String endpoint) {
        return sendAsyncRequest(endpoint, "GET", null);
    }

    public CompletableFuture<HttpResponse<String>> postAsync(String endpoint, Object requestBody) {
        return sendAsyncRequest(endpoint, "POST", requestBody);
    }

    public CompletableFuture<HttpResponse<String>> patchAsync(String endpoint, Object requestBody) {
        return sendAsyncRequest(endpoint, "PATCH", requestBody);
    }

    public CompletableFuture<HttpResponse<String>> deleteAsync(String endpoint) {
        return sendAsyncRequest(endpoint, "DELETE", null);
    }

}