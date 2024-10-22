package dev.sprikers.moustacheshop.services;

import java.net.http.HttpResponse;
import java.util.concurrent.CompletableFuture;

import dev.sprikers.moustacheshop.clients.ApiClient;
import dev.sprikers.moustacheshop.constants.ApiEndpoints;
import dev.sprikers.moustacheshop.dto.ChangePasswordRequest;
import dev.sprikers.moustacheshop.dto.UpdateProfileRequest;
import dev.sprikers.moustacheshop.models.UserModel;
import dev.sprikers.moustacheshop.utils.JsonParserUtils;

public class UserService {

    private final ApiClient apiClient = new ApiClient();

    public CompletableFuture<UserModel> update(UpdateProfileRequest updateProfileRequest, String id) {
        return apiClient.patchAsync(ApiEndpoints.USER + "/" + id, updateProfileRequest)
            .thenApply(response -> JsonParserUtils.parseResponse(response.body(), UserModel.class));
    }

    public CompletableFuture<String> changePassword(ChangePasswordRequest changePasswordRequest) {
        return apiClient.postAsync(ApiEndpoints.USER + "/change-password", changePasswordRequest)
            .thenApply(HttpResponse::body);
    }

}
