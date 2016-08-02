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

package com.xee.core;

import android.content.Context;
import android.support.annotation.NonNull;

import com.xee.core.entity.OAuth2Client;
import com.xee.core.internal.storage.TokenStorage;

/**
 * All environments from Xee available
 */
public class XeeEnv {
    /**
     * Classic Xee Environment (real users)
     */
    public static final String CLOUD = "cloud";
    /**
     * Sandbox Xee Environment (fake data)
     */
    public static final String SANDBOX = "sandbox";


    public final Context context;
    public final long connectTimeout;
    public final long readTimeout;
    public final String environment;
    public final OAuth2Client client;
    public final TokenStorage tokenStorage;

    /***
     * Create a Xee environment
     *
     * @param context the context of the app
     * @param client  the {@link OAuth2Client} of the app
     */
    public XeeEnv(@NonNull Context context, @NonNull OAuth2Client client) {
        this(context, client, 60 * 1000, 60 * 1000, CLOUD);
    }

    /**
     * Create a Xee environment
     *
     * @param context        the context of the app
     * @param client         the {@link OAuth2Client} of the app
     * @param connectTimeout the connection timeout in ms
     * @param readTimeout    the read timeout in ms
     * @param environment    the Xee environment
     */
    public XeeEnv(@NonNull Context context, @NonNull OAuth2Client client, long connectTimeout, long readTimeout, String environment) {
        this.context = context;
        this.client = client;
        this.connectTimeout = connectTimeout;
        this.readTimeout = readTimeout;
        this.environment = environment;
        this.tokenStorage = TokenStorage.get(context);
    }
}