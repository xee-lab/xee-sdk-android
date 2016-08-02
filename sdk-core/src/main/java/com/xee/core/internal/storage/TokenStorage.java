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

package com.xee.core.internal.storage;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.gson.GsonBuilder;
import com.xee.core.entity.Token;
import com.xee.core.internal.UniqueStorage;

/**
 * Xee Token Storage
 * This class stores and provides a Token
 */
public class TokenStorage implements UniqueStorage<Token> {

    private static final String NAME = "com.xee.sdk";
    private static final String TOKEN_ID = "xee.token";
    private static TokenStorage sInstance;
    private SharedPreferences preferences;

    private TokenStorage(Context context) {
        preferences = context.getSharedPreferences(NAME, Context.MODE_PRIVATE);
    }

    public static TokenStorage get(@NonNull Context context) {
        if (sInstance == null) {
            sInstance = new TokenStorage(context);
        }
        return sInstance;
    }

    @Override
    public void store(@NonNull Token item) {
        preferences.edit().putString(TOKEN_ID, new GsonBuilder().create().toJson(item)).apply();
    }

    @Override
    @Nullable
    public Token get() {
        String tokenFromPrefs = preferences.getString(TOKEN_ID, null);
        if (tokenFromPrefs != null) {
            return new GsonBuilder().create().fromJson(tokenFromPrefs, Token.class);
        } else {
            return null;
        }
    }

    public void dump() {
        preferences.edit().remove(TOKEN_ID).apply();
    }
}