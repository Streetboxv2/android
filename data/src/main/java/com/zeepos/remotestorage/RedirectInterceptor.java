package com.zeepos.remotestorage;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class RedirectInterceptor implements Interceptor {
    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        Response response = chain.proceed(chain.request());
        if (response.code() == 307) {
            request = request.newBuilder()
                    .url(response.header("Location"))
                    .build();
            response = chain.proceed(request);

        }
        return response;
    }
}
