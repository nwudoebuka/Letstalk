package com.newage.letstalk.api;

import android.util.Log;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class RetrofitService {
    private static Retrofit retrofit;
    private static OkHttpClient okHttpClient;
    private static ApiInterface apiInterface;

    private static String BASEURL = "https://globeexservices.com/letstalk/";

    public static ApiInterface initializer() {
        if(apiInterface==null){
            apiInterface = provideRetrofit().create(ApiInterface.class);
        }

        return apiInterface;
    }

    private static Retrofit provideRetrofit() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASEURL)
                    .client(provideOkHttpClient())
                    .addConverterFactory(ScalarsConverterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create())
                    //.addCallAdapterFactory(RxJava2CallAdapterFactory.create()) //to work with RxJava @JB
                    .build();
        }

        return retrofit;
    }

    private static OkHttpClient provideOkHttpClient() {
        if (okHttpClient == null) {
            okHttpClient = new OkHttpClient.Builder()
                    .connectTimeout(60, TimeUnit.SECONDS)
                    .readTimeout(60, TimeUnit.SECONDS)
                    .writeTimeout(60, TimeUnit.SECONDS)
                    .addInterceptor(new LoggingInterceptor())
                    .build();
        }

        return okHttpClient;
    }


    public static class LoggingInterceptor implements Interceptor {
        private String TAG = LoggingInterceptor.class.getName();

        @Override
        public Response intercept(Chain chain) throws IOException {

            Request request = chain.request();
            long t1 = System.nanoTime();
            Log.d(TAG, String.format("Retrofit Sending request %s on %s%n%s", request.url(), chain.connection(), request.headers()));

            Response response = chain.proceed(request);
            long t2 = System.nanoTime();
            Log.d(TAG, String.format("Retrofit Received response for %s in %.1fms%n%s", response.request().url(), (t2 - t1) / 1e6d, response.headers()));

            return response;
        }
    }
}