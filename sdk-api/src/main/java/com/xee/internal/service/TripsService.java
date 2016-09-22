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

import com.xee.api.entity.Location;
import com.xee.api.entity.Signal;
import com.xee.api.entity.Stat;
import com.xee.api.entity.Trip;

import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.QueryMap;

public interface TripsService {

    final class Routes {
        public static final String TRIP = "trips/{" + Parameters.TRIP_ID + "}";
        public static final String LOCATIONS = "trips/{" + Parameters.TRIP_ID + "}/locations";
        public static final String SIGNALS = "trips/{" + Parameters.TRIP_ID + "}/signals";
        public static final String STATS = "trips/{" + Parameters.TRIP_ID + "}/stats";
        public static final String MILEAGE = "trips/{" + Parameters.TRIP_ID + "}/stats/mileage";
        public static final String USED_TIME = "trips/{" + Parameters.TRIP_ID + "}/stats/usedtime";
    }

    final class Parameters {
        public static final String TRIP_ID = "tripId";
        public static final String SIGNAL_NAMES = "name";
        public static final String LIMIT = "limit";
    }

    @GET(Routes.TRIP)
    Call<Trip> getTrip(@Path(Parameters.TRIP_ID) String tripId);

    @GET(Routes.LOCATIONS)
    Call<List<Location>> getLocations(@Path(Parameters.TRIP_ID) String tripId);

    @GET(Routes.SIGNALS)
    Call<List<Signal>> getSignals(@Path(Parameters.TRIP_ID) String tripId, @QueryMap Map<String, String> options);

    @GET(Routes.STATS)
    Call<List<Stat>> getStats(@Path(Parameters.TRIP_ID) String tripId);

    @GET(Routes.MILEAGE)
    Call<Stat<Double>> getMileage(@Path(Parameters.TRIP_ID) String tripId);

    @GET(Routes.USED_TIME)
    Call<Stat<Long>> getUsedTime(@Path(Parameters.TRIP_ID) String tripId);

}