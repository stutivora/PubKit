package com.pubkit.service;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.pubkit.PubKit;
import com.pubkit.PubKitListener;
import com.pubkit.network.PubKitNetwork;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;

/**
 * Base PubKit implementation
 * Created by puran on 3/22/15.
 */
public final class BasePubKit implements PubKit {

    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;

    private static final String PROPERTY_DEVICE_APP_ID = "deviceAppId";
    private static final String PROPERTY_USER_ID = "sourceUserId";
    private static final String PROPERTY_REGISTRATION_ID = "registrationId";
    private static final String PROPERTY_APP_VERSION = "appVersion";
    private static final String TAG = "PUBKIT";
    private static final String ANONYMOUS_USER = "anonymous";

    private GoogleCloudMessaging gcmClient;
    private String pubKitAppId;
    private String pubKitApiKey;
    private String pubKitAppSecret;
    private String senderId;
    private String userId;
    private Context context;
    private PubKitListener pubKitListener;

    private static BasePubKit INSTANCE;

    public static BasePubKit getPubKitClient() {
        return INSTANCE;
    }

    private BasePubKit() {
       throw new UnsupportedOperationException();
    }

    private BasePubKit(String appId, String apiKey, String appSecret) {
        this.pubKitAppId = appId;
        this.pubKitApiKey = apiKey;
        this.pubKitAppSecret = appSecret;
    }

    @Override
    public PubKit initPubKit(String applicationId, String applicationKey, String applicationSecret) {
        if (INSTANCE == null) {
            INSTANCE = new BasePubKit(applicationId, applicationKey, applicationSecret);
        }
        return INSTANCE;
    }

    @Override
    public String setupGcmPush(Context context, String userId, String senderId) {
        this.context = context;

        // Check device for Play Services APK. If check succeeds, proceed with GCM registration.
        if (checkPlayServices()) {
            this.senderId = senderId;
            this.userId = userId;
            if (this.userId == null || this.userId.isEmpty()) {
                this.userId = ANONYMOUS_USER;
            }

            String registrationId = getRegistrationId(context);
            String sourceUserId = getUserId();

            if (registrationId.isEmpty() && !userId.equalsIgnoreCase(sourceUserId)) {
                registerInBackground();
            }
        } else {
            Log.i(TAG, "No valid Google Play Services APK found.");
        }
        return null;
    }

    @Override
    public void setPubKitListener(PubKitListener listener) {
        this.pubKitListener = listener;
    }

    public PubKitListener getPubKitListener() {
        return this.pubKitListener;
    }

