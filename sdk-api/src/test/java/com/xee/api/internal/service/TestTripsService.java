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
import com.xee.api.entity.Location;
import com.xee.api.entity.Signal;
import com.xee.api.entity.Stat;
import com.xee.api.entity.Trip;
import com.xee.core.ErrorUtils;
import com.xee.internal.APIInterceptor;
import com.xee.internal.service.TripsService;

import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ErrorCollector;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

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
@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class, sdk = Build.VERSION_CODES.LOLLIPOP)
public class TestTripsService {

    private TripsService tripsService;

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
        tripsService = retrofit.create(TripsService.class);
    }

    @Test
    public void getTrip() {
        try {
            Response<Trip> xeeRequest = tripsService.getTrip("56b43a4f051f29071f14218d").execute();
            if (xeeRequest.isSuccessful()) {
                collector.checkThat("no error", xeeRequest.errorBody(), nullValue());
                collector.checkThat("item", xeeRequest.body(), notNullValue());

                collector.checkThat("id", xeeRequest.body().getId(), is("56b43a4f051f29071f14218d"));
                collector.checkThat("begin location", xeeRequest.body().getBeginLocation(), is(Matchers.<Location>allOf(
                        hasProperty("latitude", is(50.6817)),
                        hasProperty("longitude", is(3.08202)),
                        hasProperty("altitude", is(2d)),
                        hasProperty("satellites", is(1)),
                        hasProperty("heading", is(0.0))
                )));
                collector.checkThat("end location", xeeRequest.body().getEndLocation(), is(Matchers.<Location>allOf(
                        hasProperty("latitude", is(50.6817)),
                        hasProperty("longitude", is(3.08202)),
                        hasProperty("altitude", is(2d)),
                        hasProperty("satellites", is(1)),
                        hasProperty("heading", is(0.0))
                )));
            } else {
                collector.addError(ErrorUtils.parseError(xeeRequest.errorBody().string()));
            }
        } catch (IOException e) {
            collector.addError(e);
        }
    }

    @Test
    public void getLocations() {
        try {
            Response<List<Location>> xeeRequest = tripsService.getLocations("56b43a4f051f29071f14218d").execute();
            if (xeeRequest.isSuccessful()) {

                collector.checkThat("no error", xeeRequest.errorBody(), nullValue());
                collector.checkThat("item", xeeRequest.body(), notNullValue());

                collector.checkThat("size", xeeRequest.body(), hasSize(1));

                collector.checkThat("location", xeeRequest.body(), hasItem(
                        Matchers.<Location>allOf(
                                hasProperty("latitude", is(50.67815)),
                                hasProperty("longitude", is(3.208155)),
                                hasProperty("altitude", is(31.8)),
                                hasProperty("satellites", is(4)),
                                hasProperty("heading", is(167.0))
                        )
                ));
            } else {
                collector.addError(ErrorUtils.parseError(xeeRequest.errorBody().string()));
            }
        } catch (IOException e) {
            collector.addError(e);
        }
    }

    @Test
    public void getSignals() {
        try {
            Map<String, String> options = new HashMap<>(1);
            Response<List<Signal>> xeeRequest = tripsService.getSignals("56b43a4f051f29071f14218d", options).execute();
            if (xeeRequest.isSuccessful()) {

                collector.checkThat("no error", xeeRequest.errorBody(), nullValue());
                collector.checkThat("item", xeeRequest.body(), notNullValue());

                collector.checkThat("size", xeeRequest.body().size(), is(2));

                collector.checkThat("contains lock", xeeRequest.body(), hasItem(
                        Matchers.<Signal>allOf(hasProperty("name", is(Signal.Name.LOCK.name)), hasProperty("value", is(0d)))
                ));
                collector.checkThat("contains odometer", xeeRequest.body(), hasItem(
                        Matchers.<Signal>allOf(hasProperty("name", is(Signal.Name.ODOMETER.name)), hasProperty("value", is(34512.1)))
                ));
            } else {
                collector.addError(ErrorUtils.parseError(xeeRequest.errorBody().string()));
            }
        } catch (IOException e) {
            collector.addError(e);
        }
    }

    @Test
    public void getStats() {
        try {
            Response<List<Stat>> xeeRequest = tripsService.getStats("56b43a4f051f29071f14218d").execute();
            if (xeeRequest.isSuccessful()) {

                collector.checkThat("no error", xeeRequest.errorBody(), nullValue());
                collector.checkThat("item", xeeRequest.body(), notNullValue());

                collector.checkThat("size", xeeRequest.body().size(), is(2));

                collector.checkThat("type", xeeRequest.body().get(0).getType(), is("MILEAGE"));
                collector.checkThat("value", (Double) xeeRequest.body().get(0).getValue(), is(4.2d));

                collector.checkThat("type", xeeRequest.body().get(1).getType(), is("USED_TIME"));
                collector.checkThat("value", (Double) xeeRequest.body().get(1).getValue(), is(4200d));
            } else {
                collector.addError(ErrorUtils.parseError(xeeRequest.errorBody().string()));
            }
        } catch (IOException e) {
            collector.addError(e);
        }
    }

    @Test
    public void getMileage() {
        try {
            Response<Stat<Double>> xeeRequest = tripsService.getMileage("56b43a4f051f29071f14218d").execute();
            if (xeeRequest.isSuccessful()) {
                collector.checkThat("no error", xeeRequest.errorBody(), nullValue());
                collector.checkThat("xeeRequest", xeeRequest.body(), notNullValue());

                collector.checkThat("type", xeeRequest.body().getType(), is("MILEAGE"));
                collector.checkThat("value", xeeRequest.body().getValue(), is(4.2d));
            } else {
                collector.addError(ErrorUtils.parseError(xeeRequest.errorBody().string()));
            }
        } catch (IOException e) {
            collector.addError(e);
        }
    }

    @Test
    public void getUsedTime() {
        try {
            Response<Stat<Long>> xeeRequest = tripsService.getUsedTime("56b43a4f051f29071f14218d").execute();
            if (xeeRequest.isSuccessful()) {
                collector.checkThat("no error", xeeRequest.errorBody(), nullValue());
                collector.checkThat("xeeRequest", xeeRequest.body(), notNullValue());

                collector.checkThat("type", xeeRequest.body().getType(), is("USED_TIME"));
                collector.checkThat("value", xeeRequest.body().getValue(), is(4200L));
            } else {
                collector.addError(ErrorUtils.parseError(xeeRequest.errorBody().string()));
            }
        } catch (IOException e) {
            collector.addError(e);
        }
    }
}