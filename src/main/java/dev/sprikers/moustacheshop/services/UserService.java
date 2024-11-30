package dev.sprikers.moustacheshop.services;

import java.net.http.HttpResponse;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import com.fasterxml.jackson.core.type.TypeReference;

import dev.sprikers.moustacheshop.clients.ApiClient;
import dev.sprikers.moustacheshop.constants.ApiEndpoints;
import dev.sprikers.moustacheshop.dto.ChangePasswordRequest;
import dev.sprikers.moustacheshop.dto.UpdateProfileRequest;
import dev.sprikers.moustacheshop.dto.UserRequest;
import dev.sprikers.moustacheshop.dto.UserUpdateRequest;
import dev.sprikers.moustacheshop.models.ReniecModel;
import dev.sprikers.moustacheshop.models.UserModel;
import dev.sprikers.moustacheshop.utils.JsonParserUtils;

public class UserService {

    private final ApiClient apiClient = new ApiClient();

    public CompletableFuture<List<UserModel>> allUsers() {
        return apiClient.getAsync(ApiEndpoints.USER)
            .thenApply(response -> JsonParserUtils.parseResponse(response.body(), new TypeReference<>() {}));
    }

    public CompletableFuture<UserModel> updateProfile(UserUpdateRequest userRequest, String id) {
        return apiClient.patchAsync(ApiEndpoints.USER + "/" + id, userRequest)
            .thenApply(response -> JsonParserUtils.parseResponse(response.body(), UserModel.class));
    }

    public CompletableFuture<UserModel> update(UpdateProfileRequest updateProfileRequest, String id) {
        return apiClient.patchAsync(ApiEndpoints.USER + "/" + id, updateProfileRequest)
            .thenApply(response -> JsonParserUtils.parseResponse(response.body(), UserModel.class));
    }

    public CompletableFuture<Void> delete(String id) {
        return apiClient.deleteAsync(ApiEndpoints.USER + "/" + id)
            .thenAccept(response -> {});
    }

    public CompletableFuture<String> changePassword(ChangePasswordRequest changePasswordRequest) {
        return apiClient.postAsync(ApiEndpoints.USER + "/change-password", changePasswordRequest)
            .thenApply(HttpResponse::body);
    }

    public CompletableFuture<ReniecModel> reniec(String dni) {
        return apiClient.getAsync(ApiEndpoints.USER + "/reniec?dni=" + dni)
            .thenApply(response -> JsonParserUtils.parseResponse(response.body(), ReniecModel.class));
    }

    public CompletableFuture<UserModel> register(UserRequest userRequest) {
        return apiClient.postAsync(ApiEndpoints.USER + "/register", userRequest)
            .thenApply(response -> JsonParserUtils.parseResponse(response.body(), UserModel.class));
    }

}
