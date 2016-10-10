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

package com.xee.api;

import android.support.annotation.IntRange;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.UiThread;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.xee.BuildConfig;
import com.xee.api.entity.Car;
import com.xee.api.entity.Location;
import com.xee.api.entity.Signal;
import com.xee.api.entity.Stat;
import com.xee.api.entity.Status;
import com.xee.api.entity.Trip;
import com.xee.api.entity.User;
import com.xee.auth.AuthenticationActivity;
import com.xee.auth.ConnectionCallback;
import com.xee.auth.DisconnectCallback;
import com.xee.core.XeeEnv;
import com.xee.core.XeeRequest;
import com.xee.core.entity.Token;
import com.xee.internal.APIInterceptor;
import com.xee.internal.LogRequestInterceptor;
import com.xee.internal.RefreshTokenInterceptor;
import com.xee.internal.service.AuthService;
import com.xee.internal.service.CarsService;
import com.xee.internal.service.TripsService;
import com.xee.internal.service.UsersService;

import java.io.IOException;
import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Converter;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Xee API SDK
 */
public class Xee {

    public static final String ROUTE_BASE = "https://%s.xee.com/v3/";

    private static final String TAG = "Xee";
    private static final String DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";

    public static final Converter.Factory CONVERTER_FACTORY = GsonConverterFactory.create(new GsonBuilder().registerTypeAdapter(Date.class, new DateDeserializer()).create());
    public static final SimpleDateFormat DATE_FORMATTER = new SimpleDateFormat(DATE_FORMAT, Locale.US);
    private static final LogRequestInterceptor LOG_INTERCEPTOR = new LogRequestInterceptor();

    @SuppressWarnings("ThrowableInstanceNeverThrown")
    private static final IllegalStateException CONNECTION_EXCEPTION = new IllegalStateException("You must connect the user before anything");

    private XeeEnv xeeEnv;

    private AuthService authService;
    private UsersService usersService;
    private CarsService carsService;
    private TripsService tripsService;

    private static class DateDeserializer implements JsonSerializer<Date>, JsonDeserializer<Date> {

        @Override
        public synchronized JsonElement serialize(Date date, Type type, JsonSerializationContext jsonSerializationContext) {
            return new JsonPrimitive(DATE_FORMATTER.format(date));
        }

        @Override
        public synchronized Date deserialize(JsonElement element, Type arg1, JsonDeserializationContext arg2) throws JsonParseException {
            final String date = element.getAsString();
            SimpleDateFormat formatter = new SimpleDateFormat(DATE_FORMAT, Locale.US);
            formatter.setTimeZone(TimeZone.getTimeZone("UTC"));

            try {
                return formatter.parse(date);
            } catch (ParseException e) {
                return null;
            }
        }
    }

    /**
     * Creates a Xee instance
     *
     * @param environment the {@link XeeEnv}
     */
    public Xee(final XeeEnv environment) {
        this.xeeEnv = environment;

        initAuthServices(environment);
    }

    /**
     * Init the Auth services
     *
     * @param env the {@link XeeEnv}
     */
    private void initAuthServices(final XeeEnv env) {
        // Create the client for auth.
        // The specificity is the Basic Authorization header we have for OAuth
        // See https://github.com/xee-lab/xee-api-docs/tree/master/api/api/v3/auth
        OkHttpClient.Builder authClientBuilder = new OkHttpClient.Builder()
                .addInterceptor(new Interceptor() {
                    @Override
                    public okhttp3.Response intercept(Chain chain) throws IOException {
                        Request.Builder requestBuilder = chain
                                .request()
                                .newBuilder()
                                .header("Authorization", "Basic " +
                                        Base64.encodeToString(
                                                (env.client.clientId
                                                        + ":"
                                                        + env.client.clientSecret).getBytes()
                                                , Base64.NO_WRAP)
                                              .replace("\n", ""));
                        return chain.proceed(requestBuilder.build());
                    }
                })
                .addInterceptor(new APIInterceptor())
                .connectTimeout(env.connectTimeout, TimeUnit.MILLISECONDS)
                .readTimeout(env.readTimeout, TimeUnit.MILLISECONDS);

        // If the SDK is in debug mode, we log all the requests and the responses sent
        if (BuildConfig.DEBUG) {
            authClientBuilder.addInterceptor(LOG_INTERCEPTOR);
        }

        // Build the retrofit interface and the Auth service
        Retrofit authRetrofit = new Retrofit.Builder()
                .baseUrl(String.format(Locale.FRANCE, ROUTE_BASE, env.environment))
                .client(authClientBuilder.build())
                .addConverterFactory(CONVERTER_FACTORY)
                .build();
        authService = authRetrofit.create(AuthService.class);
    }

