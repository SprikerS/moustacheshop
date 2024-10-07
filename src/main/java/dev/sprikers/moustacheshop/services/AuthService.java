package dev.sprikers.moustacheshop.services;

import java.net.http.HttpResponse;

import com.fasterxml.jackson.databind.ObjectMapper;

import dev.sprikers.moustacheshop.clients.ApiClient;
import dev.sprikers.moustacheshop.constants.ApiEndpoints;
import dev.sprikers.moustacheshop.dto.LoginRequest;
import dev.sprikers.moustacheshop.models.UserModel;
import dev.sprikers.moustacheshop.utils.JwtPreferencesManager;
import dev.sprikers.moustacheshop.utils.UserSession;

public class AuthService {

    private final ApiClient apiClient = new ApiClient();

    public void login(String email, String password) throws Exception {
        LoginRequest loginRequest = new LoginRequest(email, password);
        HttpResponse<String> response = apiClient.post(ApiEndpoints.AUTH_LOGIN, loginRequest);
        handleResponse(response);
    }

    public void fetchUserInfo() throws Exception {
        HttpResponse<String> response = apiClient.get(ApiEndpoints.CHECK_AUTH_STATUS);
        handleResponse(response);
    }

    private void handleResponse(HttpResponse<String> response) throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        UserModel user = objectMapper.readValue(response.body(), UserModel.class);
        UserSession.getInstance().setUserModel(user);
        JwtPreferencesManager.setJwt(user.getToken());
    }

}