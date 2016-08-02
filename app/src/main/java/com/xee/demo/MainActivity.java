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

package com.xee.demo;

import android.app.AlertDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.google.gson.Gson;
import com.xee.api.Xee;
import com.xee.api.entity.Car;
import com.xee.api.entity.Location;
import com.xee.api.entity.Signal;
import com.xee.api.entity.Stat;
import com.xee.api.entity.Status;
import com.xee.api.entity.Trip;
import com.xee.api.entity.User;
import com.xee.auth.ConnectionCallback;
import com.xee.auth.DisconnectCallback;
import com.xee.core.XeeEnv;
import com.xee.core.XeeRequest;
import com.xee.core.entity.Error;
import com.xee.core.entity.OAuth2Client;
import com.xee.demo.adapters.WebServicesAdapter;
import com.xee.demo.events.WebServiceEvent;
import com.xee.demo.interfaces.CustomRecyclerViewItemTouch;
import com.xee.demo.interfaces.WebServices;
import com.xee.demo.ui.DividerRecyclerView;
import com.xee.demo.utils.JsonLogger;

import org.greenrobot.eventbus.EventBus;

import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Provides a "Demo" of using the Xee SDK
 */
public class MainActivity extends AppCompatActivity implements ConnectionCallback {

    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;

    interface PromptDialog {
        void onInput(final String id);
    }

    /**
     * The default timeout for connections
     */
    private static final int TIMEOUT = 60 * 1000;
    /**
     * The Xee environment
     */
    private static final String ENVIRONMENT = XeeEnv.CLOUD;
    /**
     * The web services adapter
     */
    private WebServicesAdapter webServicesAdapter;
    /**
     * The Xee Env
     */
    private XeeEnv xeeEnv;
    /**
     * The Xee API instance
     */
    private Xee xeeApi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        // build the web services list
        RecyclerView.LayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.addItemDecoration(new DividerRecyclerView(this, DividerRecyclerView.NORMAL));
        recyclerView.addOnItemTouchListener(new CustomRecyclerViewItemTouch(this, (view1, position) -> {
            callWS(webServicesAdapter.getItem(position));
        }));

        List<WebServices.WS> somethingList = Arrays.asList(WebServices.WS.values());
        webServicesAdapter = new WebServicesAdapter(somethingList);
        recyclerView.setAdapter(webServicesAdapter);

