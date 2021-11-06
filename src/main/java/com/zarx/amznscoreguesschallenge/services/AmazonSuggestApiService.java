package com.zarx.amznscoreguesschallenge.services;

import com.zarx.amznscoreguesschallenge.dto.AmazonSuggestionsResponseDto;
import com.zarx.amznscoreguesschallenge.dto.SuggestionDto;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.CompletableFuture;

@Service
public class AmazonSuggestApiService {

    @Autowired
    private OkHttpClient httpClient;

    private CompletableFuture<List<SuggestionDto>> callAmazon(String prefix) {
        CompletableFuture future = new CompletableFuture();

        AmazonSuggestionsResponseDto g;
        httpClient.newCall(governmentArticlesrequest).enqueue(new Callback() {

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {

            }

            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {

            }
        });

        return future;
    }

    private static Request createSuggestApiRequest() {
        return new Request.Builder()
                .get()
                .addHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                .url(new HttpUrl.newBuilder().Builder().parse$okhttp())
                .build();
    }

}
