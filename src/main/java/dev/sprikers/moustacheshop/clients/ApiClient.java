package dev.sprikers.moustacheshop.clients;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import com.fasterxml.jackson.databind.ObjectMapper;

public class ApiClient {

    private static final String BASE_URL = "http://localhost:3000/api";
    private static HttpClient httpClient;
    private static String bearerToken;

    public ApiClient() {
        httpClient = HttpClient.newBuilder().version(HttpClient.Version.HTTP_2).build();
    }

    public void setBearerToken(String token) {
        bearerToken = token;
    }

    public HttpResponse<String> post(String endpoint, Object requestBody) throws IOException, InterruptedException {
        ObjectMapper objectMapper = new ObjectMapper();
        String json = objectMapper.writeValueAsString(requestBody);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + endpoint))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + bearerToken)
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();

        return httpClient.send(request, HttpResponse.BodyHandlers.ofString());
    }

}
