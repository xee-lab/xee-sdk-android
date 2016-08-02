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
import com.xee.api.entity.Car;
import com.xee.api.entity.User;
import com.xee.core.ErrorUtils;
import com.xee.internal.APIInterceptor;
import com.xee.internal.service.UsersService;

import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ErrorCollector;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import okhttp3.OkHttpClient;
import retrofit2.Response;
import retrofit2.Retrofit;

import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.hasSize;

/**
 * To work on unit tests, switch the Test Artifact in the Build Variants view.
 */
@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, sdk = Build.VERSION_CODES.LOLLIPOP)
public class TestUsersService {

    private UsersService usersService;

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
        usersService = retrofit.create(UsersService.class);
    }

    @Test
    public void getMe() throws Exception {
        try {
            Response<User> xeeRequest = usersService.getUser().execute();
            if (xeeRequest.isSuccessful()) {

                collector.checkThat("no error", xeeRequest.errorBody(), nullValue());
                collector.checkThat("item", xeeRequest.body(), notNullValue());

                collector.checkThat("id", xeeRequest.body().getId(), is("42"));
                collector.checkThat("last name", xeeRequest.body().getLastName(), is("Doe"));
                collector.checkThat("first name", xeeRequest.body().getFirstName(), is("John"));
                collector.checkThat("gender", xeeRequest.body().getGender(), is(User.Gender.MALE));
                collector.checkThat("role", xeeRequest.body().getRole(), is("dev"));
                collector.checkThat("location enabled", xeeRequest.body().isLocationEnabled(), is(true));
            } else {
                collector.addError(ErrorUtils.parseError(xeeRequest.errorBody().string()));
            }
        } catch (IOException e) {
            collector.addError(e);
        }
    }

    @Test
    public void getCars() {
        try {
            Response<List<Car>> xeeRequest = usersService.getCars().execute();
            if (xeeRequest.isSuccessful()) {
                collector.checkThat("no error", xeeRequest.errorBody(), nullValue());
                collector.checkThat("item", xeeRequest.body(), notNullValue());

                collector.checkThat("size", xeeRequest.body(), hasSize(1));
                collector.checkThat("car", xeeRequest.body(), hasItem(
                        Matchers.<Car>allOf(
                                hasProperty("id", is("1337")),
                                hasProperty("name", is("Mark-42")),
                                hasProperty("make", is("Mark")),
                                hasProperty("model", is("42")),
                                hasProperty("year", is(2014)),
                                hasProperty("numberPlate", is("M-42-TS")),
                                hasProperty("deviceId", is("E133742015")),
                                hasProperty("cardbId", is(210))
                        )
                ));
            } else {
                collector.addError(ErrorUtils.parseError(xeeRequest.errorBody().string()));
            }
        } catch (IOException e) {
            collector.addError(e);
        }
    }
}