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

import java.io.IOException;

import retrofit2.Call;

/**
 * A Xee request allowing to execute in synchronous or asynchronous way
 *
 * @param <T> the data associated to the request
 */
public final class XeeRequest<T> {

    /**
     * Callback used to get async result of a {@link XeeRequest}
     *
     * @param <T> the expected type of result from the {@link XeeRequest}
     */
    public interface Callback<T> {
        void onSuccess(final T response);

        void onError(Error error);
    }

    /**
     * Object sent after a sync {@link XeeRequest} execution
     */
    public class Response {
        public final T item;
        public final Error error;

        public Response(T item, Error error) {
            this.item = item;
            this.error = error;
        }
    }

    private final Call<T> request;

    /**
     * Create a request
     *
     * @param apiCall the api call
     */
    public XeeRequest(Call<T> apiCall) {
        this.request = apiCall;
    }

    /**
     * Synchronously send the request and return its response.
     *
     * @return the returned {@link Response}
     */
    public Response execute() {
        try {
            retrofit2.Response<T> tResponse = request.execute();
            if (tResponse.isSuccessful()) {
                return new Response(tResponse.body(), null);
            } else {
                return new Response(null, ErrorUtils.parseError(tResponse.errorBody().string()));
            }
        } catch (Exception e) {
            return new Response(null, new Error(e));
        }
    }

    /**
     * Asynchronously send the request and notify {@link Callback} of its response or if an error
     * occurred talking to the server, creating the request, or processing the response.
     *
     * @param callback the {@link Callback} of the request
     */
    public void enqueue(final Callback<T> callback) {
        request.enqueue(new retrofit2.Callback<T>() {
            @Override
            public void onResponse(Call<T> call, retrofit2.Response<T> tResponse) {
                if (tResponse.isSuccessful()) {
                    callback.onSuccess(tResponse.body());
                } else {
                    try {
                        callback.onError(ErrorUtils.parseError(tResponse.errorBody().string()));
                    } catch (IOException e) {
                        callback.onError(new Error(e));
                    }
                }
            }

            @Override
            public void onFailure(Call<T> call, Throwable t) {
                callback.onError(new Error(new Exception(t)));
            }
        });
    }
}