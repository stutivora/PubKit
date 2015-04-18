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
package com.pubkit;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Created by puran
 */
@Component
@ConfigurationProperties(prefix = "pubkit")
public class PubKitConfig {
    
    private String environment;
    private String redisHost;
    private int redisPort;
    private int redisTimeout;
    private int redisDatabase;
    private String redisPassword;
    private String mapdbFilePath;
    private String mapdbPassword;
    
    public String getEnvironment() {
        return environment;
    }
    
    public void setEnvironment(String environment) {
        this.environment = environment;
    }
    
    public boolean isDevEnvironment() {
        return "dev".equalsIgnoreCase(environment);
    }
    
    public String getRedisHost() {
        return redisHost;
    }
    
    public void setRedisHost(String redisHost) {
        this.redisHost = redisHost;
    }
    
    public int getRedisPort() {
        return redisPort;
    }
    
    public void setRedisPort(int redisPort) {
        this.redisPort = redisPort;
    }
    
    public int getRedisTimeout() {
        return redisTimeout;
    }
    
    public void setRedisTimeout(int redisTimeout) {
        this.redisTimeout = redisTimeout;
    }
    
    public int getRedisDatabase() {
        return redisDatabase;
    }
    
    public void setRedisDatabase(int redisDatabase) {
        this.redisDatabase = redisDatabase;
    }
    
    public String getRedisPassword() {
        return redisPassword;
    }
    
    public void setRedisPassword(String redisPassword) {
        this.redisPassword = redisPassword;
    }
    
    public String getMapdbFilePath() {
        return mapdbFilePath;
    }
    
    public void setMapdbFilePath(String mapdbFilePath) {
        this.mapdbFilePath = mapdbFilePath;
    }
    
    public String getMapdbPassword() {
        return mapdbPassword;
    }
    
    public void setMapdbPassword(String mapdbPassword) {
        this.mapdbPassword = mapdbPassword;
    }
}
