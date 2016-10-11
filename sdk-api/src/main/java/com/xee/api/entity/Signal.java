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

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import com.google.gson.annotations.SerializedName;
import com.xee.BuildConfig;

import java.util.Date;

public class Signal<T> implements Parcelable {

    public enum Name {

        HIGH_BEAMS("HighBeamSts"),
        LOW_BEAMS("LowBeamSts"),
        HEAD_LIGHTS("HeadLightSts"),
        HAZARD("HazardSts"),
        LEFT_INDICATOR("LeftIndicatorSts"),
        RIGHT_INDICATOR("RightIndicatorSts"),
        FRONT_FOG_LIGHT("FrontFogLightSts"),
        REAR_FOG_LIGHT("RearFogLightSts"),
        FUEL_LEVEL("FuelLevel"),
        IGNITION("IgnitionSts"),
        ENGINE_SPEED("EngineSpeed"),
        VEHICLE_SPEED("VehiculeSpeed"),
        ODOMETER("Odometer"),
        BATTERY_VOLTAGE("BatteryVoltage"),
        INTERMITTENT_WIPER("IntermittentWiperSts"),
        LOW_SPEED_WIPER("LowSpeedWiperSts"),
        MANUAL_WIPER("ManualWiperSts"),
        HIGH_SPEED_WIPER("HighSpeedWiperSts"),
        AUTO_REAR_WIPER("AutoRearWiperSts"),
        MANUAL_REAR_WIPER("ManualRearWiperSts"),
        LOCK("LockSts");


        public final String name;

        Name(String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return "Name{" +
                    "name='" + name + '\'' +
                    '}';
        }
    }

    @SerializedName("name")
    private String name;
    @SerializedName("value")
    private T value;
    @SerializedName("date")
    private Date date;

    public Signal() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public T getValue() {
        return value;
    }

    public void setValue(T value) {
        this.value = value;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Signal<?> signal = (Signal<?>) o;

        if (name != null ? !name.equals(signal.name) : signal.name != null) return false;
        if (value != null ? !value.equals(signal.value) : signal.value != null) return false;
        return date != null ? date.equals(signal.date) : signal.date == null;

    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + (value != null ? value.hashCode() : 0);
        result = 31 * result + (date != null ? date.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Signal{" +
                "name='" + name + '\'' +
                ", value=" + value +
                ", date=" + date +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.name);
        dest.writeValue(this.value);
        dest.writeLong(this.date != null ? this.date.getTime() : -1);
    }

    @SuppressWarnings("unchecked")
    protected Signal(Parcel in) {
        this.name = in.readString();
        try {
            this.value = (T) in.readValue(Signal.class.getClassLoader());
        } catch (Exception e) {
            if(BuildConfig.DEBUG) {
                Log.e(Signal.class.getSimpleName(), "Error getting value: ", e);
            }
        }
        long tmpDate = in.readLong();
        this.date = tmpDate == -1 ? null : new Date(tmpDate);
    }

    public static final Parcelable.Creator<Signal> CREATOR = new Parcelable.Creator<Signal>() {
        @Override
        public Signal createFromParcel(Parcel source) {
            return new Signal(source);
        }

        @Override
        public Signal[] newArray(int size) {
            return new Signal[size];
        }
    };
}