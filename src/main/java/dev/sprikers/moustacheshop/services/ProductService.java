package dev.sprikers.moustacheshop.services;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import com.fasterxml.jackson.core.type.TypeReference;

import dev.sprikers.moustacheshop.clients.ApiClient;
import dev.sprikers.moustacheshop.constants.ApiEndpoints;
import dev.sprikers.moustacheshop.dto.ProductRequest;
import dev.sprikers.moustacheshop.models.ProductModel;
import dev.sprikers.moustacheshop.utils.JsonParserUtils;

public class ProductService {

    private final ApiClient apiClient = new ApiClient();

    public CompletableFuture<List<ProductModel>> allProducts() {
        return apiClient.getAsync(ApiEndpoints.PRODUCT)
            .thenApply(response -> JsonParserUtils.parseResponse(response.body(), new TypeReference<List<ProductModel>>() {}));
    }

    public CompletableFuture<ProductModel> register(ProductRequest productRequest) {
        return apiClient.postAsync(ApiEndpoints.PRODUCT, productRequest)
            .thenApply(response -> JsonParserUtils.parseResponse(response.body(), ProductModel.class));
    }

    public CompletableFuture<ProductModel> update(ProductRequest productRequest, String id) {
        return apiClient.patchAsync(ApiEndpoints.PRODUCT + "/" + id, productRequest)
            .thenApply(response -> JsonParserUtils.parseResponse(response.body(), ProductModel.class));
    }

    public CompletableFuture<Void> delete(String id) {
        return apiClient.deleteAsync(ApiEndpoints.PRODUCT + "/" + id)
            .thenAccept(response -> {});
    }

}
