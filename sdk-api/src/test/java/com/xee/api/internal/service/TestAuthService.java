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

package com.xee.api.internal.service;

import android.os.Build;

import com.xee.BuildConfig;
import com.xee.api.Xee;
import com.xee.core.ErrorUtils;
import com.xee.core.entity.Token;
import com.xee.internal.APIInterceptor;
import com.xee.internal.service.AuthService;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ErrorCollector;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.io.IOException;
import java.util.Locale;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;

import static org.hamcrest.Matchers.is;

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class, sdk = Build.VERSION_CODES.LOLLIPOP)
public class TestAuthService {

    private AuthService authService;

    @Rule
    public ErrorCollector collector = new ErrorCollector();

    @Before
    public void setUp() {
        OkHttpClient.Builder authClientBuilder = new OkHttpClient.Builder()
                .addInterceptor(new APIInterceptor());

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(String.format(Locale.FRANCE, Xee.ROUTE_BASE, "fake"))
                .client(authClientBuilder.build())
                .addConverterFactory(Xee.CONVERTER_FACTORY)
                .build();
        authService = retrofit.create(AuthService.class);
    }

    @Test
    public void obtainToken() {
        Call<Token> tokenCall = authService.obtainToken(AuthService.GRANT_TYPES.AUTHORIZATION_CODE, "fake-code");
        try {
            Response<Token> execute = tokenCall.execute();
            if (execute.isSuccessful()) {
                Token token = execute.body();
                collector.checkThat("access token", token.getAccessToken(), is("22fe0c13e995da4a44a63a7ff549badb5d337a42bf80f17424482e35d4cca91a"));
                collector.checkThat("refresh token", token.getRefreshToken(), is("8eb667707535655f2d9e14fc6491a59f6e06f2e73170761259907d8de186b6a1"));
                collector.checkThat("expires in", token.getExpiresIn(), is(3600L));
                collector.checkThat("expires at", token.getExpiresAt(), is(1382962374L));
            } else {
                collector.addError(ErrorUtils.parseError(execute.errorBody().string()));
            }
        } catch (IOException e) {
            collector.addError(e);
        }
    }

    @Test
    public void refreshToken() {
        Call<Token> tokenCall = authService.refreshToken(AuthService.GRANT_TYPES.REFRESH_TOKEN, "fake-refresh");
        try {
            Response<Token> execute = tokenCall.execute();
            if (execute.isSuccessful()) {
                Token token = execute.body();
                collector.checkThat("access token", token.getAccessToken(), is("22fe0c13e995da4a44a63a7ff549badb5d337a42bf80f17424482e35d4cca91a"));
                collector.checkThat("refresh token", token.getRefreshToken(), is("8eb667707535655f2d9e14fc6491a59f6e06f2e73170761259907d8de186b6a1"));
                collector.checkThat("expires in", token.getExpiresIn(), is(3600L));
                collector.checkThat("expires at", token.getExpiresAt(), is(1382962374L));
            } else {
                collector.addError(ErrorUtils.parseError(execute.errorBody().string()));
            }
        } catch (IOException e) {
            collector.addError(e);
        }
    }
}