    /**
     * Init the API services
     */
    private void initApiServices() {
        // Retrieve old token from storage
        // Build the API client.
        // Specificity is the OAuth2 protection so we need to add the Authorization header with
        // the OAuth2 Bearer
        OkHttpClient.Builder apiClientBuilder = new OkHttpClient.Builder()
                .addInterceptor(new Interceptor() {
                    @Override
                    public Response intercept(Chain chain) throws IOException {
                        Token token = xeeEnv.tokenStorage.get();
                        // If the code reach this part, the token exists ! so assert it is not null
                        assert token != null;
                        Request.Builder requestBuilder = chain.request().newBuilder()
                                                              .header("Authorization", "Bearer " + token.getAccessToken());
                        return chain.proceed(requestBuilder.build());
                    }
                })
                .addInterceptor(new APIInterceptor())
                .addInterceptor(new RefreshTokenInterceptor(authService, xeeEnv.tokenStorage))
                .connectTimeout(xeeEnv.connectTimeout, TimeUnit.MILLISECONDS)
                .readTimeout(xeeEnv.readTimeout, TimeUnit.MILLISECONDS);

        // If the SDK is in debug mode, we log all the requests and the responses sent
        if (BuildConfig.DEBUG) {
            apiClientBuilder.addInterceptor(LOG_INTERCEPTOR);
        }

        // Build the retrofit interface and the API services
        Retrofit apiRetrofit = new Retrofit.Builder()
                .baseUrl(String.format(Locale.FRANCE, ROUTE_BASE, xeeEnv.environment))
                .client(apiClientBuilder.build())
                .addConverterFactory(CONVERTER_FACTORY)
                .build();


        usersService = apiRetrofit.create(UsersService.class);
        carsService = apiRetrofit.create(CarsService.class);
        tripsService = apiRetrofit.create(TripsService.class);
    }

    //region AUTH

    /**
     * Init the SDK and connects the user
     * <strong>Please note that the connection is stored, so next time you'll call {@link #connect(ConnectionCallback)} the user won't have to sign-in again</strong>
     *
     * @param connectionCallback the connection callback
     */

    @UiThread
    public void connect(@NonNull final ConnectionCallback connectionCallback) {
        // Get the storage util
        // Retrieve last token from storage
        Token currentToken = xeeEnv.tokenStorage.get();
        // Create a potential callback if we need to obtain of refresh the token
        final Callback<Token> tokenCallback = new Callback<Token>() {
            @Override
            public void onResponse(Call<Token> call, retrofit2.Response<Token> response) {
                xeeEnv.tokenStorage.store(response.body());
                initApiServices();
                connectionCallback.onSuccess();
            }

            @Override
            public void onFailure(Call<Token> call, Throwable t) {
                connectionCallback.onError(t);
            }
        };


        if (currentToken == null) {
            // Token does not exists

            // Generate callback for authentication Activity (might change one day)
            AuthenticationActivity.callback = new AuthenticationActivity.CodeCallback() {
                @Override
                public void onError(@NonNull Throwable error) {
                    connectionCallback.onError(error);
                }

                @Override
                public void onSuccess(@NonNull String code) {
                    authService
                            .obtainToken(AuthService.GRANT_TYPES.AUTHORIZATION_CODE, code)
                            .enqueue(tokenCallback);
                }
            };
            // Start the activity
            xeeEnv.context.startActivity(AuthenticationActivity.intent(xeeEnv.context, xeeEnv.client, xeeEnv.environment));

        } else if (currentToken.getExpiresAt() <= (Calendar.getInstance().getTimeInMillis() / 1000)) {
            // Token is outdated, refresh it
            authService
                    .refreshToken(AuthService.GRANT_TYPES.REFRESH_TOKEN, currentToken.getRefreshToken())
                    .enqueue(tokenCallback);
        } else {
            // Token is ok, then go out
            initApiServices();
            connectionCallback.onSuccess();
        }
    }

