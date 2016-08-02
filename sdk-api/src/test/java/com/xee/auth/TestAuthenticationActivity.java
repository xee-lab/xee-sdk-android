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

package com.xee.auth;

import android.app.Activity;
import android.os.Build;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;


import com.xee.BuildConfig;
import com.xee.api.Xee;
import com.xee.core.XeeEnv;
import com.xee.core.entity.OAuth2Client;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ErrorCollector;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.robolectric.Shadows.shadowOf;

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, sdk = Build.VERSION_CODES.LOLLIPOP)
public class TestAuthenticationActivity {

    @Rule
    public ErrorCollector collector = new ErrorCollector();

    private Xee xee;

    @Before
    public void setUp() {
        xee = new Xee(new XeeEnv(RuntimeEnvironment.application, new OAuth2Client("fake_client", "fake_secret", "fake_redirect")));
    }

    @Test
    public void connectWorkflow() {
        Activity activity = Robolectric.setupActivity(Activity.class);
        xee.connect(new ConnectionCallback() {
            @Override
            public void onError(@NonNull Throwable error) {
                collector.addError(error);
            }

            @Override
            public void onSuccess() {

            }
        });
        collector.checkThat("intent", shadowOf(activity).getNextStartedActivity(),
                is(AuthenticationActivity.intent(RuntimeEnvironment.application, new OAuth2Client("fake_client", "fake_secret", "fake_redirect"), "cloud")));
    }

    @Test
    public void authenticationWebsite() {
        Activity activity = Robolectric
                .buildActivity(AuthenticationActivity.class)
                .withIntent(AuthenticationActivity.intent(RuntimeEnvironment.application, new OAuth2Client("fake_client", "fake_secret", "fake_redirect"), "cloud"))
                .create()
                .get();

        View view = ((ViewGroup) activity.findViewById(android.R.id.content)).getChildAt(0);
        collector.checkThat("content is webview", view, is(instanceOf(WebView.class)));
    }
}