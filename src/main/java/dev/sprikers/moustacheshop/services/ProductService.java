package dev.sprikers.moustacheshop.services;

import java.net.http.HttpResponse;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import dev.sprikers.moustacheshop.clients.ApiClient;
import dev.sprikers.moustacheshop.constants.ApiEndpoints;
import dev.sprikers.moustacheshop.dto.ProductRequest;
import dev.sprikers.moustacheshop.models.ProductModel;

public class ProductService {

    private final ApiClient apiClient = new ApiClient();
    private static final ObjectMapper objectMapper = new ObjectMapper();

    public CompletableFuture<List<ProductModel>> allProductsAsync() {
        return apiClient.getAsync(ApiEndpoints.PRODUCT)
            .thenApply(response -> {
                try {
                    return objectMapper.readValue(response.body(), new TypeReference<List<ProductModel>>() {});
                } catch (Exception e) {
                    throw new CompletionException(e);
                }
            });
    }

    public void register(ProductRequest productRequest) throws Exception {
        HttpResponse<String> response = apiClient.post(ApiEndpoints.PRODUCT, productRequest);
    }

    public void update(ProductRequest productRequest, String id) throws Exception {
        HttpResponse<String> response = apiClient.patch(ApiEndpoints.PRODUCT + "/" + id, productRequest);
    }

    public void delete(String id) throws Exception {
        HttpResponse<String> response = apiClient.delete(ApiEndpoints.PRODUCT + "/" + id);
    }

}
