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
import com.xee.api.entity.User;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

public interface UsersService {

    final class Routes {
        public static final String ME = "users/me";
        public static final String CARS = "users/me/cars";
    }

    @GET(Routes.ME)
    Call<User> getUser();

    @GET(Routes.CARS)
    Call<List<Car>> getCars();
}