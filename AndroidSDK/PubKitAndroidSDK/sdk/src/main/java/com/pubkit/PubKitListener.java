package com.pubkit;

/**
 * Created by puran on 3/25/15.
 */
public interface PubKitListener {

    void receivedGcmMessage(String gcmMessage, String messageType);
}
