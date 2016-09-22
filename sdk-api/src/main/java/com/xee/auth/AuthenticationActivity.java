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

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.Window;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.xee.api.Xee;
import com.xee.core.entity.OAuth2Client;
import com.xee.internal.service.AuthService;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Locale;

/**
 * Xee Authentication SDK
 */
public final class AuthenticationActivity extends Activity {

    public interface CodeCallback {
        /**
         * Triggered when an error occurs in the authentication process
         *
         * @param error the error that has occurred
         */
        void onError(@NonNull Throwable error);

        /**
         * Triggered when the authorization code is obtained
         */
        void onSuccess(@NonNull String code);
    }

    /**
     * The extra intent for client
     */
    private static final String EXTRA_CLIENT = "extra_client";
    /**
     * The extra intent for host
     */
    private static final String EXTRA_HOST = "extra_host";
    /**
     * The callback for intercepting code when user try to connect
     */
    public static CodeCallback callback;

    @SuppressWarnings("ThrowableInstanceNeverThrown")
    public static final Throwable
            BACK_PRESSED_THROWABLE = new Throwable("User has pressed back button"),
            ACCESS_DENIED_THROWABLE = new Throwable("Access denied to Authentication");

    /**
     * Access denied urn
     */
    private static final String ERROR_CODE = "error";
    /**
     * Code parameter in url
     */
    private static final String ACCESS_CODE = "code";

    /**
     * THe webview used to connect user
     */
    private WebView authenticationWebView;

    /**
     * Authentication url (used for unit test too)
     */
    private String authenticationUrl;

    /**
     * Special oauth WebView client, checks the urls in the WebView to find out when the access code is found.
     */
    private WebViewClient mmAuthenticationWebViewClient = new WebViewClient() {

        @Override
        @TargetApi(Build.VERSION_CODES.M)
        public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
            super.onReceivedError(view, request, error);
            callback.onError(new Exception("HTTP error received from the authentication server {url : " + request.getUrl() + ", desc:" + error.getDescription() + "}"));
        }

        @Override
        @SuppressWarnings("deprecation")
        public void onReceivedError(final WebView view, final int errorCode, final String description, final String failingUrl) {
            callback.onError(new Exception("HTTP error received from the authentication server {url : " + failingUrl + ", desc:" + description + "}"));
            AuthenticationActivity.this.finish();
        }

        @Override
        @SuppressWarnings("deprecation")
        public boolean shouldOverrideUrlLoading(final WebView view, final String url) {
            return doesUrlContainsCode(view, url);
        }

        @Override
        @TargetApi(Build.VERSION_CODES.N)
        public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
            return doesUrlContainsCode(view, request.getUrl().toString());
        }

        /**
         * Check if url contains "code" field
         * @param view the webview holding the url
         * @param url the url to check
         * @return true if url contains "code" field, otherwise false
         */
        private boolean doesUrlContainsCode(WebView view, String url) {
            // Check if there is an access denied error
            Uri realUrl = Uri.parse(url);
            String errorCode = realUrl.getQueryParameter(ERROR_CODE);
            final String accessCode = realUrl.getQueryParameter(ACCESS_CODE);
            if (errorCode != null && !errorCode.isEmpty()) {
                if (errorCode.equals("access_denied")) {
                    callback.onError(ACCESS_DENIED_THROWABLE);
                } else {
                    callback.onError(new Exception(errorCode));
                }
                AuthenticationActivity.this.finish();
                // Cancel loading
                return true;
            }
            // Check if there is the access code in the url
            else if (accessCode != null && !accessCode.isEmpty()) {
                // clear the cache
                view.clearCache(true);
                // Retrieve the access code
                // Get the access token from the access code
                callback.onSuccess(accessCode);
                // Close the activity when we got the access code
                finish();
                // Cancel loading
                return true;
            }
            // Load the page
            return false;
        }
    };

    /**
     * The authentication activity intent to connect the user
     *
     * @param context the context
     * @param client  the {@link OAuth2Client}
     * @param host    the host to connect to
     * @return the {@link Intent}
     */
    public static Intent intent(@NonNull Context context, @NonNull OAuth2Client client, @NonNull final String host) {
        Intent intent = new Intent(context, AuthenticationActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(EXTRA_CLIENT, client);
        intent.putExtra(EXTRA_HOST, host);
        return intent;
    }

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        OAuth2Client client = getIntent().getParcelableExtra(EXTRA_CLIENT);
        final String host = getIntent().getStringExtra(EXTRA_HOST);

        // Hide action bar if visible
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        ActionBar actionBar = getActionBar();
        if (actionBar != null && actionBar.isShowing()) {
            actionBar.hide();
        }

        // Create the WebView with the custom client
        authenticationWebView = new WebView(this);
        authenticationWebView.setWebViewClient(mmAuthenticationWebViewClient);
        authenticationWebView.clearCache(true);
        disableRememberPasswordDialog(authenticationWebView);

        // Show the WebView on screen
        setContentView(authenticationWebView);

        // Load the authentication URL
        String callbackURI;
        try {
            callbackURI = URLEncoder.encode(client.redirectUri, "utf-8");
        } catch (UnsupportedEncodingException e) {
            // Failed to encode the callback uri
            callback.onError(e);
            finish();
            return;
        }

        // Build the url
        authenticationUrl = String.format(Locale.FRANCE, Xee.ROUTE_BASE, host) + AuthService.Routes.AUTHENTICATION + "?" + AuthService.Parameters.CLIENT_ID + "=" + client.clientId + "&" + AuthService.Parameters.REDIRECT_URL + "=" + callbackURI;

        // Load the authentication url
        authenticationWebView.loadUrl(authenticationUrl);
    }

    /**
     * Disable the "remember password dialog", only useful on KITKAT or JELLY BEAN
     *
     * @param webView the WebView we want to disable the dialog
     */
    @SuppressLint("Deprecation")
    private void disableRememberPasswordDialog(WebView webView) {
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            webView.getSettings()
                   .setSavePassword(false);
        }
    }

    @Override
    public void onBackPressed() {
        if (authenticationWebView.canGoBack()) {
            authenticationWebView.goBack();
        } else {
            callback.onError(BACK_PRESSED_THROWABLE);
            finish();
        }
    }
}