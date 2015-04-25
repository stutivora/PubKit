package com.pubkit;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.pubkit.config.PubKitCredentials;
import com.pubkit.listener.PubKitListener;
import com.pubkit.listener.SubscriptionListener;
import com.pubkit.model.PKUser;
import com.pubkit.network.PubKitNetwork;
import com.pubkit.network.protocol.pkmp.Message;
import com.pubkit.network.protocol.pkmp.PKMPConnection;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Random;

import io.realm.Realm;
import io.realm.RealmQuery;
import io.realm.RealmResults;

/**
 * PubKit implementation for push and messaging.
 * Created by puran on 3/22/15.
 */
public final class PubKit implements PubKitApi {

    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    private static final String TAG = "PubKit";
    private static final String ANONYMOUS_USER = "anonymous";
    private static final String DEVICE_TYPE = "android";

    private static final String ALLOWED_CLIENT_ID_CHARACTERS = "0123456789qwertyuiopasdfghjklzxcvbnm";

    private static PubKit INSTANCE = null;

    /* GCM Messaging client */
    private GoogleCloudMessaging gcmClient;
    /* PKMP Connection */
    private PKMPConnection pkmpConnection;
    /* Context object passed via client application*/
    private Context context;
    /* PK user object */
    private PKUser pkUser;

    private PubKit() {
        throw new PubKitException("Not allowed, use getInstance(Context context) to get PubKit instance");
        //not allowed
    }

    private PubKit(Context context) {
        this.context = context;
    }

    public static PubKit getInstance(Context context) {
        if (INSTANCE == null) {
            INSTANCE = new PubKit(context);
        }
        return INSTANCE;
    }

    @Override
    public void setupPubKit(PubKitCredentials credentials) {
        if (credentials == null) {
            throw new PubKitException("Credentials cannot be null");
        }
        if (credentials.getPubKitAppId() == null || credentials.getPubKitApiKey() == null ||
                credentials.getPubKitApiSecret() == null) {
            throw new PubKitException("Invalid Credentials or null");
        }
        Realm realm = Realm.getInstance(this.context);

        RealmQuery<PKUser> query = realm.where(PKUser.class);
        RealmResults<PKUser> users = query.findAll();
        if (users == null || users.isEmpty()) {
            String clientId = generateClientId();

            realm.beginTransaction();

            PKUser user = realm.createObject(PKUser.class);

            user.setClientId(clientId);
            user.setSourceUserId(ANONYMOUS_USER);
            user.setApplicationId(credentials.getPubKitAppId());
            user.setApplicationKey(credentials.getPubKitApiKey());
            user.setApplicationSecret(credentials.getPubKitApiKey());
            user.setDeviceType(DEVICE_TYPE);
            user.setDeviceSubType(getDeviceName());
            user.setVersionCode(getAppVersion(context));

            //commit user
            realm.commitTransaction();

            this.pkUser = user;
        } else {
            this.pkUser = users.first();
        }
    }

    @Override
    public void setupGcmPush(String gcmSenderId) {
        String userId = ANONYMOUS_USER;
        // Check device for Play Services APK. If check succeeds, proceed with GCM registration.
        if (checkPlayServices()) {
            String registrationId = pkUser.getRegistrationId();
            if (registrationId.isEmpty()) {
                Log.i(TAG, "Registration not found.");
                registrationId = "";
            }

            // Check if app was updated; if so, it must clear the registration ID
            // since the existing regID is not guaranteed to work with the new
            // app version.
            int registeredVersion = pkUser.getVersionCode();
            int currentVersion = getAppVersion(context);
            if (registeredVersion != currentVersion) {
                Log.i(TAG, "App version changed.");
                registrationId = "";
            }
            if (registrationId.isEmpty()) {
                registerInBackground(gcmSenderId, userId);
            }
        } else {
            Log.i(TAG, "No valid Google Play Services APK found.");
        }
    }

    @Override
    public String getGcmRegistrationId() {
        if (pkUser != null) {
            return pkUser.getRegistrationId();
        } else {
            return null;
        }
    }

    @Override
    public boolean isAppUserLinkedToDevice() {
        return (this.pkUser != null && !ANONYMOUS_USER.equalsIgnoreCase(pkUser.getSourceUserId()));
    }

    @Override
    public void linkDeviceToAppUser(String userId) {
        registerInBackground(pkUser.getGcmSenderId(), userId);
    }

