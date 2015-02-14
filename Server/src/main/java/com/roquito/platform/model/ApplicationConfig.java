package com.roquito.platform.model;

import org.mongodb.morphia.annotations.Embedded;

import java.util.Map;

/**
 * Created by puran on 2/7/15.
 */
@Embedded
public class ApplicationConfig {
    private String type;
    private Map<String, String> configParams;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Map<String, String> getConfigParams() {
        return configParams;
    }

    public void setConfigParams(Map<String, String> configParams) {
        this.configParams = configParams;
    }
}
