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

package com.xee.core;

import com.xee.core.entity.Error;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Used to handle errors from API
 */
public class ErrorUtils {
    /**
     * Parse an error returned from the API
     *
     * @param errorBody the response of the API
     * @return the Error sent
     */
    public static Error parseError(String errorBody) {
        try {
            Error error = null;
            if (errorBody.trim().charAt(0) == '[') {
                JSONArray errorArray = new JSONArray(errorBody);
                JSONObject errorArrayJSONObject = errorArray.getJSONObject(0);

                if (errorArrayJSONObject.has("type")) {
                    error = new Error(errorArrayJSONObject.getString("message"), errorArrayJSONObject.getString("type"), errorArrayJSONObject.getString("tip"));
                } else {
                    error = new Error(errorArrayJSONObject.getString("message"), null, null);
                }
            } else if (errorBody.trim().charAt(0) == '{') {
                JSONObject errorArrayJSONObject = new JSONObject(errorBody);
                if (errorArrayJSONObject.has("type")) {
                    error = new Error(errorArrayJSONObject.getString("message"), errorArrayJSONObject.getString("type"), errorArrayJSONObject.getString("tip"));
                } else {
                    error = new Error(errorArrayJSONObject.getString("message"), null, null);
                }
            }

            return error;

        } catch (JSONException e) {
            return new Error(e);
        }
    }
}