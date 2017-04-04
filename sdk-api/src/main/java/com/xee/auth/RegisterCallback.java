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

import android.support.annotation.NonNull;

public interface RegisterCallback {

    /**
     * Triggered when user cancels the registration by pressing back key
     */
    void onCanceled();

    /**
     * Triggered when an error occurs in the registration process
     *
     * @param error the error that has occurred
     */
    void onError(@NonNull Throwable error);

    /**
     * Triggered when the registration process has been successful but user did not accept scopes
     *
     */
    void onRegisteredButAccessDenied();

    /**
     * Triggered when the registration process has been successful
     *
     * @param loggedIn true if user has been logged in after registered
     */
    void onRegistered(boolean loggedIn);
}
