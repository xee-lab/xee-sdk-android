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

package com.xee.core.entity;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.Nullable;

/**
 * Represents an Error from requests or code
 */
public class Error extends Exception implements Parcelable {

    private final String type;
    private final String tip;

    /**
     * Create an {@link Error}
     *
     * @param cause the error cause
     */
    public Error(Exception cause) {
        super(cause);
        this.type = "See cause";
        this.tip = "See cause";
    }

    /**
     * Create an {@link Error}
     *
     * @param message the message on the error
     * @param type    the error type
     * @param tip     the tip explaining how to fix the error
     */
    public Error(String message, @Nullable String type, @Nullable String tip) {
        super(message);
        this.type = type;
        this.tip = tip;
    }

    /**
     * @return the type of the error
     */
    public String getType() {
        return type;
    }

    /**
     * @return a tip to fix the error
     */
    public String getTip() {
        return tip;
    }

    @Override
    public String toString() {
        return super.toString() + "||Error{" +
                "type='" + type + '\'' +
                ", tip='" + tip + '\'' +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.type);
        dest.writeString(this.tip);
    }

    protected Error(Parcel in) {
        this.type = in.readString();
        this.tip = in.readString();
    }

    public static final Parcelable.Creator<Error> CREATOR = new Parcelable.Creator<Error>() {
        @Override
        public Error createFromParcel(Parcel source) {
            return new Error(source);
        }

        @Override
        public Error[] newArray(int size) {
            return new Error[size];
        }
    };
}