        // use a XeeAuth.Builder to help building a XeeAuth instance
        final OAuth2Client oAuth2Client = new OAuth2Client(getString(R.string.clientId), getString(R.string.clientSecret), getString(R.string.redirectUri));
        xeeEnv = new XeeEnv(getApplicationContext(), oAuth2Client, TIMEOUT, TIMEOUT, ENVIRONMENT);
        xeeApi = new Xee(xeeEnv);
    }

    @Override
    public void onError(@NonNull Throwable error) {
        showResult("obtainToken", error, false);
    }

    @Override
    public void onSuccess() {
        Snackbar.make(recyclerView, "Connection success", Snackbar.LENGTH_SHORT).show();
    }

    /**
     * Call Xee Web service
     *
     * @param ws the Web service to call
     */
    private void callWS(WebServices.WS ws) {
        switch (ws) {
            //region AUTH
            case CONNECT:
                xeeApi.connect(this);
                break;

            case DISCONNECT:
                xeeApi.disconnect(new DisconnectCallback() {
                    @Override
                    public void onError(@NonNull Throwable error) {
                        Snackbar.make(recyclerView, "Error on DISCONNECT!", Snackbar.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onSuccess() {
                        Snackbar.make(recyclerView, "Disconnected", Snackbar.LENGTH_SHORT).show();
                    }
                });
                break;

            //endregion

            //region USER
            case GET_USER:
                // synchronous request to get the user
                new GetUserTask().execute();
                break;

            case GET_USER_CARS:
                xeeApi.getCars()
                      .enqueue(new XeeRequest.Callback<List<Car>>() {
                          @Override
                          public void onSuccess(List<Car> response) {
                              showResult(ws.getName(), response, true);
                          }

                          @Override
                          public void onError(Error error) {
                              showResult(ws.getName(), error, false);
                          }
                      });
                break;
            //endregion

            //region CAR
            case GET_CAR:
                showPromptDialog(ws.getName(), new PromptDialog() {
                    @Override
                    public void onInput(String id) {
                        xeeApi.getCar(id)
                              .enqueue(new XeeRequest.Callback<Car>() {
                                  @Override
                                  public void onSuccess(Car response) {
                                      showResult(ws.getName(), response, true);
                                  }

                                  @Override
                                  public void onError(Error error) {
                                      showResult(ws.getName(), error, false);
                                  }
                              });
                    }
                });
                break;

            case GET_STATUS:
                showPromptDialog(ws.getName(), new PromptDialog() {
                    @Override
                    public void onInput(String id) {
                        xeeApi.getStatus(id)
                              .enqueue(new XeeRequest.Callback<Status>() {
                                  @Override
                                  public void onSuccess(Status response) {
                                      showResult(ws.getName(), response, true);
                                  }

                                  @Override
                                  public void onError(Error error) {
                                      showResult(ws.getName(), error, false);
                                  }
                              });
                    }
                });
                break;

            case GET_LOCATIONS:
                showPromptDialog(ws.getName(), new PromptDialog() {
                    @Override
                    public void onInput(String id) {
                        xeeApi.getLocations(id, null, null, 10)
                              .enqueue(new XeeRequest.Callback<List<Location>>() {
                                  @Override
                                  public void onSuccess(List<Location> response) {
                                      showResult(ws.getName(), response, true);
                                  }

                                  @Override
                                  public void onError(Error error) {
                                      showResult(ws.getName(), error, false);
                                  }
                              });
                    }
                });
                break;

            case GET_SIGNALS:
                showPromptDialog(ws.getName(), new PromptDialog() {
                    @Override
                    public void onInput(String id) {
                        xeeApi.getSignals(id, null, null, null, 10)
                              .enqueue(new XeeRequest.Callback<List<Signal>>() {
                                  @Override
                                  public void onSuccess(List<Signal> response) {
                                      showResult(ws.getName(), response, true);
                                  }

                                  @Override
                                  public void onError(Error error) {
                                      showResult(ws.getName(), error, false);
                                  }
                              });
                    }
                });
                break;

            case GET_TRIPS:
                showPromptDialog(ws.getName(), new PromptDialog() {
                    @Override
                    public void onInput(String id) {
                        xeeApi.getTrips(id, null, null)
                              .enqueue(new XeeRequest.Callback<List<Trip>>() {
                                  @Override
                                  public void onSuccess(List<Trip> response) {
                                      showResult(ws.getName(), response, true);
                                  }

                                  @Override
                                  public void onError(Error error) {
                                      showResult(ws.getName(), error, false);
                                  }
                              });
                    }
                });
                break;

            case GET_MILEAGE:
                showPromptDialog(ws.getName(), new PromptDialog() {
                    @Override
                    public void onInput(String id) {
                        xeeApi.getMileage(id, null, null, 123.456789)
                              .enqueue(new XeeRequest.Callback<Stat<Double>>() {
                                  @Override
                                  public void onSuccess(Stat<Double> response) {
                                      showResult(ws.getName(), response, true);
                                  }

                                  @Override
                                  public void onError(Error error) {
                                      showResult(ws.getName(), error, false);
                                  }
                              });
                    }
                });
                break;

            case GET_USED_TIME:
                showPromptDialog(ws.getName(), new PromptDialog() {
                    @Override
                    public void onInput(String id) {
                        xeeApi.getUsedTime(id)
                              .enqueue(new XeeRequest.Callback<Stat<Long>>() {
                                  @Override
                                  public void onSuccess(Stat<Long> response) {
                                      showResult(ws.getName(), response, true);
                                  }

                                  @Override
                                  public void onError(Error error) {
                                      showResult(ws.getName(), error, false);
                                  }
                              });
                    }
                });
                break;
            //endregion

            //region TRIPS
            case GET_TRIP:
                showPromptDialog(ws.getName(), new PromptDialog() {
                    @Override
                    public void onInput(String id) {
                        xeeApi.getTrip(id)
                              .enqueue(new XeeRequest.Callback<Trip>() {
                                  @Override
                                  public void onSuccess(Trip response) {
                                      showResult(ws.getName(), response, true);
                                  }

                                  @Override
                                  public void onError(Error error) {
                                      showResult(ws.getName(), error, false);
                                  }
                              });
                    }
                });
                break;

            case GET_TRIP_LOCATIONS:
                showPromptDialog(ws.getName(), new PromptDialog() {
                    @Override
                    public void onInput(String id) {
                        xeeApi.getTripLocations(id)
                              .enqueue(new XeeRequest.Callback<List<Location>>() {
                                  @Override
                                  public void onSuccess(List<Location> response) {
                                      showResult(ws.getName(), response, true);
                                  }

                                  @Override
                                  public void onError(Error error) {
                                      showResult(ws.getName(), error, false);
                                  }
                              });
                    }
                });
                break;


            case GET_TRIP_SIGNALS:
                showPromptDialog(ws.getName(), new PromptDialog() {
                    @Override
                    public void onInput(String id) {
                        xeeApi.getTripSignals(id, null)
                              .enqueue(new XeeRequest.Callback<List<Signal>>() {
                                  @Override
                                  public void onSuccess(List<Signal> response) {
                                      showResult(ws.getName(), response, true);
                                  }

                                  @Override
                                  public void onError(Error error) {
                                      showResult(ws.getName(), error, false);
                                  }
                              });
                    }
                });
                break;
            //endregion
        }
    }

    /**
     * Show the json response (one the right part)
     *
     * @param caller    the caller (web service name)
     * @param data      the data response
     * @param isSuccess true if response is successful, otherwise false
     */
    private void showResult(String caller, Object data, boolean isSuccess) {
        Gson gson = new Gson();
        String json = gson.toJson(data);
        final String prettyResult = JsonLogger.getPrettyLog(json);
        EventBus.getDefault()
                .post(new WebServiceEvent(caller, prettyResult, isSuccess));
    }

    /**
     * Show a prompt dialog to the user in order to enter an id to check
     *
     * @param callback the callback response when user has entered the id
     */
    private void showPromptDialog(@Nullable String title, @NonNull PromptDialog callback) {
        LayoutInflater layoutInflater = LayoutInflater.from(MainActivity.this);
        View promptView = layoutInflater.inflate(R.layout.dialog_prompt, null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MainActivity.this);
        alertDialogBuilder.setView(promptView);

        final TextView promptDialogTitle = (TextView) promptView.findViewById(R.id.dialog_prompt_title_text_view);
        promptDialogTitle.setText(title);
        final EditText promptDialogInput = (EditText) promptView.findViewById(R.id.dialog_prompt_edit_text);
        alertDialogBuilder.setCancelable(false)
                          .setPositiveButton("Valid", (dialog, id) -> callback.onInput(promptDialogInput.getText().toString()))
                          .setNegativeButton("Cancel", (dialog, id) -> dialog.cancel());

        AlertDialog alert = alertDialogBuilder.create();
        alert.setCancelable(true);
        alert.setCanceledOnTouchOutside(true);
        alert.show();
    }

    /**
     * Simple AsyncTask to get current user with a synchronous request
     */
    private class GetUserTask extends AsyncTask<Void, Void, XeeRequest.Response> {
        @Override
        protected XeeRequest.Response doInBackground(Void... voids) {
            XeeRequest<User> userXeeRequest = xeeApi.getUser();
            return userXeeRequest.execute();
        }

        @Override
        protected void onPostExecute(XeeRequest.Response response) {
            if (response.error != null) {
                showResult("getUser", response.error, false);
                return;
            }

            showResult("getUser", response, true);
        }
    }
}