package dev.sprikers.moustacheshop.services;

import java.net.http.HttpResponse;
import java.util.concurrent.CompletableFuture;

import dev.sprikers.moustacheshop.clients.ApiClient;
import dev.sprikers.moustacheshop.constants.ApiEndpoints;
import dev.sprikers.moustacheshop.dto.OrderRequest;

public class OrderService {

    private static final ApiClient apiClient = new ApiClient();

    public CompletableFuture<String> create(OrderRequest orderRequest) {
        return apiClient.postAsync(ApiEndpoints.ORDER, orderRequest).thenApply(HttpResponse::body);
    }

}
