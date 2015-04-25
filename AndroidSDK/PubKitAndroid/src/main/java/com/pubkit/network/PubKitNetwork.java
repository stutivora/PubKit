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
package com.pubkit.network;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by puran on 3/22/15.
 */
public final class PubKitNetwork {

    private static String PUBKIT_API_URL = "http://app.pubkit.co/push/register";

    public static JSONObject sendPost(String apiKey, JSONObject jsonObject) {
        URL url;
        HttpURLConnection connection = null;
        try {
            //Create connection
            url = new URL(PUBKIT_API_URL);
            String encodedData = jsonObject.toString();
            byte[] postDataBytes = jsonObject.toString().getBytes("UTF-8");

            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("Accept", "application/json");
            connection.setRequestProperty("Content-Length", "" + String.valueOf(postDataBytes.length));
            connection.setRequestProperty("api_key", apiKey);

            connection.setUseCaches(false);
            connection.setDoOutput(true);

            //Send request
            DataOutputStream wr = new DataOutputStream(connection.getOutputStream());
            wr.writeBytes(encodedData);//set data
            wr.flush();

            //Get Response
            InputStream inputStream = connection.getErrorStream(); //first check for error.
            if (inputStream == null) {
                inputStream = connection.getInputStream();
            }
            BufferedReader rd = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            StringBuilder response = new StringBuilder();
            while ((line = rd.readLine()) != null) {
                response.append(line);
                response.append('\r');
            }
            wr.close();
            rd.close();

            String responseString = response.toString();
            if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                return new JSONObject("{'error':'" + responseString + "'}");
            } else {
                try {
                    return new JSONObject(responseString);
                } catch (JSONException e) {
                    Log.e("PUBKIT", "Error parsing data", e);
                }
            }
        } catch (Exception e) {
            Log.e("PUBKIT", "Network exception:", e);
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
        return null;
    }
}
