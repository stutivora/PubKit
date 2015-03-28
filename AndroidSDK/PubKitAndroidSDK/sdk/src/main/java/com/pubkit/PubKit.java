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
     * Setup {@code PubKit} anonymous client for the given senderId and gcm API KEY. This is recommended
     * for apps that do not need to know user associated with a device registration Id. For creating
     * senderId follow {@link http://developer.android.com/google/gcm/gs.html}
     * @param context the app context
     * @param userId the user id of the application. Use "null" if it's not associated with any app user
     * @param senderId the sender Id
     * @return registrationId for the device
     */
    String setupGcmPush(Context context, String userId, String senderId);

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