    @Override
    public boolean isConnected() {
        if (this.pkmpConnection == null) {
            return false;
        }
        return this.pkmpConnection.isConnected();
    }

    @Override
    public void connect(PubKitListener pubKitListener) {
        if (this.pkmpConnection == null) {
            this.pkmpConnection = new PKMPConnection(this.context, pubKitListener, pkUser);
        }
        this.pkmpConnection.connect();
    }

    @Override
    public void subscribe(String topic, SubscriptionListener subscriptionListener) {
        checkConnection();
        this.pkmpConnection.subscribe(topic, subscriptionListener);
    }

    @Override
    public void unSubscribe(String topic) {
        checkConnection();
        this.pkmpConnection.unSubscribe(topic);
    }

    @Override
    public void publish(String topic, Message message) {
        checkConnection();
        this.pkmpConnection.publish(topic, message);
    }

    @Override
    public void disconnect() {
        checkConnection();
        this.pkmpConnection.disconnect();
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

    private static String generateClientId() {
        final Random random = new Random();
        final StringBuilder sb = new StringBuilder(12);
        for (int i = 0; i < 12; ++i)
            sb.append(ALLOWED_CLIENT_ID_CHARACTERS.charAt(random.nextInt(ALLOWED_CLIENT_ID_CHARACTERS.length())));
        return sb.toString();
    }

    private void checkConnection() {
        if (this.pkmpConnection == null) {
            throw new PubKitException("Subscription failed. Please connect first");
        }
    }

    private boolean checkPlayServices() {
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
    private void registerInBackground(final String gcmSenderId, final String userId) {

        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {
                String msg = "";
                try {
                    if (!userId.equalsIgnoreCase(pkUser.getSourceUserId())) {
                        updateGcmInfoToBackend(pkUser.getRegistrationId(), gcmSenderId, userId);
                    } else {
                        if (gcmClient == null) {
                            gcmClient = GoogleCloudMessaging.getInstance(context);
                        }
                        String registrationId = gcmClient.register(gcmSenderId, userId);
                        msg = "REGISTRATION ID[ " + registrationId + " ]";

                        // You should send the registration ID to your server over HTTP, so it
                        // can use GCM/HTTP or CCS to send messages to your app.
                        updateGcmInfoToBackend(registrationId, gcmSenderId, userId);
                    }
                } catch (IOException ex) {
                    msg = "Error :" + ex.getMessage();
                    Log.e(TAG, "I/O Error registring device", ex);
                } catch (JSONException ex) {
                    Log.e(TAG, "JSON error registring device", ex);
                    msg = "Error :" + ex.getMessage();
                }
                return msg;
            }

            @Override
            protected void onPostExecute(String msg) {
                Log.i(TAG, "Device registration complete:-" + msg);
            }
        }.execute(null, null, null);
    }

    private void updateGcmInfoToBackend(String registrationId, String gcmSenderId, String userId) throws JSONException {
        JSONObject registerObject = new JSONObject();

        String deviceInfoId = pkUser.getDeviceInfoId();
        registerObject.put("deviceInfoId", deviceInfoId);
        registerObject.put("applicationId", this.pkUser.getApplicationId());
        registerObject.put("registrationId", registrationId);
        registerObject.put("sourceUserId", userId);
        registerObject.put("deviceType", DEVICE_TYPE);
        registerObject.put("deviceSubType", getDeviceName());

        JSONObject responseObject = PubKitNetwork.sendPost(this.pkUser.getApplicationKey(), registerObject);
        if (responseObject != null) {
            if (responseObject.getString("error") == null) {
                boolean success = responseObject.getBoolean("success");
                if (success) {
                    deviceInfoId = responseObject.getString("deviceInfoId");
                    if (deviceInfoId != null) {
                        Realm realm = Realm.getInstance(this.context);

                        realm.beginTransaction();
                        pkUser.setDeviceInfoId(deviceInfoId);
                        pkUser.setSourceUserId(userId);
                        pkUser.setGcmSenderId(gcmSenderId);
                        pkUser.setRegistrationId(registrationId);

                        //commit changes!
                        realm.commitTransaction();
                    }
                } else {
                    String errorMessage = responseObject.getString("errorResponse");
                    Log.i(TAG, errorMessage);
                }
            }
        }
    }

    /**
     * Returns the consumer friendly device name
     */
    private String getDeviceName() {
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
