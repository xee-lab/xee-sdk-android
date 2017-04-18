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
import com.xee.api.entity.Device;
import com.xee.api.entity.User;

import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface UsersService {

    final class Routes {
        public static final String ME = "users/me";
        public static final String CARS = "users/me/cars";
        public static final String CREATE_CAR = "users/{userId}/cars";
        public static final String DEVICES = "users/{userId}/devices";
    }

    final class Parameters {
        public static final String USER_ID = "userId";
    }

    @GET(Routes.ME)
    Call<User> getUser();

    @GET(Routes.CARS)
    Call<List<Car>> getCars();

    @Headers("Content-Type: application/json")
    @POST(Routes.CREATE_CAR)
    Call<Car> createCar(@Path(Parameters.USER_ID) String userId, @Body Map<String, Object> fields);

    @GET(Routes.DEVICES)
    Call<List<Device>> getDevices(@Path(Parameters.USER_ID) String userId);
}