package dev.sprikers.moustacheshop.services;

import java.net.http.HttpResponse;
import java.util.List;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import dev.sprikers.moustacheshop.clients.ApiClient;
import dev.sprikers.moustacheshop.constants.ApiEndpoints;
import dev.sprikers.moustacheshop.dto.ProductRequest;
import dev.sprikers.moustacheshop.models.ProductModel;

public class ProductService {

    private final ApiClient apiClient = new ApiClient();

    public List<ProductModel> allProducts() throws Exception {
        HttpResponse<String> response = apiClient.get(ApiEndpoints.PRODUCT);
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readValue(response.body(), new TypeReference<List<ProductModel>>() {});
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
