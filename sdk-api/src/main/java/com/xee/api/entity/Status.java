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

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Status implements Parcelable {

    @SerializedName("location")
    private Location location;
    @SerializedName("accelerometer")
    private Accelerometer accelerometer;
    @SerializedName("signals")
    private List<Signal> signals;

    public Status() {
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public Accelerometer getAccelerometer() {
        return accelerometer;
    }

    public void setAccelerometer(Accelerometer accelerometer) {
        this.accelerometer = accelerometer;
    }

    public List<Signal> getSignals() {
        return signals;
    }

    public void setSignals(List<Signal> signals) {
        this.signals = signals;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Status status = (Status) o;

        if (location != null ? !location.equals(status.location) : status.location != null)
            return false;
        if (accelerometer != null ? !accelerometer.equals(status.accelerometer) : status.accelerometer != null)
            return false;
        return !(signals != null ? !signals.equals(status.signals) : status.signals != null);

    }

    @Override
    public int hashCode() {
        int result = location != null ? location.hashCode() : 0;
        result = 31 * result + (accelerometer != null ? accelerometer.hashCode() : 0);
        result = 31 * result + (signals != null ? signals.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Status{" +
                "location=" + location +
                ", accelerometer=" + accelerometer +
                ", signals=" + signals +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(this.location, flags);
        dest.writeParcelable(this.accelerometer, flags);
        dest.writeTypedList(this.signals);
    }

    protected Status(Parcel in) {
        this.location = in.readParcelable(Location.class.getClassLoader());
        this.accelerometer = in.readParcelable(Accelerometer.class.getClassLoader());
        this.signals = in.createTypedArrayList(Signal.CREATOR);
    }

    public static final Parcelable.Creator<Status> CREATOR = new Parcelable.Creator<Status>() {
        @Override
        public Status createFromParcel(Parcel source) {
            return new Status(source);
        }

        @Override
        public Status[] newArray(int size) {
            return new Status[size];
        }
    };
}