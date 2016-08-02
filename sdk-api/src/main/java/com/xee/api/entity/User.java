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

import com.google.gson.annotations.SerializedName;

import java.util.Date;

public class User {

    @SerializedName("id")
    private int id;
    @SerializedName("lastName")
    private String lastName;
    @SerializedName("firstName")
    private String firstName;
    @SerializedName("nickname")
    private String nickName;
    @SerializedName("gender")
    private Gender gender;
    @SerializedName("birthDate")
    private Date birthDate;
    @SerializedName("licenceDeliveryDate")
    private Date licenceDeliveryDate;
    @SerializedName("role")
    private String role;
    @SerializedName("isLocationEnabled")
    private boolean isLocationEnabled;
    @SerializedName("creationDate")
    private Date creationDate;
    @SerializedName("lastUpdateDate")
    private Date lastUpdateDate;

    public enum Gender {
        MALE, FEMALE
    }

    public String getId() {
        return String.valueOf(id);
    }

    public void setId(String id) {
        this.id = Integer.parseInt(id);
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public Gender getGender() {
        return gender;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }

    public Date getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(Date birthDate) {
        this.birthDate = birthDate;
    }

    public Date getLicenceDeliveryDate() {
        return licenceDeliveryDate;
    }

    public void setLicenceDeliveryDate(Date licenceDeliveryDate) {
        this.licenceDeliveryDate = licenceDeliveryDate;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public boolean isLocationEnabled() {
        return isLocationEnabled;
    }

    public void setIsLocationEnabled(boolean isLocationEnabled) {
        this.isLocationEnabled = isLocationEnabled;
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

        User user = (User) o;

        if (id != user.id) return false;
        if (isLocationEnabled != user.isLocationEnabled) return false;
        if (lastName != null ? !lastName.equals(user.lastName) : user.lastName != null)
            return false;
        if (firstName != null ? !firstName.equals(user.firstName) : user.firstName != null)
            return false;
        if (nickName != null ? !nickName.equals(user.nickName) : user.nickName != null)
            return false;
        if (gender != user.gender) return false;
        if (birthDate != null ? !birthDate.equals(user.birthDate) : user.birthDate != null)
            return false;
        if (licenceDeliveryDate != null ? !licenceDeliveryDate.equals(user.licenceDeliveryDate) : user.licenceDeliveryDate != null)
            return false;
        if (role != null ? !role.equals(user.role) : user.role != null) return false;
        if (creationDate != null ? !creationDate.equals(user.creationDate) : user.creationDate != null)
            return false;
        return !(lastUpdateDate != null ? !lastUpdateDate.equals(user.lastUpdateDate) : user.lastUpdateDate != null);

    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + (lastName != null ? lastName.hashCode() : 0);
        result = 31 * result + (firstName != null ? firstName.hashCode() : 0);
        result = 31 * result + (nickName != null ? nickName.hashCode() : 0);
        result = 31 * result + (gender != null ? gender.hashCode() : 0);
        result = 31 * result + (birthDate != null ? birthDate.hashCode() : 0);
        result = 31 * result + (licenceDeliveryDate != null ? licenceDeliveryDate.hashCode() : 0);
        result = 31 * result + (role != null ? role.hashCode() : 0);
        result = 31 * result + (isLocationEnabled ? 1 : 0);
        result = 31 * result + (creationDate != null ? creationDate.hashCode() : 0);
        result = 31 * result + (lastUpdateDate != null ? lastUpdateDate.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", lastName='" + lastName + '\'' +
                ", firstName='" + firstName + '\'' +
                ", nickName='" + nickName + '\'' +
                ", gender=" + gender +
                ", birthDate=" + birthDate +
                ", licenceDeliveryDate=" + licenceDeliveryDate +
                ", role='" + role + '\'' +
                ", isLocationEnabled=" + isLocationEnabled +
                ", creationDate=" + creationDate +
                ", lastUpdateDate=" + lastUpdateDate +
                '}';
    }
}