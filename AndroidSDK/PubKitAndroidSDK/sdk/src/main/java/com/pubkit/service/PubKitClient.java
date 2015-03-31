package com.pubkit.service;

import android.content.Context;

import com.pubkit.PubKitListener;

/**
 * Created by puran on 3/28/15.
 */
public interface PubKitClient {
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

    /**
     * Returns the PubKit listener
     * @return the listener
     */
    PubKitListener getPubKitListener();
}
