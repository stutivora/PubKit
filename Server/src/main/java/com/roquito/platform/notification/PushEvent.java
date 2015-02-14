package com.roquito.platform.notification;

/**
 * Created by puran on 1/30/15.
 */
public class PushEvent {
    private PushNotification pushNotification;
    private PushType pushType;

    public PushType getPushType() {
        return pushType;
    }

    public void setPushType(PushType pushType) {
        this.pushType = pushType;
    }

    public PushNotification getPushNotification() {
        return pushNotification;
    }

    public void setPushNotification(PushNotification pushNotification) {
        this.pushNotification = pushNotification;
    }
}
