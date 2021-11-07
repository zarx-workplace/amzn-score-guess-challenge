package com.zarx.amznscoreguesschallenge.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zarx.amznscoreguesschallenge.dto.AmazonSuggestionsResponseDto;
import com.zarx.amznscoreguesschallenge.dto.SuggestionDto;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;

@Service
@Slf4j
public class AmazonSuggestApiService {

    private static final String URL_PARAM_CATEGORY = "alias";
    private static final String URL_PARAM_CATEGORY_ALL_VALUE = "aps";
    private static final String URL_PARAM_MARKET_ID = "plain-mid";
    private static final String URL_PARAM_MARKET_ID_VALUE = "1";
    private static final String URL_PARAM_PREFIX = "prefix";

    @Autowired
    private OkHttpClient httpClient;

    @Autowired
    private ObjectMapper objectMapper;

    private HttpUrl amazonApiEndpoint;

    @Autowired
    public void setAmazonApiEndpoint(@Value("${amznscoreguess.amazon.endpoint}") String baseUrl) {
        this.amazonApiEndpoint = HttpUrl.get(baseUrl);
    }

    public Future<List<SuggestionDto>> getSuggestions(String prefix) {
        CompletableFuture future = new CompletableFuture();

        httpClient.newCall(createSuggestApiRequest(prefix)).enqueue(new Callback() {

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful()) {
                    future.completeExceptionally(new IOException("Unexpected HTTP status on Amazon API call: " + response.toString()));
                    return;
                }
                try (InputStream is = response.body().byteStream()) {
                    AmazonSuggestionsResponseDto dto = objectMapper.readValue(is, AmazonSuggestionsResponseDto.class);
                    future.complete(dto.getSuggestions());
                } catch (IOException e) {
                    future.completeExceptionally(e);
                }
            }

            @Override
            public void onFailure(Call call, IOException e) {
                future.completeExceptionally(e);
                call.cancel();
            }
        });

        return future;
    }

    private Request createSuggestApiRequest(String prefix) {
        return new Request.Builder()
                .get()
                .addHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                .url(amazonApiEndpoint
                        .newBuilder()
                        .addQueryParameter(URL_PARAM_CATEGORY, URL_PARAM_CATEGORY_ALL_VALUE)
                        .addQueryParameter(URL_PARAM_MARKET_ID, URL_PARAM_MARKET_ID_VALUE)
                        .addQueryParameter(URL_PARAM_PREFIX, prefix)
                        .build())
                .build();
    }

}
