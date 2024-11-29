package dev.sprikers.moustacheshop.services;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import com.fasterxml.jackson.core.type.TypeReference;

import dev.sprikers.moustacheshop.clients.ApiClient;
import dev.sprikers.moustacheshop.constants.ApiEndpoints;
import dev.sprikers.moustacheshop.models.CategoryModel;
import dev.sprikers.moustacheshop.utils.JsonParserUtils;

public class CategoryService {

    private static final ApiClient apiClient = new ApiClient();

    public CompletableFuture<List<CategoryModel>> getAll() {
        return apiClient.getAsync(ApiEndpoints.CATEGORIES)
            .thenApply(response -> {
                List<CategoryModel> categories = JsonParserUtils.parseResponse(response.body(), new TypeReference<>() {});

                CategoryModel defaultCategory = new CategoryModel();
                defaultCategory.setId(null);
                defaultCategory.setName("Seleccione una categoría");
                categories.addFirst(defaultCategory);

                return categories;
            });
    }

}
