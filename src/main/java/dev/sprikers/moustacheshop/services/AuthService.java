package dev.sprikers.moustacheshop.services;

import java.net.http.HttpResponse;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import dev.sprikers.moustacheshop.clients.ApiClient;
import dev.sprikers.moustacheshop.dto.JwtResponse;
import dev.sprikers.moustacheshop.dto.LoginRequest;
import dev.sprikers.moustacheshop.utils.JwtPreferencesManager;

public class AuthService {

    private final ApiClient apiClient;

    public AuthService() {
        apiClient = new ApiClient();
    }

    public void login(String email, String password) throws Exception {
        LoginRequest loginRequest = new LoginRequest(email, password);
        ObjectMapper objectMapper = new ObjectMapper();

        HttpResponse<String> response = apiClient.post("/user/login", loginRequest);
        if (response.statusCode() != 200) {
            JsonNode errorResponse = objectMapper.readTree(response.body());
            throw new Exception(errorResponse.get("message").asText());
        }

        JwtResponse jwtResponse = objectMapper.readValue(response.body(), JwtResponse.class);
        String token = jwtResponse.getToken();
        apiClient.setBearerToken(token);
        JwtPreferencesManager.setJwt(token);
    }

}