    /**
     * Disconnect the user
     *
     * @param disconnectCallback the disconnect callback
     */
    public void disconnect(@Nullable final DisconnectCallback disconnectCallback) {
        try {
            usersService = null;
            carsService = null;
            tripsService = null;
            xeeEnv.tokenStorage.dump();
            if (disconnectCallback != null) {
                disconnectCallback.onSuccess();
            }
        } catch (Exception e) {
            if (disconnectCallback != null) {
                disconnectCallback.onError(e);
            }
        }
    }

    /**
     * Disconnect the user
     */
    public void disconnect() {
        disconnect(null);
    }

    //endregion

    //region USER

    /**
     * Get the connected user
     *
     * @return a {@code XeeRequest<User>}
     * @see <a href="https://github.com/xee-lab/xee-api-docs/blob/master/api/api/v3/users/me.md" target="_blank">User API</a>
     */
    public XeeRequest<User> getUser() {
        if (usersService == null) {
            throw CONNECTION_EXCEPTION;
        }
        return new XeeRequest<>(usersService.getUser());
    }

    /**
     * Get all the cars from a user
     *
     * @return a {@code XeeRequest<List<Car>>}
     * @see <a href="https://github.com/xee-lab/xee-api-docs/blob/master/api/api/v3/users/cars.md" target="_blank">User cars API</a>
     */
    public XeeRequest<List<Car>> getCars() {
        if (usersService == null) {
            throw CONNECTION_EXCEPTION;
        }
        return new XeeRequest<>(usersService.getCars());
    }

    //endregion

    //region CAR

    /**
     * Get a specific car from its id
     *
     * @param carId the id of the car you are looking for
     * @return a {@code XeeRequest<Car>}
     * @see <a href="https://github.com/xee-lab/xee-api-docs/blob/master/api/api/v3/cars/car_id.md" target="_blank">Car API</a>
     */
    public XeeRequest<Car> getCar(@NonNull String carId) {
        if (carsService == null) {
            throw CONNECTION_EXCEPTION;
        }
        return new XeeRequest<>(carsService.getCar(carId));
    }

    /**
     * Get the current status of a specific car
     *
     * @param carId the id of the car you are looking for the status
     * @return a {@code XeeRequest<Status>}
     * @see <a href="https://github.com/xee-lab/xee-api-docs/blob/master/api/api/v3/cars/status.md" target="_blank">Car status API</a>
     */
    public XeeRequest<Status> getStatus(@NonNull String carId) {
        if (carsService == null) {
            throw CONNECTION_EXCEPTION;
        }
        return new XeeRequest<>(carsService.getStatus(carId));
    }

    /**
     * Get the locations history from a specific car in a specific interval with an optional limit
     *
     * @param carId the id of the car you are looking for the locations
     * @param begin the start of the interval when you want the locations
     * @param end   the end of the interval when you want the locations
     * @param limit the maximum number of locations you want in return (optional)
     * @return a {@code XeeRequest<List<Location>>}
     * @see <a href="https://github.com/xee-lab/xee-api-docs/blob/master/api/api/v3/cars/locations.md" target="_blank">Car locations API</a>
     */
    public XeeRequest<List<Location>> getLocations(@NonNull String carId, Date begin, Date end,
                                                   @IntRange(from = 1) @Nullable Integer limit) {
        if (carsService == null) {
            throw CONNECTION_EXCEPTION;
        }
        Map<String, String> options = new HashMap<>(3);
        if (begin != null) {
            options.put(CarsService.Parameters.BEGIN_DATE, DATE_FORMATTER.format(begin));
        }
        if (end != null) {
            options.put(CarsService.Parameters.END_DATE, DATE_FORMATTER.format(end));
        }
        if (limit != null) {
            if (limit > 0) {
                options.put(CarsService.Parameters.LIMIT, String.valueOf(limit));
            } else {
                Log.w(TAG, "Limit given but negative. Parameter ignored");
            }
        }
        return new XeeRequest<>(carsService.getLocations(carId, options));
    }

