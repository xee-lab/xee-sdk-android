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

import android.os.Build;

import com.xee.core.entity.Error;

import org.hamcrest.CoreMatchers;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ErrorCollector;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;

import static org.hamcrest.CoreMatchers.is;

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, sdk = Build.VERSION_CODES.LOLLIPOP)
public class TestErrorUtils {

    @Rule
    public ErrorCollector collector = new ErrorCollector();

    @Test
    public void authenticationError() {
        String fakeError = "[\n" +
                "    {\n" +
                "        \"type\": \"AUTHENTICATION_ERROR\",\n" +
                "        \"message\": \"Fake error message\",\n" +
                "        \"tip\": \"Fake error type\"\n" +
                "    }\n" +
                "]";
        Error error = ErrorUtils.parseError(fakeError);
        collector.checkThat("error type", error.getType(), is("AUTHENTICATION_ERROR"));
        collector.checkThat("error message", error.getMessage(), is("Fake error message"));
        collector.checkThat("error tip", error.getTip(), is("Fake error type"));
    }

    @Test
    public void authorizationError() {
        String fakeError = "[\n" +
                "    {\n" +
                "        \"type\": \"AUTHORIZATION_ERROR\",\n" +
                "        \"message\": \"Fake error message\",\n" +
                "        \"tip\": \"Fake error type\"\n" +
                "    }\n" +
                "]";
        Error error = ErrorUtils.parseError(fakeError);
        collector.checkThat("error type", error.getType(), is("AUTHORIZATION_ERROR"));
        collector.checkThat("error message", error.getMessage(), is("Fake error message"));
        collector.checkThat("error tip", error.getTip(), is("Fake error type"));
    }

    @Test
    public void parametersError() {
        String fakeError = "[\n" +
                "    {\n" +
                "        \"type\": \"PARAMETERS_ERROR\",\n" +
                "        \"message\": \"Fake error message\",\n" +
                "        \"tip\": \"Fake error type\"\n" +
                "    }\n" +
                "]";
        Error error = ErrorUtils.parseError(fakeError);
        collector.checkThat("error type", error.getType(), is("PARAMETERS_ERROR"));
        collector.checkThat("error message", error.getMessage(), is("Fake error message"));
        collector.checkThat("error tip", error.getTip(), is("Fake error type"));
    }

    @Test
    public void apiCommonResponse() {
        String fakeError = "[\n" +
                "    {\n" +
                "        \"type\": \"PARAMETERS_ERROR\",\n" +
                "        \"message\": \"Fake error message\",\n" +
                "        \"tip\": \"Fake error type\"\n" +
                "    }\n" +
                "]";
        Error error = ErrorUtils.parseError(fakeError);
        collector.checkThat("error type", error.getType(), is("PARAMETERS_ERROR"));
        collector.checkThat("error message", error.getMessage(), is("Fake error message"));
        collector.checkThat("error tip", error.getTip(), is("Fake error type"));
    }

    @Test
    public void apiAuthResponse() {
        String fakeError = "{\n" +
                "  \"message\": \"Invalid authentication\"\n" +
                "}";
        Error error = ErrorUtils.parseError(fakeError);
        collector.checkThat("error message", error.getMessage(), is("Invalid authentication"));
        collector.checkThat("error message", error.getType(), CoreMatchers.nullValue());
    }
}