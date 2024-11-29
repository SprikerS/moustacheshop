package dev.sprikers.moustacheshop.services;

import java.net.http.HttpResponse;
import java.util.concurrent.CompletableFuture;

import dev.sprikers.moustacheshop.clients.ApiClient;
import dev.sprikers.moustacheshop.constants.ApiEndpoints;
import dev.sprikers.moustacheshop.dto.OrderRequest;
import dev.sprikers.moustacheshop.models.OrderModel;
import dev.sprikers.moustacheshop.utils.JsonParserUtils;

public class OrderService {

    private static final ApiClient apiClient = new ApiClient();

    public CompletableFuture<OrderModel> create(OrderRequest orderRequest) {
        return apiClient.postAsync(ApiEndpoints.ORDER, orderRequest)
            .thenApply(response -> JsonParserUtils.parseResponse(response.body(), OrderModel.class));
    }

    public CompletableFuture<byte[]> fetchReport(String orderId) {
        return apiClient.getAsyncBytes(ApiEndpoints.REPORTS + "/" + orderId)
            .thenApply(HttpResponse::body);
    }

    public String generateReportLink(String orderId) {
        return ApiEndpoints.REPORTS + "/" + orderId;
    }

}
