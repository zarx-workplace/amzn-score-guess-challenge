package com.zarx.amznscoreguesschallenge.configs;

import okhttp3.CookieJar;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

@Configuration
public class CommsConfiguration {

    public static final int CALL_TIMEOUT_GLOBAL_SECONDS = 5;

    @Bean
    public OkHttpClient okHttpClient() {
        Logger httpLog = LoggerFactory.getLogger("ApiComms");

        return new OkHttpClient.Builder()
                .cookieJar(CookieJar.NO_COOKIES)
                .followRedirects(false)
                .callTimeout(CALL_TIMEOUT_GLOBAL_SECONDS, TimeUnit.SECONDS)
                .connectTimeout(500, TimeUnit.MILLISECONDS)
                .readTimeout(2, TimeUnit.SECONDS)
                .addInterceptor(new HttpLoggingInterceptor(httpLog::info).setLevel(HttpLoggingInterceptor.Level.BODY))
                .build();
    }
}
