package dev.sprikers.moustacheshop.clients;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

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

    public HttpResponse<String> get(String endpoint) throws Exception {
        HttpResponse<String> response = sendRequest(endpoint, "GET", null);
        return handleResponse(response);
    }

    public HttpResponse<String> post(String endpoint, Object requestBody) throws Exception {
        HttpResponse<String> response = sendRequest(endpoint, "POST", requestBody);
        return handleResponse(response);
    }

    public HttpResponse<String> patch(String endpoint, Object requestBody) throws Exception {
        HttpResponse<String> response = sendRequest(endpoint, "PATCH", requestBody);
        return handleResponse(response);
    }

    public HttpResponse<String> delete(String endpoint) throws Exception {
        HttpResponse<String> response = sendRequest(endpoint, "DELETE", null);
        return handleResponse(response);
    }

    private HttpResponse<String> sendRequest(String endpoint, String method, Object requestBody) throws Exception {
        return ApiExceptionHandler.handleApiCall(() -> {
            HttpRequest.Builder requestBuilder = HttpRequest.newBuilder()
                    .uri(URI.create(endpoint))
                    .header("Authorization", "Bearer " + getBearerToken());

            if (requestBody != null) {
                String body = objectMapper.writeValueAsString(requestBody);
                requestBuilder
                        .header("Content-Type", "application/json")
                        .method(method, HttpRequest.BodyPublishers.ofString(body));
            } else {
                requestBuilder.method(method, HttpRequest.BodyPublishers.noBody());
            }

            HttpRequest request = requestBuilder.build();
            return httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        });
    }

    private HttpResponse<String> handleResponse(HttpResponse<String> response) throws Exception {
        int statusCode = response.statusCode();
        if (statusCode < 200 || statusCode > 300) {
            JsonNode errorResponse = objectMapper.readTree(response.body());
            throw new Exception(errorResponse.get("message").asText());
        }
        return response;
    }

}