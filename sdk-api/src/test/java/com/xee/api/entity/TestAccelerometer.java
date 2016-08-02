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

package com.xee.api.entity;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ErrorCollector;

import java.util.Calendar;
import java.util.Date;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.is;

public class TestAccelerometer {

    @Rule
    public ErrorCollector collector = new ErrorCollector();

    @Test
    public void setters() {
        double x = 217.3;
        double y = 432.5;
        double z = 11.98;
        Date date = Calendar.getInstance().getTime();
        Accelerometer accelerometer = new Accelerometer();
        accelerometer.setX(x);
        accelerometer.setY(y);
        accelerometer.setZ(z);
        accelerometer.setDate(date);

        collector.checkThat("x", accelerometer, hasProperty("x", is(x)));
        collector.checkThat("y", accelerometer, hasProperty("y", is(y)));
        collector.checkThat("z", accelerometer, hasProperty("z", is(z)));
        collector.checkThat("date", accelerometer, hasProperty("date", is(date)));
    }

    @Test
    public void getters() {
        double x = 217.3;
        double y = 432.5;
        double z = 11.98;
        Date date = Calendar.getInstance().getTime();
        Accelerometer accelerometer = new Accelerometer();
        accelerometer.setX(x);
        accelerometer.setY(y);
        accelerometer.setZ(z);
        accelerometer.setDate(date);

        collector.checkThat("x", accelerometer.getX(), is(x));
        collector.checkThat("y", accelerometer.getY(), is(y));
        collector.checkThat("z", accelerometer.getZ(), is(z));
        collector.checkThat("date", accelerometer.getDate(), is(date));
    }

    @Test
    public void equalsAndHash() {
        double x = 217.3;
        double y = 432.5;
        double z = 11.98;
        Date date = Calendar.getInstance().getTime();
        Accelerometer accelerometer1 = new Accelerometer();
        accelerometer1.setX(x);
        accelerometer1.setY(y);
        accelerometer1.setZ(z);
        accelerometer1.setDate(date);

        Accelerometer accelerometer2 = new Accelerometer();
        accelerometer2.setX(x);
        accelerometer2.setY(y);
        accelerometer2.setZ(z);
        accelerometer2.setDate(date);

        collector.checkThat("equals ok", accelerometer1, equalTo(accelerometer2));
        collector.checkThat("hash ok", accelerometer1.hashCode(), equalTo(accelerometer2.hashCode()));

        x = 1221.1;
        y = 231.d;
        z = 111.98;
        Date date2 = Calendar.getInstance().getTime();
        Accelerometer accelerometer3 = new Accelerometer();
        accelerometer3.setX(x);
        accelerometer3.setY(y);
        accelerometer3.setZ(z);
        accelerometer3.setDate(date);

        collector.checkThat("equals nok", accelerometer1, not(equalTo(accelerometer3)));
        collector.checkThat("hash nok", accelerometer1.hashCode(), not(equalTo(accelerometer3.hashCode())));

    }

}