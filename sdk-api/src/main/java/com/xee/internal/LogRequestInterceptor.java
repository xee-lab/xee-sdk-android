/*
 * Copyright 2016 Eliocity
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.xee.internal;

import android.util.Log;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.ResponseBody;

/**
 * Makes possible to Log the requests in debug mode
 */
public class LogRequestInterceptor implements Interceptor {
    private static final String TAG = "Xee";

    @Override
    public okhttp3.Response intercept(Chain chain) throws IOException {
        Request request = chain.request();

        okhttp3.Response response = chain.proceed(request);
        String rawJson = response.body().string();

        Log.v(TAG, "==============================================================================================================================");
        Log.v(TAG, "URL: " + request.url().toString());
        Log.v(TAG, "RESPONSE: " + rawJson);
        Log.v(TAG, "==============================================================================================================================");

        // Recreate the item before returning it because body can be read only once
        return response.newBuilder().body(ResponseBody.create(response.body().contentType(), rawJson)).build();
    }
}