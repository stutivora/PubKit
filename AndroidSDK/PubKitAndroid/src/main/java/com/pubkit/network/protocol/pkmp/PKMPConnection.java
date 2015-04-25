package com.pubkit.network.protocol.pkmp;

import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;
import com.koushikdutta.async.http.AsyncHttpClient;
import com.koushikdutta.async.http.WebSocket;
import com.pubkit.listener.PubKitListener;
import com.pubkit.listener.SubscriptionListener;
import com.pubkit.model.PKUser;
import java.util.HashMap;
import java.util.Map;
import io.realm.Realm;

/**
 * Created by puran on 4/15/15.
 */
public class PKMPConnection implements AsyncHttpClient.WebSocketConnectCallback, WebSocket.StringCallback {
    private static final String TAG = "PubKit";

    private static final String STATUS_OK = "OK";

    private Context context;

    private WebSocket webSocket;

    private PubKitListener pubKitListener;

    private PKUser pkUser;

    private Map<String, SubscriptionListener> subscriptionListenerMap;

    /* Pending payload after first failed attempt */
    private PKMPPayload pendingPayload;

    private int retryCount = 0;

    private String PKMP_URI = "ws://pubkit.co/pkmp";

    private String PROTOCOL = "pkmp";

    private Gson gson = new Gson();

    private PKMPConnection() {
        //not allowed!
        throw new UnsupportedOperationException("Not supported. Need listener");
    }

    public PKMPConnection(Context context, PubKitListener pubKitListener, PKUser pkUser) {
        this.context = context;
        this.pubKitListener = pubKitListener;
        this.pkUser = pkUser;
        this.subscriptionListenerMap = new HashMap<>();
    }

    private void ping() {
    }

    public void connect() {
        if (!isConnected()) {
            AsyncHttpClient.getDefaultInstance().websocket(PKMP_URI, PROTOCOL, this);
        }
    }

    public void pkmpConnect() {
        PKMPPayload connect = new PKMPPayload();
        connect.addHeader(PKMPPayload.APP_ID, pkUser.getApplicationId());
        connect.addHeader(PKMPPayload.API_KEY, pkUser.getApplicationKey());
        connect.addHeader(PKMPPayload.API_VERSION, "1.0");
        connect.addHeader(PKMPPayload.CLIENT_ID, pkUser.getClientId());
        connect.addHeader(PKMPPayload.SOURCE_USER_ID, pkUser.getSourceUserId());
        connect.addHeader(PKMPPayload.TYPE, PKMPPayload.CONNECT);

        sendPayload(connect);
    }

    public void subscribe(String topic, SubscriptionListener subscriptionListener) {
        this.subscriptionListenerMap.put(topic, subscriptionListener);
        PKMPPayload subscribe = basePayload(topic);
        subscribe.addHeader(PKMPPayload.TYPE, PKMPPayload.SUBSCRIBE);

        sendPayload(subscribe);
    }

    public void unSubscribe(String topic) {
        PKMPPayload unsubscribe = basePayload(topic);
        unsubscribe.addHeader(PKMPPayload.TYPE, PKMPPayload.UNSUBSCRIBE);

        sendPayload(unsubscribe);
    }

    public void publish(String topic, Message message) {
        PKMPPayload publish = basePayload(topic);
        publish.addHeader(PKMPPayload.MESSAGE_ID, message.getMessageId());
        publish.setData(message.getData());

        sendPayload(publish);
    }

    public void disconnect() {
        PKMPPayload disconnect = basePayload(null);
        disconnect.addHeader(PKMPPayload.TYPE, PKMPPayload.DISCONNECT);

        sendPayload(disconnect);
    }

    public boolean isConnected() {
        return (this.webSocket != null && this.webSocket.isOpen()
                && this.pkUser != null
                && this.pkUser.getAccessToken() != null);
    }

    private void sendPayload(PKMPPayload payload) {
        if (!PKMPPayload.CONNECT.equalsIgnoreCase(payload.getType())) {
            this.pendingPayload = payload;
        }
        String jsonPayload = gson.toJson(payload);
        if (this.webSocket.isOpen()) {
            this.webSocket.send(jsonPayload);
        } else {
            connect();
        }
    }

    private PKMPPayload basePayload(String topic) {
        PKMPPayload basePayload = new PKMPPayload();
        if (topic != null) {
            basePayload.addHeader(PKMPPayload.TOPIC, topic);
        }
        basePayload.addHeader(PKMPPayload.CLIENT_ID, pkUser.getClientId());
        basePayload.addHeader(PKMPPayload.SESSION_TOKEN, pkUser.getAccessToken());

        return basePayload;
    }

    @Override
    public void onCompleted(Exception ex, WebSocket webSocket) {
        if (ex != null) {
            Log.e(TAG, "Websocket connection error", ex);
            pubKitListener.onError(ex.getMessage());
            handleDisconnect(this.pkUser.getClientId());

            return;
        }
        if (webSocket.isOpen()) {
            this.webSocket = webSocket;
            this.webSocket.setStringCallback(this);

            //now do PKMP connect
            this.pkmpConnect();
        }
    }

    @Override
    public void onStringAvailable(String data) {
        if (data == null) {
            this.pubKitListener.onError("Null data received");
            return;
        }
        PKMPPayload pkmpPayload = gson.fromJson(data, PKMPPayload.class);
        if (pkmpPayload == null) {
            this.pubKitListener.onError("Bad payload received");
            return;
        }

        String type = pkmpPayload.getType();
        Log.i(TAG, "Handle PKMP data of type " + type);

        if (PKMPPayload.CONNACK.equalsIgnoreCase(type) || PKMPPayload.DISCONNECT.equalsIgnoreCase(type)) {
            handleConnectionEvent(pkmpPayload);
        } else {
            handleSubscriptionEvent(pkmpPayload);
        }
    }

