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
package com.jellymold.boss;

import com.jellymold.boss.util.BOSSException;
import com.jellymold.boss.util.BOSSHTTPRequest;
import com.jellymold.boss.util.HTTPRequestImpl;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

/**
 * Abstract search class that the other searches (news, image & web) extend.
 */
public abstract class BOSSSearch {

    protected static final int HTTP_OK = 200;

    private static BOSSHTTPRequest httpRequest = new HTTPRequestImpl();

    /**
     * The BOSS app key provided by Yahoo!
     */
    private String appKey = "dj0yJmk9bE91bnBzWGtObjlzJmQ9WVdrOVFUUnVkSGRUTXpRbWNHbzlNVFV3T1RJM01qVTJNZy0tJnM9Y29uc3VtZXJzZWNyZXQmeD0yYg--";

    /**
     * The BOSS server
     */
    private static String server = "http://yboss.yahooapis.com/ysearch/";

    private int responseCode = 0;
    private String searchString = "";
    private String nextPage = "";
    private String prevPage = "";
    private long pagerCount = 0;
    private long pagerStart = 0;
    private long totalResults = 0;

    /**
     * Implemented by the Search classes to parse the returned results set
     *
     * @param results - The results as a JSONObject
     * @throws JSONException - JSONException can be thrown
     */
    protected abstract void parseResults(JSONObject results) throws JSONException;

    /**
     * Requests the next page of results from a given search
     * and populates the search results set with them.
     * <p/>
     * If no search has been performed or nextPage is null then
     * this method will return as if it encountered a HTTP 500
     *
     * @return - HTTP response code
     * @throws BOSSException runtime exception
     */
    public int getNextPage() throws BOSSException {
        if ("".equals(nextPage)) {
            setResponseCode(500);

        } else {
            try {
                setResponseCode(httpRequest.sendGetRequest(getServer() + nextPage));
                if (HTTP_OK == getResponseCode()) {
                    JSONObject searchResults = new JSONObject(httpRequest.getResponseBody()).getJSONObject("ysearchresponse");
                    this.parseResults(searchResults);
                }
            } catch (JSONException e) {
                throw new BOSSException("Unable to get next page", e);
            } catch (IOException ioe) {
                throw new BOSSException("IO Exception", ioe);
            }
        }

        return getResponseCode();
    }

    /**
     * Sets the next page string
     *
     * @param nextPage the next page end point string
     */
    protected void setNextPage(String nextPage) {
        if (nextPage != null) {
            this.nextPage = nextPage;
        }
    }

    /**
     * Requests the previous page of results from a given search
     * and populates the search results set with them
     * <p/>
     * If no search has been performed or prevPage is null then
     * this method will return as if it encountered a HTTP 500
     *
     * @return - HTTP response code
     * @throws BOSSException runtime exception
     */
    public int getPreviousPage() throws BOSSException {
        if ("".equals(prevPage)) {
            setResponseCode(500);
        } else {
            try {
                setResponseCode(httpRequest.sendGetRequest(getServer() + prevPage));
                if (HTTP_OK == getResponseCode()) {
                    JSONObject searchResults = new JSONObject(httpRequest.getResponseBody()).getJSONObject("ysearchresponse");
                    this.parseResults(searchResults);
                }
            } catch (JSONException e) {
                throw new BOSSException("Unable to get previous page", e);
            } catch (IOException ioe) {
                throw new BOSSException("IO Exception", ioe);
            }
        }
        return getResponseCode();
    }

    /**
     * Sets the previous page string
     *
     * @param prevPage the previous page end point string
     */
    protected void setPrevPage(String prevPage) {
        if (prevPage != null) {
            this.prevPage = prevPage;
        }
    }

    /**
     * Gets the HTTP response code for the last executed search
     *
     * @return - HTTP response code
     */
    public int getResponseCode() {
        return responseCode;
    }

    /**
     * Sets the HTTP response code for the last executed search
     *
     * @param responseCode - The HTTP response code
     */
    protected void setResponseCode(int responseCode) {
        this.responseCode = responseCode;
    }

    /**
     * Gets the search string
     *
     * @return - The search string
     */
    public String getSearchString() {
        return searchString;
    }

    /**
     * Set the search string to be used
     *
     * @param searchString - The search string
     */
    public void setSearchString(String searchString) {
        if (null != searchString) {
            this.searchString = searchString;
        }
    }

    /**
     * Gets pager count
     *
     * @return - Number of pages
     */
    public long getPagerCount() {
        return pagerCount;
    }

    /**
     * Sets the pager count
     *
     * @param pagerCount - The pager count
     */
    protected void setPagerCount(long pagerCount) {
        this.pagerCount = pagerCount;
    }

    /**
     * Get the pager start count
     *
     * @return - Pager start
     */
    public long getPagerStart() {
        return pagerStart;
    }

    /**
     * Sets the pager start
     *
     * @param pagerStart - The pager start
     */
    public void setPagerStart(long pagerStart) {
        this.pagerStart = pagerStart;
    }

    /**
     * Get the total number of search results found by BOSS
     *
     * @return - total results
     */
    public long getTotalResults() {
        return totalResults;
    }

    /**
     * Sets the total results
     *
     * @param totalResults - The number of results
     */
    protected void setTotalResults(long totalResults) {
        this.totalResults = totalResults;
    }

    /**
     * @return - the app key
     */
    public String getAppKey() {
        return appKey;
    }

    /**
     * @param appKey - a new app key
     */
    public void setAppKey(String appKey) {
        if (null != appKey) {
            this.appKey = appKey;
        }
    }

    /**
     * @return the server url
     */
    public static String getServer() {
        return server;
    }

    /**
     * Allows you to set the BOSS server URL
     * defaults to http://boss.yahooapis.com
     *
     * @param server - the new server url
     */
    public static void setServer(String server) {
        if (null != server) {
            BOSSSearch.server = server;
        }
    }

    /**
     * @return - an implementation of HTTPRequest
     */
    public static BOSSHTTPRequest getHttpRequest() {
        return httpRequest;
    }

    /**
     * Set the httpRequest object to any valid implementation of
     * HTTPRequest
     *
     * @param httpRequest - a HTTPRequest implementation.
     */
    public static void setHttpRequest(BOSSHTTPRequest httpRequest) {
        if (null != httpRequest) {
            BOSSSearch.httpRequest = httpRequest;
        }
    }
}
