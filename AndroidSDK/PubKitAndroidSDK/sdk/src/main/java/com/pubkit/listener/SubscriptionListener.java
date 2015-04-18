package com.pubkit.listener;

import com.pubkit.network.protocol.pkmp.Message;

/**
 * Created by puran on 4/10/15.
 */
public interface SubscriptionListener {

    void onSubscribe(String topic, String clientId);

    void onError(String error);

    void onMessage(String topic, Message message);

    void onMessageDelivered(String topic, String messageId);

    void onUnSubscribe(String topic);
}
