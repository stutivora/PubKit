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
package com.pubkit.listener;
/**
 * Listener for receiving PubKit connection events. It's recommended to not perform any long running
 * operation on the listener event methods.
 *
 * Created by puran on 3/25/15.
 */
public interface PubKitListener {
    /**
     * Receive gcm push message
     * @param gcmMessage the message
     * @param messageType message type
     */
    void receivedGcmMessage(String gcmMessage, String messageType);

    /**
     * Called when the client is connected to messaging backend.
     * @param sessionId the current session id
     * @param accessToken the access token for the current session
     */
    void onConnect(String sessionId, String accessToken);

    /**
     * Called if client is disconnected.
     */
    void onDisConnect();

    /**
     * On any error, client or server.
     * @param errorCode the error code
     */
    void onError(String errorCode);
}