    /**
     * Get the locations history from a specific car from first day of month at 00:00:00 to current
     * moment with no limit
     *
     * @param carId the id of the car you are looking for the locations
     * @return a {@code XeeRequest<List<Location>>}
     * @see <a href="https://github.com/xee-lab/xee-api-docs/blob/master/api/api/v3/cars/locations.md" target="_blank">Car locations API</a>
     */
    public XeeRequest<List<Location>> getLocations(@NonNull String carId) {
        return getLocations(carId, null, null, null);
    }

    /**
     * Get all the specified signals (data) history from a specific car
     *
     * @param carId        the id of the car you are looking for the signals
     * @param begin        the start of the interval when you want the signals
     * @param end          the end of the interval when you want the signals
     * @param signalsNames the list of signals you want, for example, if you want LockSts and Odometer, you'll have name=LockSts,Odometer,
     *                     by default, all the signals available are sent back.
     *                     You can see the full list @see <a href="https://github.com/xee-lab/xee-api-docs/blob/master/api/api/v3/cars/signals_list.md">here</a>
     * @param limit        the maximum number of signals you want in return (optional)
     * @return a {@code XeeRequest<List<Signal>>}
     * @see <a href="https://dev.xee.com/signals" target="_blank">The full list of signals</a>
     * @see <a href="https://github.com/xee-lab/xee-api-docs/blob/master/api/api/v3/cars/signals.md" target="_blank">Car signals API</a>
     */
    public XeeRequest<List<Signal>> getSignals(@NonNull String carId, Date begin, Date end,
                                               List<Signal.Name> signalsNames,
                                               @IntRange(from = 1) @Nullable Integer limit) {
        if (carsService == null) {
            throw CONNECTION_EXCEPTION;
        }
        Map<String, String> options = new HashMap<>(4);
        if (begin != null) {
            options.put(CarsService.Parameters.BEGIN_DATE, DATE_FORMATTER.format(begin));
        }
        if (end != null) {
            options.put(CarsService.Parameters.END_DATE, DATE_FORMATTER.format(end));
        }
        if (signalsNames != null) {
            if (!signalsNames.isEmpty()) {
                List<String> signalsNameAsStrings = new ArrayList<>(signalsNames.size());
                for (Signal.Name name : signalsNames) {
                    signalsNameAsStrings.add(name.name);
                }
                options.put(CarsService.Parameters.SIGNAL_NAMES, TextUtils.join(",", signalsNameAsStrings));
            } else {
                Log.w(TAG, "Signals list given but empty. Parameter ignored");
            }
        }
        if (limit != null) {
            if (limit > 0) {
                options.put(CarsService.Parameters.LIMIT, String.valueOf(limit));
            } else {
                Log.w(TAG, "Limit given but negative. Parameter ignored");
            }
        }
        return new XeeRequest<>(carsService.getSignals(carId, options));
    }

    /**
     * Get all signals (data) history from a specific car from first day of month at 00:00:00 to current
     * moment with no limit
     *
     * @param carId the id of the car you are looking for the signals
     * @return a {@code XeeRequest<List<Signal>>}
     * @see <a href="https://dev.xee.com/signals" target="_blank">The full list of signals</a>
     * @see <a href="https://github.com/xee-lab/xee-api-docs/blob/master/api/api/v3/cars/signals.md" target="_blank">Car signals API</a>
     */
    public XeeRequest<List<Signal>> getSignals(@NonNull String carId) {
        return getSignals(carId, null, null, null, null);
    }

    /**
     * Get the trips history from a specific car within a specific interval
     *
     * @param carId the id of the car you are looking for the trips
     * @param begin the start of the interval when you want the trips
     * @param end   the end of the interval when you want the trips
     * @return a {@code XeeRequest<List<Trip>>}
     * @see <a href="https://github.com/xee-lab/xee-api-docs/blob/master/api/api/v3/cars/trips.md" target="_blank">Car trips API</a>
     */
    public XeeRequest<List<Trip>> getTrips(@NonNull String carId, Date begin, Date end) {
        if (carsService == null) {
            throw CONNECTION_EXCEPTION;
        }
        Map<String, String> options = new HashMap<>(2);
        if (begin != null) {
            options.put(CarsService.Parameters.BEGIN_DATE, DATE_FORMATTER.format(begin));
        }
        if (end != null) {
            options.put(CarsService.Parameters.END_DATE, DATE_FORMATTER.format(end));
        }

        return new XeeRequest<>(carsService.getTrips(carId, options));
    }

