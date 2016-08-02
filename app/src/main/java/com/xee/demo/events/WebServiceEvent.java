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

package com.xee.demo.events;

/**
 * A simple event to show the web service response
 */
public class WebServiceEvent {
    /** The web service name called */
    private String caller;
    /** The web service result */
    private String result;
    /** True if web service response is successful, otherwise false */
    private boolean isSuccess;

    /**
     * Create a WebServiceEvent
     * @param caller the web service which is called
     * @param result the web service response
     * @param isSuccess true if web service response is successful, otherwise false
     */
    public WebServiceEvent(String caller, String result, boolean isSuccess) {
        this.caller = caller;
        this.result = result;
        this.isSuccess = isSuccess;
    }

    /**
     * @return the web service response
     */
    public String getResult() {
        return result;
    }

    /**
     * @return the name of web service
     */
    public String getCaller() {
        return caller;
    }

    /**
     * @return true if web service response is successful, otherwise false
     */
    public boolean isSuccess() {
        return isSuccess;
    }
}
