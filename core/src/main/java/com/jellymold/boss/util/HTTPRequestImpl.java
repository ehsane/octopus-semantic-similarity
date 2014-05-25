/*
 * Copyright 2008 Jellymold.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *	    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.jellymold.boss.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;


/**
 * Simple BOSS HTTP Request implementation
 */
public class HTTPRequestImpl implements BOSSHTTPRequest {

    private String responseBody = "";

    public HTTPRequestImpl() {

    }

    /**
     * Sends an HTTP GET request to a url
     *
     * @param url the url
     * @return - HTTP response code
     */
    public int sendGetRequest(String url) throws IOException{
        int ret = 500;
        try {
            URL u = new URL(url);
            HttpURLConnection uc = (HttpURLConnection) u.openConnection();
            ret = uc.getResponseCode();
            if(200==ret){
                BufferedReader rd = new BufferedReader(new InputStreamReader(uc.getInputStream()));
                StringBuffer sb = new StringBuffer();
                String line;
                while ((line = rd.readLine()) != null) {
                    sb.append(line);
                }
                rd.close();
                setResponseBody(sb.toString());
            }
        }catch (MalformedURLException ex) {
            throw new IOException(url+" is not valid");
        }catch (IOException ie) {
			throw new IOException("IO Exception" + ie.getMessage());
		}
        return ret;
    }


    public String getResponseBody() {
        return responseBody;
    }

    public void setResponseBody(String responseBody) {
        if (null != responseBody) {
            this.responseBody = responseBody;
        }
    }
}
