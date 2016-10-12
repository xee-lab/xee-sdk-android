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

public class Stat<T> implements Parcelable {

    @SerializedName("beginDate")
    private Date beginDate;
    @SerializedName("endDate")
    private Date endDate;
    @SerializedName("type")
    private String type;
    @SerializedName("value")
    private T value;

    public Stat() {
    }

    public Date getBeginDate() {
        return beginDate;
    }

    public void setBeginDate(Date beginDate) {
        this.beginDate = beginDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public T getValue() {
        return value;
    }

    public void setValue(T value) {
        this.value = value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Stat<?> stat = (Stat<?>) o;

        if (beginDate != null ? !beginDate.equals(stat.beginDate) : stat.beginDate != null)
            return false;
        if (endDate != null ? !endDate.equals(stat.endDate) : stat.endDate != null) return false;
        if (type != null ? !type.equals(stat.type) : stat.type != null) return false;
        return value != null ? value.equals(stat.value) : stat.value == null;

    }

    @Override
    public int hashCode() {
        int result = beginDate != null ? beginDate.hashCode() : 0;
        result = 31 * result + (endDate != null ? endDate.hashCode() : 0);
        result = 31 * result + (type != null ? type.hashCode() : 0);
        result = 31 * result + (value != null ? value.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Stat{" +
                ", beginDate=" + beginDate +
                ", endDate=" + endDate +
                ", type=" + type +
                ", value=" + value +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.beginDate != null ? this.beginDate.getTime() : -1);
        dest.writeLong(this.endDate != null ? this.endDate.getTime() : -1);
        dest.writeString(this.type);
        dest.writeValue(this.value);
    }

    @SuppressWarnings("unchecked")
    protected Stat(Parcel in) {
        long tmpBeginDate = in.readLong();
        this.beginDate = tmpBeginDate == -1 ? null : new Date(tmpBeginDate);
        long tmpEndDate = in.readLong();
        this.endDate = tmpEndDate == -1 ? null : new Date(tmpEndDate);
        this.type = in.readString();
        try {
            this.value = (T) in.readValue(Signal.class.getClassLoader());
        } catch (Exception e) {
            if(BuildConfig.DEBUG) {
                Log.e(Signal.class.getSimpleName(), "Error getting value: ", e);
            }
        }
    }

    public static final Parcelable.Creator<Stat> CREATOR = new Parcelable.Creator<Stat>() {
        @Override
        public Stat createFromParcel(Parcel source) {
            return new Stat(source);
        }

        @Override
        public Stat[] newArray(int size) {
            return new Stat[size];
        }
    };
}