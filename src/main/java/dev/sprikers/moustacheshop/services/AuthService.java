package dev.sprikers.moustacheshop.services;

import java.io.IOException;
import java.net.http.HttpResponse;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import dev.sprikers.moustacheshop.clients.ApiClient;
import dev.sprikers.moustacheshop.constants.ApiEndpoints;
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
        try {
            LoginRequest loginRequest = new LoginRequest(email, password);
            HttpResponse<String> response = apiClient.post(ApiEndpoints.AUTH_LOGIN, loginRequest);
            handleResponse(response);
        } catch (IOException e) {
            throw new Exception(e.getMessage());
        }
    }

    public void fetchUserInfo() throws Exception {
        try {
            HttpResponse<String> response = apiClient.get(ApiEndpoints.CHECK_AUTH_STATUS);
            handleResponse(response);
        } catch (IOException e) {
            throw new Exception(e.getMessage());
        }
    }

    private void handleResponse(HttpResponse<String> response) throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();

        if (response.statusCode() != 200) {
            JsonNode errorResponse = objectMapper.readTree(response.body());
            throw new Exception(errorResponse.get("message").asText());
        }

        UserModel user = objectMapper.readValue(response.body(), UserModel.class);
        UserSession.getInstance().setUserModel(user);
        JwtPreferencesManager.setJwt(user.getToken());
    }

}
