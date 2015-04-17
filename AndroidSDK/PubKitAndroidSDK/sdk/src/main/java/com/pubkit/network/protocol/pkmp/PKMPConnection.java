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

    public void unsubscribe(String topic) {
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

    }

    private void sendPayload(PKMPPayload payload) {
        String jsonPayload = gson.toJson(payload);
        this.webSocket.send(jsonPayload);
    }

    private PKMPPayload basePayload(String topic) {
        PKMPPayload basePayload = new PKMPPayload();
        basePayload.addHeader(PKMPPayload.TOPIC, topic);
        basePayload.addHeader(PKMPPayload.CLIENT_ID, pkUser.getClientId());
        basePayload.addHeader(PKMPPayload.SESSION_TOKEN, pkUser.getAccessToken());

        return basePayload;
    }

    @Override
    public void onCompleted(Exception ex, WebSocket webSocket) {
        if (ex != null) {
            Log.e(TAG, "Websocket connection error", ex);
            pubKitListener.onError(ex.getMessage());
            return;
        }
        this.webSocket = webSocket;
        this.webSocket.setStringCallback(this);

        //now do PKMP connect
        this.connect();
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
        Log.i(TAG, "Handle PKMP data of type "+type);
        switch (type) {
            case PKMPPayload.CONNACK:
                handleConnAck(pkmpPayload);
                break;
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
            case PKMPPayload.DISCONNECT:
                handleDisconnect(pkmpPayload);
                break;
            default:
                break;
        }
    }

    private void handleConnAck(PKMPPayload pkmpPayload) {
        String status = pkmpPayload.getStatus();
        if (STATUS_OK.equalsIgnoreCase(status)) {
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

            this.pubKitListener.onConnect(sessionId, accessToken);
        } else {
            this.pubKitListener.onError(pkmpPayload.getStatus());
        }
    }

    private void handleSubsAck(PKMPPayload pkmpPayload) {
        String status = pkmpPayload.getStatus();
        if (STATUS_OK.equalsIgnoreCase(status)) {
            String topic = pkmpPayload.getHeader(PKMPPayload.TOPIC);
            SubscriptionListener subscriptionListener = subscriptionListenerMap.get(topic);
            if (subscriptionListener != null) {
                subscriptionListener.onSubscribe(topic, pkmpPayload.getClientId());
            }
        } else {
            handleSubscribeError(pkmpPayload.getStatus(), pkmpPayload.getHeader(PKMPPayload.TOPIC));
        }
    }

    private void handleSubscribeError(String error, String topic) {
        Log.e(TAG, "Error subscribing to topic:"+topic+" with error "+error);
        if (PKMPPayload.INVALID_TOKEN.equalsIgnoreCase(error) ||
                PKMPPayload.CONNECTION_ERROR.equalsIgnoreCase(error)) {
            //TODO: Handle reconnect and try again?
        }
        SubscriptionListener subscriptionListener = subscriptionListenerMap.get(topic);
        if (subscriptionListener != null) {
            subscriptionListener.onError(error);
        }
    }

    private void handleUnSubsAck(PKMPPayload pkmpPayload) {

    }

    private void handlePubAck(PKMPPayload pkmpPayload) {

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

    private void handleDisconnect(PKMPPayload pkmpPayload) {

    }
}