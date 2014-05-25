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
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.HashSet;
import java.util.Set;
import java.util.ArrayList;
import java.util.List;

/**
 * Encapsulates a Yahoo! BOSS Image search
 *
 */
public class ImageSearch extends BOSSSearch {

    private static String endPoint = "/ysearch/images/v1/";

    private List<ImageSearchResult> results;

    private boolean filtered = false;


    /**
     * Performs a search based upon the values of
     * the searchString and filtered fields for
     * instance
     *
     * @return  - HTTP response code
     */
    public int search() {
        return search(getSearchString(), isFiltered());

    }

    /**
     * Performs a search based on the search string parameter
     * and the filtered field for this instance
     *
     * Sets the value of search string for this instance.
     *
     * @param search  - the search string
     * @return - HTTP response code
     */
    public int search(String search) {
        return search(search, isFiltered());
    }

    /**
     * Performs a search based on the search string parameter
     * and the filter parameter
     *
     * Sets the value of search string and filter for this instance.
     *
     * @param search - the search string
     * @param filter - filter results or not
     * @return - HTTP response code
     * @throws BOSSException runtime exception
     */
    public int search(String search, boolean filter) throws BOSSException {

        setFiltered(filter);

        setSearchString(search);

        String params = "appid=" + getAppKey();

        if (isFiltered()) {
            params += "&filtered=yes";
        }

        try {

            setResponseCode(getHttpRequest().sendGetRequest(getServer() + getEndPoint() + URLEncoder.encode(search) + "?" + params));
            if(HTTP_OK==getResponseCode()){
                JSONObject searchResults = new JSONObject(getHttpRequest().getResponseBody()).getJSONObject("ysearchresponse");
                this.parseResults(searchResults);
            }
        } catch (JSONException e) {
            setResponseCode(500);
            throw new BOSSException("JSON Exception parsing image search results", e);
        } catch (IOException ioe) {
            setResponseCode(500);
            throw new BOSSException("IO Exception", ioe);
        }

        return getResponseCode();

    }

    protected void parseResults(JSONObject jobj) throws JSONException {

        if (jobj != null) {
            

            setResponseCode(jobj.getInt("responsecode"));
            if (jobj.has("nextpage")) setNextPage(jobj.getString("nextpage"));
            if (jobj.has("prevpage")) setPrevPage(jobj.getString("prevpage"));
            setTotalResults(jobj.getLong("totalhits"));
	    long count = jobj.getLong("count");
            setPagerCount(count);
            setPagerStart(jobj.getLong("start"));
            this.setResults(new ArrayList<ImageSearchResult>((int) count));

            if (jobj.has("resultset_images")) {

                JSONArray res = jobj.getJSONArray("resultset_images");
                for (int i = 0; i < res.length(); i++) {
                    JSONObject thisResult = res.getJSONObject(i);
                    ImageSearchResult imageSearchResult = new ImageSearchResult();
                    imageSearchResult.setDescription(thisResult.getString("abstract"));
                    imageSearchResult.setClickUrl(thisResult.getString("clickurl"));
                    imageSearchResult.setDate(thisResult.getString("date"));
                    imageSearchResult.setTitle(thisResult.getString("title"));
                    imageSearchResult.setUrl(thisResult.getString("url"));
                    imageSearchResult.setSize(thisResult.getLong("size"));
                    imageSearchResult.setFilename(thisResult.getString("filename"));
                    imageSearchResult.setFormat(thisResult.getString("format"));
                    imageSearchResult.setHeight(thisResult.getLong("height"));
                    imageSearchResult.setMimeType(thisResult.getString("mimetype"));
                    imageSearchResult.setRefererClickUrl(thisResult.getString("refererclickurl"));
                    imageSearchResult.setRefererUrl(thisResult.getString("refererurl"));
                    imageSearchResult.setThumbnailHeight(thisResult.getLong("thumbnail_height"));
                    imageSearchResult.setThumbnailWidth(thisResult.getLong("thumbnail_width"));
                    imageSearchResult.setThumbnailUrl(thisResult.getString("thumbnail_url"));
                    this.getResults().add(imageSearchResult);
                }
            }
        }

    }

    /**
     * The ordered list of search results for this search
     * @return - set of image search results
     */
    public List<ImageSearchResult> getResults() {
        return results;
    }

    /**
     * Sets the search results set for this search
     * @param results - set of image search results
     */
    public void setResults(List<ImageSearchResult> results) {
        this.results = results;
    }

    /**
     *
     * @return - the end point for an image search
     */
    public static String getEndPoint() {
        return endPoint;
    }

    /**
     * Allows you to override the image search endpoint
     *
     * Defaults to /ysearch/images/v1/
     *
     * @param endPoint - the new end point for an image search
     */
    public static void setEndPoint(String endPoint) {
        if(null!=endPoint){
            ImageSearch.endPoint = endPoint;
        }
    }

    /**
     *
     * @return - a boolean indicating whether these results are filtered or not
     */
    public boolean isFiltered() {
        return filtered;
    }

    /**
     * Set whether or not to filter these results
     *
     * @param filtered - filter the results
     */
    public void setFiltered(boolean filtered) {
        this.filtered = filtered;
    }
}
