package com.xee.api.entity;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

public class Device implements Parcelable {

    public static final int STATUS_NORMAL = 0;
    public static final int STATUS_SETUP = 3;

    @SerializedName("status")
    private int status;
    @SerializedName("xeeId")
    private String xeeId;
    @SerializedName("associationAttempts")
    private int associationAttempts;

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getXeeId() {
        return xeeId;
    }

    public void setXeeId(String xeeId) {
        this.xeeId = xeeId;
    }

    public int getAssociationAttempts() {
        return associationAttempts;
    }

    public void setAssociationAttempts(int associationAttempts) {
        this.associationAttempts = associationAttempts;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Device device = (Device) o;

        if (status != device.status) return false;
        if (associationAttempts != device.associationAttempts) return false;
        return xeeId.equals(device.xeeId);

    }

    @Override
    public int hashCode() {
        int result = status;
        result = 31 * result + xeeId.hashCode();
        result = 31 * result + associationAttempts;
        return result;
    }

    @Override
    public String toString() {
        return "Device{" +
                "status=" + status +
                ", xeeId='" + xeeId + '\'' +
                ", associationAttempts=" + associationAttempts +
                '}';
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.status);
        dest.writeString(this.xeeId);
        dest.writeInt(this.associationAttempts);
    }

    public Device() {
    }

    protected Device(Parcel in) {
        this.status = in.readInt();
        this.xeeId = in.readString();
        this.associationAttempts = in.readInt();
    }

    public static final Parcelable.Creator<Device> CREATOR = new Parcelable.Creator<Device>() {
        @Override
        public Device createFromParcel(Parcel source) {
            return new Device(source);
        }

        @Override
        public Device[] newArray(int size) {
            return new Device[size];
        }
    };
}