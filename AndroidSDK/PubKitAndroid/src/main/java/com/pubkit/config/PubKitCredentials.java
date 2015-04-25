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
package com.pubkit.config;
/**
 * Class that holds the credential info to connect to PubKit backend. Credentials can be obtained
 * from {@link \http://pubkit.co} by registering an app.
 * <p/>
 * Created by puran on 4/10/15.
 */
public final class PubKitCredentials {

    private String pubKitAppId;
    private String pubKitApiKey;
    private String pubKitApiSecret;

    public PubKitCredentials(String pubKitAppId, String pubKitApiKey, String pubKitApiSecret) {
        this.pubKitAppId = pubKitAppId;
        this.pubKitApiKey = pubKitApiKey;
        this.pubKitApiSecret = pubKitApiSecret;
    }

    public String getPubKitAppId() {
        return pubKitAppId;
    }

    public void setPubKitAppId(String pubKitAppId) {
        this.pubKitAppId = pubKitAppId;
    }

    public String getPubKitApiKey() {
        return pubKitApiKey;
    }

    public void setPubKitApiKey(String pubKitApiKey) {
        this.pubKitApiKey = pubKitApiKey;
    }

    public String getPubKitApiSecret() {
        return pubKitApiSecret;
    }

    public void setPubKitApiSecret(String pubKitApiSecret) {
        this.pubKitApiSecret = pubKitApiSecret;
    }
}