    /**
     * Get the trips history from a specific car from first day of month at 00:00:00 to current
     * moment
     *
     * @param carId the id of the car you are looking for the trips
     * @return a {@code XeeRequest<List<Trip>>}
     * @see <a href="https://github.com/xee-lab/xee-api-docs/blob/master/api/api/v3/cars/trips.md" target="_blank">Car trips API</a>
     */
    public XeeRequest<List<Trip>> getTrips(@NonNull String carId) {
        return getTrips(carId, null, null);
    }

    /**
     * Get the mileage value for a specific car
     *
     * @param carId        the id of the car you are looking for the mileage
     * @param begin        the start of the interval when you want the mileage to be computed
     * @param end          the end of the interval when you want the mileage to be computed
     * @param initialValue an offset to add to the used time stat (in seconds). Default value is 0
     * @return a {@code XeeRequest<Stat>}
     * @see <a href="https://github.com/xee-lab/xee-api-docs/blob/master/api/api/v3/cars/stats/mileage.md" target="_blank">Car mileage API</a>
     */
    public XeeRequest<Stat<Double>> getMileage(@NonNull String carId, Date begin, Date end, Double initialValue) {
        if (carsService == null) {
            throw CONNECTION_EXCEPTION;
        }
        Map<String, String> options = new HashMap<>(3);
        if (begin != null) {
            options.put(CarsService.Parameters.BEGIN_DATE, DATE_FORMATTER.format(begin));
        }
        if (end != null) {
            options.put(CarsService.Parameters.END_DATE, DATE_FORMATTER.format(end));
        }
        if (initialValue >= 0 && initialValue != null) {
            options.put(CarsService.Parameters.INITIAL_VALUE, Double.toString(initialValue));
        }
        return new XeeRequest<>(carsService.getMileage(carId, options));
    }

    /**
     * Get the mileage value for a specific car
     *
     * @param carId the id of the car you are looking for the mileage
     * @return a {@code XeeRequest<Stat>}
     * @see <a href="https://github.com/xee-lab/xee-api-docs/blob/master/api/api/v3/cars/stats/mileage.md" target="_blank">Car mileage API</a>
     */
    public XeeRequest<Stat<Double>> getMileage(@NonNull String carId) {
        return getMileage(carId, null, null, 0.0);
    }

    /**
     * Get the used time value for a specific car
     *
     * @param carId        the id of the car you are looking for the used time
     * @param begin        the start of the interval when you want the used time to be computed
     * @param end          the end of the interval when you want the used time to be computed
     * @param initialValue an offset to add to the used time stat (in seconds). Default value is 0
     * @return a {@code XeeRequest<Stat>}
     * @see <a href="https://github.com/xee-lab/xee-api-docs/blob/master/api/api/v3/cars/stats/usedtime.md" target="_blank">Car used time API</a>
     */
    public XeeRequest<Stat<Long>> getUsedTime(@NonNull String carId, Date begin, Date end, Long initialValue) {
        if (carsService == null) {
            throw CONNECTION_EXCEPTION;
        }
        Map<String, String> options = new HashMap<>(3);
        if (begin != null) {
            options.put(CarsService.Parameters.BEGIN_DATE, DATE_FORMATTER.format(begin));
        }
        if (end != null) {
            options.put(CarsService.Parameters.END_DATE, DATE_FORMATTER.format(end));
        }
        if (initialValue >= 0) {
            options.put(CarsService.Parameters.INITIAL_VALUE, Long.toString(initialValue));
        }
        return new XeeRequest<>(carsService.getUsedTime(carId, options));
    }

    /**
     * Get the used time value for a specific car
     *
     * @param carId the id of the car you are looking for the used time
     * @return a {@code XeeRequest<Stat>}
     * @see <a href="https://github.com/xee-lab/xee-api-docs/blob/master/api/api/v3/cars/stats/usedtime.md" target="_blank">Car used time API</a>
     */
    public XeeRequest<Stat<Long>> getUsedTime(@NonNull String carId) {
        return getUsedTime(carId, null, null, 0L);
    }

    //endregion

    //region TRIP

