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

import com.xee.api.entity.Car;
import com.xee.api.entity.Location;
import com.xee.api.entity.Signal;
import com.xee.api.entity.Stat;
import com.xee.api.entity.Status;
import com.xee.api.entity.Trip;

import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.QueryMap;

public interface CarsService {

    final class Routes {
        public static final String CAR = "cars/{" + Parameters.CAR_ID + "}";
        public static final String STATUS = "cars/{" + Parameters.CAR_ID + "}/status";
        public static final String LOCATIONS = "cars/{" + Parameters.CAR_ID + "}/locations";
        public static final String SIGNALS = "cars/{" + Parameters.CAR_ID + "}/signals";
        public static final String TRIPS = "cars/{" + Parameters.CAR_ID + "}/trips";
        public static final String MILEAGE = "cars/{" + Parameters.CAR_ID + "}/stats/mileage";
        public static final String USED_TIME = "cars/{" + Parameters.CAR_ID + "}/stats/usedtime";
    }

    final class Parameters {
        public static final String CAR_ID = "carId";
        public static final String BEGIN_DATE = "begin";
        public static final String END_DATE = "end";
        public static final String SIGNAL_NAMES = "name";
        public static final String LIMIT = "limit";
        public static final String INITIAL_VALUE = "initialValue";
    }

    @GET(Routes.CAR)
    Call<Car> getCar(@Path(Parameters.CAR_ID) String carId);

    @GET(Routes.STATUS)
    Call<Status> getStatus(@Path(Parameters.CAR_ID) String carId);

    @GET(Routes.LOCATIONS)
    Call<List<Location>> getLocations(@Path(Parameters.CAR_ID) String carId, @QueryMap Map<String, String> options);

    @GET(Routes.SIGNALS)
    Call<List<Signal>> getSignals(@Path(Parameters.CAR_ID) String carId, @QueryMap Map<String, String> options);

    @GET(Routes.TRIPS)
    Call<List<Trip>> getTrips(@Path(Parameters.CAR_ID) String carId, @QueryMap Map<String, String> options);

    @GET(Routes.MILEAGE)
    Call<Stat<Double>> getMileage(@Path(Parameters.CAR_ID) String carId, @QueryMap Map<String, String> options);

    @GET(Routes.USED_TIME)
    Call<Stat<Long>> getUsedTime(@Path(Parameters.CAR_ID) String carId, @QueryMap Map<String, String> options);
}