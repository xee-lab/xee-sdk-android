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

package com.xee.demo.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.xee.demo.R;
import com.xee.demo.events.WebServiceEvent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * The fragment holding the Web services results
 */
public class WebServicesResultFragment extends Fragment {

    @BindView(R.id.webservice_call) EditText vWebServiceCall;
    @BindView(R.id.webservice_result) EditText vWebServiceResult;

    public WebServicesResultFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_web_services_result, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Subscribe
    public void onEvent(WebServiceEvent event) {
        if(event.isSuccess()) {
            vWebServiceResult.setTextColor(ContextCompat.getColor(getActivity(),  R.color.webservice_success));
        } else {
            vWebServiceResult.setTextColor(ContextCompat.getColor(getActivity(),  R.color.webservice_error));
        }

        vWebServiceCall.setText(event.getCaller());
        vWebServiceResult.setText(event.getResult());
    }
}