package com.roquito.platform.model;

import org.mongodb.morphia.annotations.Embedded;

/**
 * Created by puran on 1/18/15.
 */
@Embedded
public class ApplicationUser {
    private String userId;
    private String permission;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getPermission() {
        return permission;
    }

    public void setPermission(String permission) {
        this.permission = permission;
    }
}
