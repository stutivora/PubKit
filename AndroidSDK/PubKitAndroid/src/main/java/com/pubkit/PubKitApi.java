/* Copyright (c) 2015 32skills Inc.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.pubkit;

import com.pubkit.config.PubKitCredentials;
import com.pubkit.listener.PubKitListener;
import com.pubkit.listener.SubscriptionListener;
import com.pubkit.network.Message;

/**
 * Base interface for PubKit SDK. It provides the APIs for publish/subscribe
 * messaging based on {@link \http://mqtt.org} MQTT and PKMP (PubKit Messaging Protocol). Also, it
 * allows APIs to register device and more for receiving GCM push notification.
 *
 * To use the SDK, necessary APP_ID and other credentials must be registered on PubKit backend.
 * Please refer to {@link \http://pubkit.co} for more detail.
 *
 * Created by puran on 4/24/15.
 */
public interface PubKitApi {

    /**
     * Setup PubKit client using the given credentials. For valid app credentials visit
     * {@link \http://pubkit.co}
     * @param credentials the credentials
     */
    void setupPubKit(PubKitCredentials credentials);

    /**
     * Register this device for receiving push notification.
     * @param gcmSenderId the gcm sender id.
     */
    void setupGcmPush(String gcmSenderId);

    /**
     * Returns the gcm registration id if the current device is registered with GCM server.
     * @return the gcm registration id
     */
    String getGcmRegistrationId();

    /**
     * Check if the client app's user is linked to the current device. By default device is registered
     * as anonymous. To send push notification to anonymous devices via PubKit's push console or REST api
     * registration id is needed.
     *
     * @return {@code true} if device is linked to source user {@code false} otherwise
     */
    boolean isAppUserLinkedToDevice();

    /**
     * Links the current device with the client app's user. <tt>userId</tt> must be unique within the
     * given app. Linking the device to a user will enable client app to send push notification via
     * PubKit's web console or using REST api based on userId.
     *
     * @param userId the user id
     */
    void linkDeviceToAppUser(String userId);

    /**
     * Check if the device is connected to PubKit messaging
     * @return {@code true} if the device is connected {@code false} otherwise
     */
    boolean isConnected();

    /**
     * Connect to PubKit messaging backend.
     * @param pubKitListener the connection listener
     */
    void connect(PubKitListener pubKitListener);

    /**
     * Subscribe to a topic. Topic is namespaced internally to a given application context.
     * @param topic the topic
     * @param subscriptionListener subscription listener for events
     */
    void subscribe(String topic, SubscriptionListener subscriptionListener);

    /**
     * Unsubscribe the device from the topic
     * @param topic the topic
     */
    void unSubscribe(String topic);

    /**
     * Publish a message to a topic.
     * @param topic the topic
     * @param message the message with actual data and type
     */
    void publish(String topic, Message message);

    /**
     * Disconnect the client
     */
    void disconnect();

}
