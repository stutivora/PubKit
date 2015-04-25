package com.pubkit.listener;

/**
 * Created by puran on 3/25/15.
 */
public interface PubKitListener {

    void receivedGcmMessage(String gcmMessage, String messageType);

    void onConnect(String sessionId, String accessToken);

    void onDisConnect();

    void onError(String error);
}
