package com.moviles.proyectofinal.utils.network;

import androidx.annotation.NonNull;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class GoogleLanguageInterceptor implements Interceptor {
    @NonNull
    @Override
    public Response intercept(Interceptor.Chain chain) throws IOException {
        Request originalRequest = chain.request();
        Request requestWithHeaders = originalRequest.newBuilder()
                .header("Accept-Language", "es")
                .build();
        return chain.proceed(requestWithHeaders);
    }
}


