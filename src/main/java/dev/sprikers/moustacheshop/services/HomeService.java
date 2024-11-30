package dev.sprikers.moustacheshop.services;

import java.util.concurrent.CompletableFuture;

import dev.sprikers.moustacheshop.clients.ApiClient;
import dev.sprikers.moustacheshop.constants.ApiEndpoints;
import dev.sprikers.moustacheshop.models.SummaryModel;
import dev.sprikers.moustacheshop.utils.JsonParserUtils;

public class HomeService {

    private static final ApiClient apiClient = new ApiClient();

    public CompletableFuture<SummaryModel> fetchSummaries() {
        return apiClient.getAsync(ApiEndpoints.SUMMARIES)
            .thenApply(response -> JsonParserUtils.parseResponse(response.body(), SummaryModel.class));
    }

}
