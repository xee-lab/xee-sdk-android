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
import com.xee.api.entity.Location;
import com.xee.api.entity.Signal;
import com.xee.api.entity.Stat;
import com.xee.api.entity.Status;
import com.xee.api.entity.Trip;
import com.xee.core.ErrorUtils;
import com.xee.internal.APIInterceptor;
import com.xee.internal.service.CarsService;

import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ErrorCollector;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.io.IOException;
import java.util.Date;
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
public class TestCarsService {

    private CarsService carsService;

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
        carsService = retrofit.create(CarsService.class);
    }

    @Test
    public void getCar() {
        try {
            Response<Car> xeeRequest = carsService.getCar("1337").execute();
            if (xeeRequest.isSuccessful()) {
                collector.checkThat("no error", xeeRequest.errorBody(), nullValue());
                collector.checkThat("item", xeeRequest.body(), notNullValue());

                collector.checkThat("id", xeeRequest.body().getId(), is("1337"));
                collector.checkThat("name", xeeRequest.body().getName(), is("Mark-42"));
                collector.checkThat("make", xeeRequest.body().getMake(), is("Mark"));
                collector.checkThat("model", xeeRequest.body().getModel(), is("42"));
                collector.checkThat("year", xeeRequest.body().getYear(), is(2014));
                collector.checkThat("numberPlate", xeeRequest.body().getNumberPlate(), is("M-42-TS"));
                collector.checkThat("deviceId", xeeRequest.body().getDeviceId(), is("E133742015"));
                collector.checkThat("cardbId", xeeRequest.body().getCardbId(), is(210));
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
            Map<String, String> options = new HashMap<>(3);
            options.put(CarsService.Parameters.BEGIN_DATE, Xee.DATE_FORMATTER.format(new Date()));
            options.put(CarsService.Parameters.END_DATE, Xee.DATE_FORMATTER.format(new Date()));
            options.put(CarsService.Parameters.LIMIT, String.valueOf(10));

            Response<List<Location>> xeeRequest = carsService.getLocations("1337", options).execute();
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
            Map<String, String> options = new HashMap<>(4);
            options.put(CarsService.Parameters.BEGIN_DATE, Xee.DATE_FORMATTER.format(new Date()));
            options.put(CarsService.Parameters.END_DATE, Xee.DATE_FORMATTER.format(new Date()));
            options.put(CarsService.Parameters.LIMIT, String.valueOf(10));
            options.put(CarsService.Parameters.SIGNAL_NAMES, "name=EngineSpeed");

            Response<List<Signal>> xeeRequest = carsService.getSignals("1337", options).execute();
            if (xeeRequest.isSuccessful()) {
                collector.checkThat("no error", xeeRequest.errorBody(), nullValue());
                collector.checkThat("item", xeeRequest.body(), notNullValue());

                collector.checkThat("size", xeeRequest.body(), hasSize(2));
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
    public void getStatus() {
        try {
            Response<Status> xeeRequest = carsService.getStatus("1337").execute();
            if (xeeRequest.isSuccessful()) {
                collector.checkThat("no error", xeeRequest.errorBody(), nullValue());
                collector.checkThat("xeeRequest", xeeRequest.body(), notNullValue());

                collector.checkThat("accelerometer x", xeeRequest.body().getAccelerometer().getX(), is(-768d));
                collector.checkThat("accelerometer y", xeeRequest.body().getAccelerometer().getY(), is(240d));
                collector.checkThat("accelerometer z", xeeRequest.body().getAccelerometer().getZ(), is(4032d));

                collector.checkThat("location latitude", xeeRequest.body().getLocation().getLatitude(), is(50.67815));
                collector.checkThat("location longitude", xeeRequest.body().getLocation().getLongitude(), is(3.208155));
                collector.checkThat("location altitude", xeeRequest.body().getLocation().getAltitude(), is(31.8));
                collector.checkThat("location satellites", xeeRequest.body().getLocation().getSatellites(), is(4));
                collector.checkThat("location heading", xeeRequest.body().getLocation().getHeading(), is(167.00));

                collector.checkThat("contains lock", xeeRequest.body().getSignals(), hasItem(
                        Matchers.<Signal<Double>>allOf(hasProperty("name", is(Signal.Name.LOCK.name)), hasProperty("value", is(0d)))
                ));
                collector.checkThat("contains odometer", xeeRequest.body().getSignals(), hasItem(
                        Matchers.<Signal<Double>>allOf(hasProperty("name", is(Signal.Name.ODOMETER.name)), hasProperty("value", is(34512.1d)))
                ));
            } else {
                collector.addError(ErrorUtils.parseError(xeeRequest.errorBody().string()));
            }
        } catch (IOException e) {
            collector.addError(e);
        }
    }

    @Test
    public void getTrips() {
        try {
            Map<String, String> options = new HashMap<>(2);
            options.put(CarsService.Parameters.BEGIN_DATE, Xee.DATE_FORMATTER.format(new Date()));
            options.put(CarsService.Parameters.END_DATE, Xee.DATE_FORMATTER.format(new Date()));

            Response<List<Trip>> xeeRequest = carsService.getTrips("1337", options).execute();
            if (xeeRequest.isSuccessful()) {
                collector.checkThat("no error", xeeRequest.errorBody(), nullValue());
                collector.checkThat("xeeRequest", xeeRequest.body(), notNullValue());

                collector.checkThat("size", xeeRequest.body(), hasSize(1));
                collector.checkThat("trip", xeeRequest.body(), hasItem(Matchers.<Trip>allOf(
                        hasProperty("id", is("56b43a4f051f29071f14218d")),
                        hasProperty("beginLocation", is(Matchers.<Location>allOf(
                                hasProperty("latitude", is(50.6817)),
                                hasProperty("longitude", is(3.08202)),
                                hasProperty("altitude", is(2d)),
                                hasProperty("satellites", is(1)),
                                hasProperty("heading", is(0.0))
                        ))),
                        hasProperty("endLocation", is(Matchers.<Location>allOf(
                                hasProperty("latitude", is(50.6817)),
                                hasProperty("longitude", is(3.08202)),
                                hasProperty("altitude", is(2d)),
                                hasProperty("satellites", is(1)),
                                hasProperty("heading", is(0.0))
                        )))

                )));
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
            Map<String, String> options = new HashMap<>(3);
            options.put(CarsService.Parameters.BEGIN_DATE, Xee.DATE_FORMATTER.format(new Date()));
            options.put(CarsService.Parameters.END_DATE, Xee.DATE_FORMATTER.format(new Date()));
            options.put(CarsService.Parameters.INITIAL_VALUE, String.valueOf(0.0));

            Response<Stat<Double>> xeeRequest = carsService.getMileage("1337", options).execute();
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
            Map<String, String> options = new HashMap<>(3);
            options.put(CarsService.Parameters.BEGIN_DATE, Xee.DATE_FORMATTER.format(new Date()));
            options.put(CarsService.Parameters.END_DATE, Xee.DATE_FORMATTER.format(new Date()));
            options.put(CarsService.Parameters.INITIAL_VALUE, String.valueOf(0.0));

            Response<Stat<Long>> xeeRequest = carsService.getUsedTime("1337", options).execute();
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