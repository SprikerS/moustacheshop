package dev.sprikers.moustacheshop.services;

import java.io.IOException;
import java.net.http.HttpResponse;
import java.util.List;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import dev.sprikers.moustacheshop.clients.ApiClient;
import dev.sprikers.moustacheshop.constants.ApiEndpoints;
import dev.sprikers.moustacheshop.dto.ProductRequest;
import dev.sprikers.moustacheshop.models.ProductModel;

public class ProductService {

    private final ApiClient apiClient = new ApiClient();

    public List<ProductModel> allProducts() throws Exception {
        try {
            HttpResponse<String> response = apiClient.get(ApiEndpoints.PRODUCT);
            ObjectMapper objectMapper = new ObjectMapper();

            if (response.statusCode() != 200) {
                JsonNode errorResponse = objectMapper.readTree(response.body());
                throw new Exception(errorResponse.get("message").asText());
            }

            return objectMapper.readValue(response.body(), new TypeReference<List<ProductModel>>() {});
        } catch (IOException e) {
            throw new Exception(e.getMessage());
        }
    }

    public void register(ProductRequest productRequest) throws Exception {
        try {
            HttpResponse<String> response = apiClient.post(ApiEndpoints.PRODUCT, productRequest);
            ObjectMapper objectMapper = new ObjectMapper();

            if (response.statusCode() != 201) {
                JsonNode errorResponse = objectMapper.readTree(response.body());
                throw new Exception(errorResponse.get("message").asText());
            }
        } catch (IOException e) {
            throw new Exception(e.getMessage());
        }
    }

    public void update(ProductRequest productRequest, String id) throws Exception {
        try {
            HttpResponse<String> response = apiClient.patch(ApiEndpoints.PRODUCT + "/" + id, productRequest);
            ObjectMapper objectMapper = new ObjectMapper();

            if (response.statusCode() != 200) {
                JsonNode errorResponse = objectMapper.readTree(response.body());
                throw new Exception(errorResponse.get("message").asText());
            }
        } catch (IOException e) {
            throw new Exception(e.getMessage());
        }
    }

    public void delete(String id) throws Exception {
        try {
            HttpResponse<String> response = apiClient.delete(ApiEndpoints.PRODUCT + "/" + id);
            ObjectMapper objectMapper = new ObjectMapper();

            if (response.statusCode() != 200) {
                JsonNode errorResponse = objectMapper.readTree(response.body());
                throw new Exception(errorResponse.get("message").asText());
            }
        } catch (IOException e) {
            throw new Exception(e.getMessage());
        }
    }

}
