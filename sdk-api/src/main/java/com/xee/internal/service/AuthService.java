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

package com.xee.internal.service;

import com.xee.core.entity.Token;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface AuthService {

    final class Routes {
        public static final String AUTHENTICATION = "auth/auth";
        public static final String ACCESS_TOKEN = "auth/access_token";
    }

    final class Parameters {
        public static final String GRANT_TYPE = "grant_type";
        public static final String CODE = "code";
        public static final String REFRESH_TOKEN = "refresh_token";
        public static final String CLIENT_ID = "client_id";
        public static final String STATE = "state";
        public static final String REDIRECT_URL = "redirect_uri";
        public static final String SCOPE = "scope";
    }

    final class GRANT_TYPES {
        public static final String AUTHORIZATION_CODE = "authorization_code";
        public static final String REFRESH_TOKEN = "refresh_token";
    }

    @FormUrlEncoded
    @POST(Routes.ACCESS_TOKEN)
    Call<Token> obtainToken(@Field(Parameters.GRANT_TYPE) String grantType, @Field(Parameters.CODE) String authorizationCode);

    @FormUrlEncoded
    @POST(Routes.ACCESS_TOKEN)
    Call<Token> refreshToken(@Field(Parameters.GRANT_TYPE) String grantType, @Field(Parameters.REFRESH_TOKEN) String refreshToken);

}