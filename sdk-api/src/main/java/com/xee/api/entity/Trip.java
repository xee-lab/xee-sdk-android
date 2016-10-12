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

import java.util.Date;

public class Trip implements Parcelable {

    @SerializedName("id")
    private String id;
    @SerializedName("beginLocation")
    private Location beginLocation;
    @SerializedName("endLocation")
    private Location endLocation;
    @SerializedName("beginDate")
    private Date beginDate;
    @SerializedName("endDate")
    private Date endDate;
    @SerializedName("creationDate")
    private Date creationDate;
    @SerializedName("lastUpdateDate")
    private Date lastUpdateDate;

    public Trip() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Location getBeginLocation() {
        return beginLocation;
    }

    public void setBeginLocation(Location beginLocation) {
        this.beginLocation = beginLocation;
    }

    public Location getEndLocation() {
        return endLocation;
    }

    public void setEndLocation(Location endLocation) {
        this.endLocation = endLocation;
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

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    public Date getLastUpdateDate() {
        return lastUpdateDate;
    }

    public void setLastUpdateDate(Date lastUpdateDate) {
        this.lastUpdateDate = lastUpdateDate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Trip trip = (Trip) o;

        if (id != null ? !id.equals(trip.id) : trip.id != null) return false;
        if (beginLocation != null ? !beginLocation.equals(trip.beginLocation) : trip.beginLocation != null)
            return false;
        if (endLocation != null ? !endLocation.equals(trip.endLocation) : trip.endLocation != null)
            return false;
        if (beginDate != null ? !beginDate.equals(trip.beginDate) : trip.beginDate != null)
            return false;
        if (endDate != null ? !endDate.equals(trip.endDate) : trip.endDate != null) return false;
        if (creationDate != null ? !creationDate.equals(trip.creationDate) : trip.creationDate != null)
            return false;
        return !(lastUpdateDate != null ? !lastUpdateDate.equals(trip.lastUpdateDate) : trip.lastUpdateDate != null);

    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (beginLocation != null ? beginLocation.hashCode() : 0);
        result = 31 * result + (endLocation != null ? endLocation.hashCode() : 0);
        result = 31 * result + (beginDate != null ? beginDate.hashCode() : 0);
        result = 31 * result + (endDate != null ? endDate.hashCode() : 0);
        result = 31 * result + (creationDate != null ? creationDate.hashCode() : 0);
        result = 31 * result + (lastUpdateDate != null ? lastUpdateDate.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Trip{" +
                "id='" + id + '\'' +
                ", beginLocation=" + beginLocation +
                ", endLocation=" + endLocation +
                ", beginDate=" + beginDate +
                ", endDate=" + endDate +
                ", creationDate=" + creationDate +
                ", lastUpdateDate=" + lastUpdateDate +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeParcelable(this.beginLocation, flags);
        dest.writeParcelable(this.endLocation, flags);
        dest.writeLong(this.beginDate != null ? this.beginDate.getTime() : -1);
        dest.writeLong(this.endDate != null ? this.endDate.getTime() : -1);
        dest.writeLong(this.creationDate != null ? this.creationDate.getTime() : -1);
        dest.writeLong(this.lastUpdateDate != null ? this.lastUpdateDate.getTime() : -1);
    }

    protected Trip(Parcel in) {
        this.id = in.readString();
        this.beginLocation = in.readParcelable(Location.class.getClassLoader());
        this.endLocation = in.readParcelable(Location.class.getClassLoader());
        long tmpBeginDate = in.readLong();
        this.beginDate = tmpBeginDate == -1 ? null : new Date(tmpBeginDate);
        long tmpEndDate = in.readLong();
        this.endDate = tmpEndDate == -1 ? null : new Date(tmpEndDate);
        long tmpCreationDate = in.readLong();
        this.creationDate = tmpCreationDate == -1 ? null : new Date(tmpCreationDate);
        long tmpLastUpdateDate = in.readLong();
        this.lastUpdateDate = tmpLastUpdateDate == -1 ? null : new Date(tmpLastUpdateDate);
    }

    public static final Parcelable.Creator<Trip> CREATOR = new Parcelable.Creator<Trip>() {
        @Override
        public Trip createFromParcel(Parcel source) {
            return new Trip(source);
        }

        @Override
        public Trip[] newArray(int size) {
            return new Trip[size];
        }
    };
}