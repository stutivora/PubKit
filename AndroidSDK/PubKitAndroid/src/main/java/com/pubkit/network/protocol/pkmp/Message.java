package com.pubkit.network.protocol.pkmp;

/**
 * Created by puran on 4/16/15.
 */
public class Message {
    private String messageId;
    private String data;

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }
}