    private void handleConnectionEvent(PKMPPayload pkmpPayload) {
        String type = pkmpPayload.getType();
        String status = pkmpPayload.getStatus();
        if (STATUS_OK.equalsIgnoreCase(status)) {
            switch (type) {
                case PKMPPayload.CONNACK:
                    handleConnAck(pkmpPayload);
                    break;
                case PKMPPayload.DISCONNECT:
                    handleDisconnect(pkmpPayload.getClientId());
                    break;
                default:
                    break;
            }
        } else {
            this.pubKitListener.onError(pkmpPayload.getStatus());
        }
    }

    private void handleSubscriptionEvent(PKMPPayload pkmpPayload) {
        String status = pkmpPayload.getStatus();
        String topic = pkmpPayload.getHeader(PKMPPayload.TOPIC);
        String type = pkmpPayload.getType();
        if (STATUS_OK.equalsIgnoreCase(status)) {
            switch (type) {
                case PKMPPayload.SUBSACK:
                    handleSubsAck(pkmpPayload);
                    break;
                case PKMPPayload.UNSUBSACK:
                    handleUnSubsAck(pkmpPayload);
                    break;
                case PKMPPayload.PUBACK:
                    handlePubAck(pkmpPayload);
                    break;
                case PKMPPayload.MESSAGE:
                    handleMessage(pkmpPayload);
                    break;
                default:
                    break;
            }
        } else {
            Log.e(TAG, "Error subscribing to topic:" + topic + " with error " + status);
            handleSubscriptionError(status, topic);
        }
    }

    private void handleConnAck(PKMPPayload pkmpPayload) {
        String sessionId = pkmpPayload.getSessionId();
        if (sessionId == null) {
            this.pubKitListener.onError("Invalid session id.");
            return;
        }
        String accessToken = pkmpPayload.getSessionToken();
        if (accessToken == null) {
            this.pubKitListener.onError("Invalid session token");
            return;
        }
        //persist the changes!
        Realm realm = Realm.getInstance(this.context);
        realm.beginTransaction();
        this.pkUser.setSessionId(sessionId);
        this.pkUser.setAccessToken(accessToken);
        realm.commitTransaction();

        if (this.pendingPayload != null) {
            handlePendingPayload();
        } else {
            this.pubKitListener.onConnect(sessionId, accessToken);
        }
    }

    private void handleSubsAck(PKMPPayload pkmpPayload) {
        String topic = pkmpPayload.getHeader(PKMPPayload.TOPIC);
        SubscriptionListener subscriptionListener = subscriptionListenerMap.get(topic);
        if (subscriptionListener != null) {
            subscriptionListener.onSubscribe(topic, pkmpPayload.getClientId());
        }
    }

    private void handleUnSubsAck(PKMPPayload pkmpPayload) {
        String topic = pkmpPayload.getHeader(PKMPPayload.TOPIC);
        SubscriptionListener subscriptionListener = subscriptionListenerMap.get(topic);
        if (subscriptionListener != null) {
            subscriptionListener.onUnSubscribe(topic);
        }
    }

    private void handlePubAck(PKMPPayload pkmpPayload) {
        String topic = pkmpPayload.getHeader(PKMPPayload.TOPIC);
        SubscriptionListener subscriptionListener = subscriptionListenerMap.get(topic);
        if (subscriptionListener != null) {
            String messageId = pkmpPayload.getHeader(PKMPPayload.MESSAGE_ID);
            subscriptionListener.onMessageDelivered(topic, messageId);
        }
    }

    private void handleMessage(PKMPPayload pkmpPayload) {
        String topic = pkmpPayload.getHeader(PKMPPayload.TOPIC);
        SubscriptionListener subscriptionListener = subscriptionListenerMap.get(topic);
        if (subscriptionListener != null) {
            Message message = new Message();
            message.setMessageId(pkmpPayload.getHeader(PKMPPayload.MESSAGE_ID));
            message.setData(pkmpPayload.getData());

            subscriptionListener.onMessage(topic, message);
        }
    }

    private void handleDisconnect(String clientId) {
        Log.i(TAG, "PubKit client disconnected " + clientId);
        //persist the changes!
        Realm realm = Realm.getInstance(this.context);

        realm.beginTransaction();
        this.pkUser.setSessionId(null);
        this.pkUser.setAccessToken(null);
        realm.commitTransaction();

        this.pubKitListener.onDisConnect();
    }

    private void handleSubscriptionError(String error, String topic) {
        if (PKMPPayload.INVALID_TOKEN.equalsIgnoreCase(error)) {
            //connect
            connect();
        }
        SubscriptionListener subscriptionListener = subscriptionListenerMap.get(topic);
        if (subscriptionListener != null) {
            subscriptionListener.onError(error);
        }
    }

    private void handlePendingPayload() {
        if (retryCount > 2) {
            Log.i(TAG, "Operation "+this.pendingPayload.getType() + " failed even after retries");
            retryCount = 0;
            this.pendingPayload = null;
        } else {
            Log.i(TAG, "Retrying "+this.pendingPayload.getType());
            retryCount++;
            sendPayload(this.pendingPayload);
        }
    }
}