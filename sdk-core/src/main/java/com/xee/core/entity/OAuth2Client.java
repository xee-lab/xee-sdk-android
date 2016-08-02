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

public class OAuth2Client implements Parcelable {

    public final String clientId;
    public final String clientSecret;
    public final String redirectUri;

    /**
     * Create an {@link OAuth2Client}
     * @param clientId the client id of your application
     * @param clientSecret the client secret of your application
     * @param redirectUri the uri our server will redirect to once the user has entered its credentials
     */
    public OAuth2Client(String clientId, String clientSecret, String redirectUri) {
        this.clientId = clientId;
        this.clientSecret = clientSecret;
        this.redirectUri = redirectUri;
    }

    protected OAuth2Client(Parcel in) {
        this.clientId = in.readString();
        this.clientSecret = in.readString();
        this.redirectUri = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.clientId);
        dest.writeString(this.clientSecret);
        dest.writeString(this.redirectUri);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        OAuth2Client that = (OAuth2Client) o;

        if (clientId != null ? !clientId.equals(that.clientId) : that.clientId != null)
            return false;
        if (clientSecret != null ? !clientSecret.equals(that.clientSecret) : that.clientSecret != null)
            return false;
        return redirectUri != null ? redirectUri.equals(that.redirectUri) : that.redirectUri == null;

    }

    @Override
    public int hashCode() {
        int result = clientId != null ? clientId.hashCode() : 0;
        result = 31 * result + (clientSecret != null ? clientSecret.hashCode() : 0);
        result = 31 * result + (redirectUri != null ? redirectUri.hashCode() : 0);
        return result;
    }

    public static final Parcelable.Creator<OAuth2Client> CREATOR = new Parcelable.Creator<OAuth2Client>() {
        @Override
        public OAuth2Client createFromParcel(Parcel source) {
            return new OAuth2Client(source);
        }

        @Override
        public OAuth2Client[] newArray(int size) {
            return new OAuth2Client[size];
        }
    };
}