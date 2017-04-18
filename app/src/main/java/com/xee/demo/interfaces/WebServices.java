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

package com.xee.demo.interfaces;

public class WebServices {
    public enum WS {
        AUTH("AUTH", true),
        CONNECT("Connect"),
        DISCONNECT("Disconnect"),
        REGISTER("Register"),

        USER("USER", true),
        GET_USER("Get user"),
        GET_USER_CARS("Get user cars"),
        CREATE_CAR("Create car"),
        GET_DEVICES("Get devices"),

        CARS("CARS", true),
        GET_CAR("Get car"),
        GET_STATUS("Get status"),
        GET_LOCATIONS("Get locations"),
        GET_SIGNALS("Get signals"),
        GET_TRIPS("Get trips"),
        GET_MILEAGE("Get mileage"),
        GET_USED_TIME("Get used time"),

        DEVICE("DEVICE", true),
        ASSOCIATE_USER_WITH_DEVICE("Associate user with device"),
        ASSOCIATE_CAR_WITH_DEVICE("Associate car with device"),
        DISSOCIATE("Dissociate"),

        TRIPS("TRIPS", true),
        GET_TRIP("Get trip"),
        GET_TRIP_LOCATIONS("Get trip locations"),
        GET_TRIP_SIGNALS("Get trip signals"),
        GET_TRIP_STATS("Get trip stats"),
        GET_TRIP_MILEAGE("Get trip mileage"),
        GET_TRIP_USED_TIME("Get trip used time");

        private boolean isHeader;
        private String name;

        WS(String name, boolean isHeader) {
            this.name = name;
            this.isHeader = isHeader;
        }

        WS(String name) {
            this.isHeader = false;
            this.name = name;
        }

        public boolean isHeader() {
            return isHeader;
        }

        public String getName() {
            return name;
        }
    }
}
