package com.pubkit;

import android.content.Context;
/**
 * Created by puran on 3/22/15.
 */
public interface PubKit {

    /**
     * Initializes the PubKit client. User {@link https://pubkit.co} to register the PubKit app
     * and get applicationId, applicationKey and applicationSecret.
     *
     * @param applicationId the PubKit applicationId
     * @param applicationKey the PubKit application key
     * @param applicationSecret the PubKit application secret
     *
     * @return PubKit client object to perform all messaging operations
     */
    PubKit initPubKit(String applicationId, String applicationKey, String applicationSecret);

    /**
     * Setup {@code PubKit} client for the given userId, senderId and gcm API KEY. For creating
     * senderId and API KEY, follow {@link http://developer.android.com/google/gcm/gs.html}
     * @param context the app context
     * @param userId the userId of the client app
     * @param senderId the sender id
     * @param gcmApiKey the GCM API KEY
     *
     * @return registrationId for the device
     */
    String setupClient(Context context, String userId, String senderId, String gcmApiKey);

    /**
     * Setup {@code PubKit} anonymous client for the given senderId and gcm API KEY. This is recommended
     * for apps that do not need to know user associated with a device registration Id. For creating
     * senderId and API KEY, follow {@link http://developer.android.com/google/gcm/gs.html}
     * @param context the app context
     * @param senderId the sender id
     * @param gcmApiKey the GCM API KEY
     *
     * @return registrationId for the device
     */
    String setupAnonymousClient(Context context, String senderId, String gcmApiKey);

    /**
     * Set the listener for receiving all the PubKit events and messages
     * @param listener the listener
     */
    void setPubKitListener(PubKitListener listener);

    /**
     * Check the device to make sure it has the Google Play Services APK. If
     * it doesn't, display a dialog that allows users to download the APK from
     * the Google Play Store or enable it in the device's system settings.
     */
    boolean checkPlayServices();
}
