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

import com.google.gson.GsonBuilder;
import com.xee.internal.service.AuthService;
import com.xee.internal.service.CarsService;
import com.xee.internal.service.TripsService;
import com.xee.internal.service.UsersService;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.Protocol;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class APIInterceptor implements Interceptor {

    private String path(String name) {
        return "/v3/" + name;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        HttpUrl requestedUrl = request.url();
        String path = requestedUrl.encodedPath();
        String response = "";
        // For debug util
        System.out.println("Request received  : " + path);
        if (path.equals(path(UsersService.Routes.ME))) {
            response = getUser();
        } else if (path.equals(path(UsersService.Routes.CARS))) {
            response = getCars();
        } else if (path.equals(path(CarsService.Routes.CAR.replace("{" + CarsService.Parameters.CAR_ID + "}", "1337")))) {
            response = getCar();
        } else if (path.equals(path(CarsService.Routes.LOCATIONS.replace("{" + CarsService.Parameters.CAR_ID + "}", "1337")))) {
            response = getLocations();
        } else if (path.equals(path(CarsService.Routes.SIGNALS.replace("{" + CarsService.Parameters.CAR_ID + "}", "1337")))) {
            response = getSignals();
        } else if (path.equals(path(CarsService.Routes.STATUS.replace("{" + CarsService.Parameters.CAR_ID + "}", "1337")))) {
            response = getStatus();
        } else if (path.equals(path(CarsService.Routes.TRIPS.replace("{" + CarsService.Parameters.CAR_ID + "}", "1337")))) {
            response = getTrips();
        } else if (path.equals(path(TripsService.Routes.TRIP.replace("{" + TripsService.Parameters.TRIP_ID + "}", "56b43a4f051f29071f14218d")))) {
            response = getTrip();
        } else if (path.equals(path(TripsService.Routes.LOCATIONS.replace("{" + TripsService.Parameters.TRIP_ID + "}", "56b43a4f051f29071f14218d")))) {
            response = getLocations();
        } else if (path.equals(path(TripsService.Routes.SIGNALS.replace("{" + TripsService.Parameters.TRIP_ID + "}", "56b43a4f051f29071f14218d")))) {
            response = getSignals();
        } else if (path.equals(path(AuthService.Routes.ACCESS_TOKEN))) {
            response = getToken();
        } else if (path.equals(path(CarsService.Routes.MILEAGE.replace("{" + CarsService.Parameters.CAR_ID + "}", "1337")))) {
            response = getMileage();
        } else if (path.equals(path(CarsService.Routes.USED_TIME.replace("{" + CarsService.Parameters.CAR_ID + "}", "1337")))) {
            response = getUsedTime();
        }

        System.out.println("XeeRequest returned : " + response);
        System.out.println("--------------------------");
        return new Response.Builder()
                .request(request)
                .addHeader("Content-Type", "application/json")
                .protocol(Protocol.HTTP_1_1)
                .code(200)
                .body(ResponseBody.create(MediaType.parse("application/json"), response))
                .build();
    }

    private String json(Object item) {
        return new GsonBuilder().setPrettyPrinting().create().toJson(item);
    }

    private String getUser() {
        Map<String, Object> response = new HashMap<>();
        response.put("id", 42);
        response.put("lastName", "Doe");
        response.put("firstName", "John");
        response.put("nickname", "Johny");
        response.put("gender", "MALE");
        response.put("birthDate", "2016-01-11T00:00:00+00:00");
        response.put("licenceDeliveryDate", "2014-08-13T00:00:00+00:00");
        response.put("role", "dev");
        response.put("isLocationEnabled", true);
        response.put("creationDate", "2014-08-13T15:20:58+00:00");
        response.put("lastUpdateDate", "2016-02-12T09:07:47+00:00");
        return json(response);
    }

    private String getCars() {
        Map<String, Object> response = new HashMap<>();
        response.put("id", 1337);
        response.put("name", "Mark-42");
        response.put("make", "Mark");
        response.put("model", "42");
        response.put("year", 2014);
        response.put("numberPlate", "M-42-TS");
        response.put("deviceId", "E133742015");
        response.put("cardbId", 210);
        response.put("creationDate", "2014-09-23T12:49:48+00:00");
        response.put("lastUpdateDate", "2016-02-19T08:41:58+00:00");
        return json(Collections.singletonList(response));
    }

    private String getCar() {
        Map<String, Object> response = new HashMap<>();
        response.put("id", 1337);
        response.put("name", "Mark-42");
        response.put("make", "Mark");
        response.put("model", "42");
        response.put("year", 2014);
        response.put("numberPlate", "M-42-TS");
        response.put("deviceId", "E133742015");
        response.put("cardbId", 210);
        response.put("creationDate", "2014-09-23T12:49:48+00:00");
        response.put("lastUpdateDate", "2016-02-19T08:41:58+00:00");
        return json(response);
    }

    private String getLocations() {
        Map<String, Object> response = new HashMap<>();
        response.put("latitude", 50.67815);
        response.put("longitude", 3.208155);
        response.put("altitude", 31.8);
        response.put("satellites", 4);
        response.put("heading", 167.0);
        response.put("date", "2016-03-01T02:24:20.000000+00:00");
        return json(Collections.singletonList(response));
    }

    private String getSignals() {
        Map<String, Object> response1 = new HashMap<>();
        response1.put("name", "LockSts");
        response1.put("value", 0);
        response1.put("date", "2016-03-01T02:24:24.000000+00:00");
        Map<String, Object> response2 = new HashMap<>();
        response2.put("name", "Odometer");
        response2.put("value", 34512.1);
        response2.put("date", "2016-03-01T02:24:27.116000+00:00");
        return json(Arrays.asList(response1, response2));
    }

    private String getStatus() {
        Map<String, Object> accelerometer = new HashMap<>();
        accelerometer.put("x", -768);
        accelerometer.put("y", 240);
        accelerometer.put("z", 4032);
        accelerometer.put("date", "2016-03-01T02:24:20.000000+00:00");
        Map<String, Object> location = new HashMap<>();
        location.put("latitude", 50.67815);
        location.put("longitude", 3.208155);
        location.put("altitude", 31.8);
        location.put("satellites", 4);
        location.put("heading", 167.0);
        location.put("date", "2016-03-01T02:24:20.000000+00:00");
        Map<String, Object> signals1 = new HashMap<>();
        signals1.put("name", "LockSts");
        signals1.put("value", 0);
        signals1.put("date", "2016-03-01T02:24:24.000000+00:00");
        Map<String, Object> signals2 = new HashMap<>();
        signals2.put("name", "Odometer");
        signals2.put("value", 34512.1);
        signals2.put("date", "2016-03-01T02:24:27.116000+00:00");
        Map<String, Object> response = new HashMap<>();
        response.put("accelerometer", accelerometer);
        response.put("location", location);
        response.put("signals", Arrays.asList(signals1, signals2));
        return json(response);
    }

    private String getTrips() {
        Map<String, Object> response = new HashMap<>();
        response.put("id", "56b43a4f051f29071f14218d");
        Map<String, Object> beginLocation = new HashMap<>();
        beginLocation.put("latitude", 50.6817);
        beginLocation.put("longitude", 3.08202);
        beginLocation.put("altitude", 2);
        beginLocation.put("satellites", 1);
        beginLocation.put("heading", 0.0);
        beginLocation.put("date", "2016-01-29T18:36:17Z");
        response.put("beginLocation", beginLocation);
        Map<String, Object> endLocation = new HashMap<>();
        endLocation.put("latitude", 50.6817);
        endLocation.put("longitude", 3.08202);
        endLocation.put("altitude", 2);
        endLocation.put("satellites", 1);
        endLocation.put("heading", 0.0);
        endLocation.put("date", "2016-01-29T18:36:17Z");
        response.put("endLocation", endLocation);
        response.put("beginDate", "2016-01-29T18:36:17Z");
        response.put("endDate", "2016-01-29T19:15:15Z");
        return json(Collections.singletonList(response));
    }

    private String getTrip() {
        Map<String, Object> response = new HashMap<>();
        response.put("id", "56b43a4f051f29071f14218d");
        Map<String, Object> beginLocation = new HashMap<>();
        beginLocation.put("latitude", 50.6817);
        beginLocation.put("longitude", 3.08202);
        beginLocation.put("altitude", 2);
        beginLocation.put("satellites", 1);
        beginLocation.put("heading", 0.0);
        beginLocation.put("date", "2016-01-29T18:36:17Z");
        response.put("beginLocation", beginLocation);
        Map<String, Object> endLocation = new HashMap<>();
        endLocation.put("latitude", 50.6817);
        endLocation.put("longitude", 3.08202);
        endLocation.put("altitude", 2);
        endLocation.put("satellites", 1);
        endLocation.put("heading", 0.0);
        endLocation.put("date", "2016-01-29T18:36:17Z");
        response.put("endLocation", endLocation);
        response.put("beginDate", "2016-01-29T18:36:17Z");
        response.put("endDate", "2016-01-29T19:15:15Z");
        return json(response);
    }


    private String getToken() {
        Map<String, Object> response = new HashMap<>();
        response.put("access_token", "22fe0c13e995da4a44a63a7ff549badb5d337a42bf80f17424482e35d4cca91a");
        response.put("expires_at", 1382962374);
        response.put("expires_in", 3600);
        response.put("refresh_token", "8eb667707535655f2d9e14fc6491a59f6e06f2e73170761259907d8de186b6a1");
        response.put("token_type", "bearer");
        return json(response);
    }

    private String getMileage() {
        Map<String, Object> response = new HashMap<>();
        response.put("beginDate", "2016-07-01T00:00:00Z");
        response.put("endDate", "2016-07-15T12:34:30.447653854Z");
        response.put("type", "MILEAGE");
        response.put("value", 4.2);
        return json(response);
    }

    private String getUsedTime() {
        Map<String, Object> response = new HashMap<>();
        response.put("beginDate", "2016-07-01T00:00:00Z");
        response.put("endDate", "2016-07-15T12:34:30.447653854Z");
        response.put("type", "USED_TIME");
        response.put("value", 4200);
        return json(response);
    }
}
