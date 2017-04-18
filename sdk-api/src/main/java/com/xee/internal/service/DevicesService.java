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

import retrofit2.Call;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface DevicesService {

    final class Routes {

        public static final String ASSOCIATE_USER_WITH_DEVICE = "devices/{deviceId}/associate";
        public static final String ASSOCIATE_CAR_WITH_DEVICE = "devices/{deviceId}/associate";
        public static final String DISSOCIATE = "devices/{deviceId}/dissociate";
    }

    final class Parameters {
        public static final String DEVICE_ID = "deviceId";
        public static final String PIN = "pin";
        public static final String CAR_ID = "carId";
    }

    @Headers("Content-Type: application/json")
    @POST(Routes.ASSOCIATE_USER_WITH_DEVICE)
    Call<Void> associateUserWithDevice(@Path(Parameters.DEVICE_ID) String deviceId, @Query(Parameters.PIN) String pin);

    @Headers("Content-Type: application/json")
    @POST(Routes.ASSOCIATE_CAR_WITH_DEVICE)
    Call<Void> associateCarWithDevice(@Path(Parameters.DEVICE_ID) String deviceId, @Query(Parameters.CAR_ID) String carId);

    @Headers("Content-Type: application/json")
    @POST(Routes.DISSOCIATE)
    Call<Void> dissociate(@Path(Parameters.DEVICE_ID) String deviceId);
}