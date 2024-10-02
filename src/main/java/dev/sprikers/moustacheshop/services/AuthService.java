package dev.sprikers.moustacheshop.services;

import java.io.IOException;
import java.net.http.HttpResponse;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import dev.sprikers.moustacheshop.clients.ApiClient;
import dev.sprikers.moustacheshop.constants.ApiEndpoints;
import dev.sprikers.moustacheshop.dto.JwtResponse;
import dev.sprikers.moustacheshop.dto.LoginRequest;
import dev.sprikers.moustacheshop.models.UserModel;
import dev.sprikers.moustacheshop.utils.JwtPreferencesManager;
import dev.sprikers.moustacheshop.utils.UserSession;

public class AuthService {

    private final ApiClient apiClient;

    public AuthService() {
        apiClient = new ApiClient();
    }

    public void login(String email, String password) throws Exception {
        LoginRequest loginRequest = new LoginRequest(email, password);
        ObjectMapper objectMapper = new ObjectMapper();

        try {
            HttpResponse<String> response = apiClient.post(ApiEndpoints.AUTH_LOGIN, loginRequest);
            if (response.statusCode() != 200) {
                JsonNode errorResponse = objectMapper.readTree(response.body());
                throw new Exception(errorResponse.get("message").asText());
            }

            JwtResponse jwtResponse = objectMapper.readValue(response.body(), JwtResponse.class);
            JwtPreferencesManager.setJwt(jwtResponse.getToken());
        } catch (IOException e) {
            throw new Exception(e.getMessage());
        }
    }

    public void loadUserInfo() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();

        try {
            HttpResponse<String> response = apiClient.get(ApiEndpoints.CHECK_AUTH_STATUS);
            if (response.statusCode() != 200) {
                JsonNode errorResponse = objectMapper.readTree(response.body());
                throw new Exception(errorResponse.get("message").asText());
            }

            UserModel user = objectMapper.readValue(response.body(), UserModel.class);
            UserSession.getInstance().setUserModel(user);
            JwtPreferencesManager.setJwt(user.getToken());
        } catch (IOException e) {
            throw new Exception(e.getMessage());
        }
    }

}
