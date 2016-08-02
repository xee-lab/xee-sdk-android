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

import com.xee.core.ErrorUtils;
import com.xee.core.entity.Error;
import com.xee.core.entity.Token;
import com.xee.core.internal.storage.TokenStorage;
import com.xee.internal.service.AuthService;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

/**
 * This interceptor refresh a token and relaunch the same request if the error is a token outdated error
 */
public class RefreshTokenInterceptor implements Interceptor {

    private static final int FORBIDDEN_CODE = 403;
    private static final String TOKEN_OUTDATED_MESSAGE = "Token has expired";

    private final AuthService authService;
    private final TokenStorage storage;

    public RefreshTokenInterceptor(AuthService service, TokenStorage storage) {
        this.authService = service;
        this.storage = storage;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request originalRequest = chain.request();
        Response originalResponse = chain.proceed(originalRequest);

        final String rawJson = originalResponse.body().string();

        // If the original request is not successful because of 403
        if (!originalResponse.isSuccessful() && originalResponse.code() == FORBIDDEN_CODE) {
            Error error = ErrorUtils.parseError(rawJson);
            // If the error was because the token was outdated
            if (TOKEN_OUTDATED_MESSAGE.equals(error.getMessage())) {
                // Refresh the token
                Token currentToken = storage.get();
                assert currentToken != null;
                retrofit2.Response<Token> tokenResponse = authService
                        .refreshToken(AuthService.GRANT_TYPES.REFRESH_TOKEN, currentToken.getRefreshToken())
                        .execute();

                if (tokenResponse.isSuccessful()) {
                    // If it worked, then store the new token and retry the previous request
                    storage.store(tokenResponse.body());
                    Request.Builder requestBuilder = originalRequest.newBuilder()
                                                                    .header("Authorization", "Bearer " + tokenResponse.body().getAccessToken());
                    originalRequest = requestBuilder.build();
                    return chain.proceed(originalRequest);
                } else {
                    return originalResponse.newBuilder()
                                           .body(ResponseBody.create(originalResponse.body().contentType(), rawJson))
                                           .build();
                }
            } else {
                return originalResponse.newBuilder()
                                       .body(ResponseBody.create(originalResponse.body().contentType(), rawJson))
                                       .build();
            }
        } else {
            return originalResponse.newBuilder()
                                   .body(ResponseBody.create(originalResponse.body().contentType(), rawJson))
                                   .build();
        }
    }
}