    /**
     * Get a specific trip
     *
     * @param tripId the id of the trip you are looking for
     * @return a {@code XeeRequest<Trip>}
     * @see <a href="https://github.com/xee-lab/xee-api-docs/blob/master/api/api/v3/trips/trip_id.md" target="_blank">Trip API</a>
     */
    public XeeRequest<Trip> getTrip(@NonNull String tripId) {
        if (tripsService == null) {
            throw CONNECTION_EXCEPTION;
        }
        return new XeeRequest<>(tripsService.getTrip(tripId));
    }

    /**
     * Get the locations history from a specific trip
     *
     * @param tripId the id of the trip you are looking for the locations
     * @return a {@code XeeRequest<List<Location>>}
     * @see <a href="https://github.com/xee-lab/xee-api-docs/blob/master/api/api/v3/trips/locations.md" target="_blank">Trip locations API</a>
     */
    public XeeRequest<List<Location>> getTripLocations(@NonNull String tripId) {
        if (tripsService == null) {
            throw CONNECTION_EXCEPTION;
        }
        return new XeeRequest<>(tripsService.getLocations(tripId));
    }

    /**
     * Get all signals history from a specific trip
     *
     * @param tripId       the id of the trip you are looking for the signals
     * @param signalsNames the list of signals you want,
     *                     by default, all the signals available are sent back. You can see the full list @see <a href="https://github.com/xee-lab/xee-api-docs/blob/master/api/api/v3/cars/signals_list.md">here</a>
     * @return a {@code XeeRequest<List<Signal>>}
     * @see <a href="https://github.com/xee-lab/xee-api-docs/blob/master/api/api/v3/trips/signals.md" target="_blank">Trip signals API</a>
     */
    public XeeRequest<List<Signal>> getTripSignals(@NonNull String
                                                           tripId, @Nullable List<Signal.Name> signalsNames) {
        if (tripsService == null) {
            throw CONNECTION_EXCEPTION;
        }
        Map<String, String> options = new HashMap<>(2);
        if (signalsNames != null) {
            if (!signalsNames.isEmpty()) {
                List<String> signalsNameAsStrings = new ArrayList<>(signalsNames.size());
                for (Signal.Name name : signalsNames) {
                    signalsNameAsStrings.add(name.name);
                }
                options.put(TripsService.Parameters.SIGNAL_NAMES, TextUtils.join(",", signalsNameAsStrings));
            } else {
                Log.w(TAG, "Signals list given but empty. Parameter ignored");
            }
        }
        return new XeeRequest<>(tripsService.getSignals(tripId, options));
    }

    /**
     * Get the statistics a specific trip
     *
     * @param tripId the id of the trip you are looking for the stats
     * @return a {@code XeeRequest<Stat>}
     * @see <a href="https://github.com/xee-lab/xee-api-docs/blob/master/api/api/v3/trips/trip_id/stats.md" target="_blank">Trip stats API</a>
     */
    public XeeRequest<List<Stat>> getTripStats(@NonNull String tripId) {
        if (tripsService == null) {
            throw CONNECTION_EXCEPTION;
        }
        return new XeeRequest<>(tripsService.getStats(tripId));
    }

    /**
     * Get the mileage value for a specific trip
     *
     * @param tripId the id of the trip you are looking for the mileage
     * @return a {@code XeeRequest<Stat>}
     * @see <a href="https://github.com/xee-lab/xee-api-docs/blob/master/api/api/v3/trips/trip_id/stats/mileage.md" target="_blank">Trip mileage API</a>
     */
    public XeeRequest<Stat<Double>> getTripMileage(@NonNull String tripId) {
        if (tripsService == null) {
            throw CONNECTION_EXCEPTION;
        }
        return new XeeRequest<>(tripsService.getMileage(tripId));
    }

    /**
     * Get the used time value for a specific trip
     *
     * @param tripId the id of the trip you are looking for the used time
     * @return a {@code XeeRequest<Stat>}
     * @see <a href="https://github.com/xee-lab/xee-api-docs/blob/master/api/api/v3/trips/trip_id/stats/usedtime.md" target="_blank">Trip used time API</a>
     */
    public XeeRequest<Stat<Long>> getTripUsedTime(@NonNull String tripId) {
        if (tripsService == null) {
            throw CONNECTION_EXCEPTION;
        }
        return new XeeRequest<>(tripsService.getUsedTime(tripId));
    }

    //endregion
}