    @Override
    public boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(context);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, (android.app.Activity) context,
                        PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                Log.i(TAG, "This device is not supported.");
            }
            return false;
        }
        return true;
    }

    /**
     * Registers the application with GCM servers asynchronously. Stores the registration ID and
     * the app versionCode in the application's shared preferences.
     */
    private void registerInBackground() {

        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {
                String msg = "";
                try {
                    if (gcmClient == null) {
                        gcmClient = GoogleCloudMessaging.getInstance(context);
                    }
                    String registrationId = gcmClient.register(senderId);
                    msg = "Device registered, registration ID=" + registrationId;

                    // You should send the registration ID to your server over HTTP, so it
                    // can use GCM/HTTP or CCS to send messages to your app.
                    sendRegistrationIdToBackend(registrationId);
                } catch (IOException ex) {
                    msg = "Error :" + ex.getMessage();
                } catch (JSONException ex) {
                    msg = "Error :" + ex.getMessage();
                }
                return msg;
            }

            @Override
            protected void onPostExecute(String msg) {

            }
        }.execute(null, null, null);
    }

    private void sendRegistrationIdToBackend(String registrationId) throws JSONException {
        JSONObject registerObject = new JSONObject();

        registerObject.put("applicationId", this.pubKitAppId);
        registerObject.put("registrationId", registrationId);
        registerObject.put("sourceUserId", this.userId);
        registerObject.put("deviceType", "android");
        registerObject.put("deviceSubType", getDeviceName());

        JSONObject responseObject = PubKitNetwork.sendPost(this.pubKitApiKey, registerObject);
        if (responseObject != null) {
            if (responseObject.getString("error") == null) {
                boolean success = responseObject.getBoolean("success");
                if (success) {
                    String deviceAppId = responseObject.getString("deviceAppId");
                    if (deviceAppId != null) {
                        this.storeRegistrationId(deviceAppId, registrationId, this.userId);
                    }
                } else {
                    String errorMessage = responseObject.getString("errorResponse");
                    Log.i(TAG, errorMessage);
                }
            }
        }
    }

    /**
     * Stores the registration ID and the app versionCode in the application's
     * {@code SharedPreferences}.
     * @param deviceAppId the app device id
     * @param registrationId the registration id of device
     * @param userId the user id
     */
    private void storeRegistrationId(String deviceAppId, String registrationId, String userId) {
        final SharedPreferences prefs = getGcmPreferences();
        int appVersion = getAppVersion(context);
        Log.i(TAG, "Saving regId on app version " + appVersion);

        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(PROPERTY_DEVICE_APP_ID, deviceAppId);
        editor.putString(PROPERTY_REGISTRATION_ID, registrationId);
        editor.putString(PROPERTY_USER_ID, userId);
        editor.putInt(PROPERTY_APP_VERSION, appVersion);

        editor.commit();
    }

    private String getUserId() {
        final SharedPreferences prefs = getGcmPreferences();
        return prefs.getString(PROPERTY_USER_ID, "");
    }

    /**
     * Gets the current registration ID for application on GCM service, if there is one. If result
     * is empty, the app needs to register.
     *
     * @return registration ID, or empty string if there is no existing registration ID.
     */
    private String getRegistrationId(Context context) {
        final SharedPreferences prefs = getGcmPreferences();
        String registrationId = prefs.getString(PROPERTY_REGISTRATION_ID, "");
        if (registrationId.isEmpty()) {
            Log.i(TAG, "Registration not found.");
            registrationId = "";
        }
        // Check if app was updated; if so, it must clear the registration ID
        // since the existing regID is not guaranteed to work with the new
        // app version.
        int registeredVersion = prefs.getInt(PROPERTY_APP_VERSION, Integer.MIN_VALUE);
        int currentVersion = getAppVersion(context);
        if (registeredVersion != currentVersion) {
            Log.i(TAG, "App version changed.");
            registrationId = "";
        }
        return registrationId;
    }

    /**
     * @return Application's {@code SharedPreferences}.
     */
    private SharedPreferences getGcmPreferences() {
        return context.getSharedPreferences("PubKitAndroid", Context.MODE_PRIVATE);
    }

    /**
     * @return Application's version code from the {@code PackageManager}.
     */
    private static int getAppVersion(Context context) {
        try {
            PackageInfo packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            return packageInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            throw new RuntimeException("Could not get package name: " + e);
        }
    }

    /** Returns the consumer friendly device name */
    public String getDeviceName() {
        final String manufacturer = Build.MANUFACTURER;
        final String model = Build.MODEL;
        if (model.startsWith(manufacturer)) {
            return capitalize(model);
        }
        if (manufacturer.equalsIgnoreCase("HTC")) {
            return "HTC " + model;
        }
        return capitalize(manufacturer) + " " + model;
    }

    private String capitalize(String str) {
        if (TextUtils.isEmpty(str)) {
            return str;
        }
        final char[] arr = str.toCharArray();
        boolean capitalizeNext = true;
        String phrase = "";
        for (final char c : arr) {
            if (capitalizeNext && Character.isLetter(c)) {
                phrase += Character.toUpperCase(c);
                capitalizeNext = false;
                continue;
            } else if (Character.isWhitespace(c)) {
                capitalizeNext = true;
            }
            phrase += c;
        }
        return phrase;
    }
}
