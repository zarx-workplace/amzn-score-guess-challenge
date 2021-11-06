package com.zarx.amznscoreguesschallenge.configs;

import okhttp3.CookieJar;
import okhttp3.OkHttpClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

@Configuration
public class CommsConfiguration {

    @Bean
    public OkHttpClient okHttpClient() {
        OkHttpClient client = new OkHttpClient.Builder()
                .cookieJar(CookieJar.NO_COOKIES)
                .followRedirects(false)
                .callTimeout(7, TimeUnit.SECONDS)
                .connectTimeout(500, TimeUnit.MILLISECONDS)
                .readTimeout(2, TimeUnit.SECONDS)

                .build();
        return client;
    }
}
