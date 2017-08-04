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
import com.xee.internal.service.AuthService;

import java.util.Locale;

public class RegisterActivity extends Activity {

    public interface CodeCallback {

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
         * Triggered when the user did not accept scopes
         */
        void onAccessDenied();

        /**
         * Triggered when the authorization code is obtained
         *
         * @param code the authorization code to use to register
         */
        void onSuccess(@NonNull String code);
    }

    /**
     * The extra intent for client
     */
    private static final String EXTRA_CLIENT_ID = "extra_client_id";
    /**
     * The extra intent for host
     */
    private static final String EXTRA_HOST = "extra_host";
    /**
     * The callback for intercepting code when user try to connect
     */
    public static RegisterActivity.CodeCallback callback;

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
    private WebView registerWebView;

    /**
     * Register url (used for unit test too)
     */
    private String registrationUrl;

    /**
     * Special oauth WebView client, checks the urls in the WebView to find out when the access code is found.
     */
    private WebViewClient mmRegisterWebViewClient = new WebViewClient() {

        @Override
        @TargetApi(Build.VERSION_CODES.M)
        public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
            super.onReceivedError(view, request, error);
            callback.onError(new Exception("HTTP error received from the registration server {url : " + request.getUrl() + ", desc:" + error.getDescription() + "}"));
        }

        @Override
        @SuppressWarnings("deprecation")
        public void onReceivedError(final WebView view, final int errorCode, final String description, final String failingUrl) {
            callback.onError(new Exception("HTTP error received from the registration server {url : " + failingUrl + ", desc:" + description + "}"));
            RegisterActivity.this.finish();
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
            if(!realUrl.isHierarchical()) {
                if(url.startsWith("mailto:")){
                    Intent i = new Intent(Intent.ACTION_SENDTO, Uri.parse(url));
                    startActivity(i);
                    return true;
                }
            }

            if(url.endsWith(".pdf")) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, realUrl);
                startActivity(browserIntent);
                return true;
            }

            String errorCode = realUrl.getQueryParameter(ERROR_CODE);
            final String accessCode = realUrl.getQueryParameter(ACCESS_CODE);
            if (errorCode != null && !errorCode.isEmpty()) {
                if (errorCode.equals("access_denied")) {
                    callback.onAccessDenied();
                } else {
                    callback.onError(new Exception(errorCode));
                }
                RegisterActivity.this.finish();
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
     * The register activity intent to connect the user
     *
     * @param context  the context
     * @param clientId the client id
     * @param host     the host to connect to
     * @return the {@link Intent}
     */
    public static Intent intent(@NonNull Context context, @NonNull String clientId, @NonNull final String host) {
        Intent intent = new Intent(context, RegisterActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(EXTRA_CLIENT_ID, clientId);
        intent.putExtra(EXTRA_HOST, host);
        return intent;
    }

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final String clientId = getIntent().getStringExtra(EXTRA_CLIENT_ID);
        final String host = getIntent().getStringExtra(EXTRA_HOST);

        // Hide action bar if visible
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        ActionBar actionBar = getActionBar();
        if (actionBar != null && actionBar.isShowing()) {
            actionBar.hide();
        }

        // Create the WebView with the custom client
        registerWebView = new WebView(this);
        registerWebView.setWebViewClient(mmRegisterWebViewClient);
        registerWebView.getSettings().setJavaScriptEnabled(true);
        registerWebView.clearCache(true);
        disableRememberPasswordDialog(registerWebView);

        // Show the WebView on screen
        setContentView(registerWebView);

        // Build the url
        registrationUrl = String.format(Locale.FRANCE, Xee.ROUTE_BASE, host) + AuthService.Routes.REGISTER + "?" + AuthService.Parameters.CLIENT_ID + "=" + clientId;

        // Load the registration url
        registerWebView.loadUrl(registrationUrl);
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
        if (registerWebView.canGoBack()) {
            registerWebView.goBack();
        } else {
            callback.onCanceled();
            finish();
        }
    }
}