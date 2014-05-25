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
import java.util.List;
import java.util.ArrayList;


/**
 * Encapsulates a Yahoo! BOSS News search
 *
 */
public class NewsSearch extends BOSSSearch{

     private static String endPoint = "/ysearch/news/v1/";

     private List<NewsSearchResult> results;

     private int age = 30; //Yahoo! default

    /**
     * Performs a search based on the current age (default : 30 days) and the current value of searchString
     *
     * @return HTTP Response code
     */
     public int search() {
        return search(getSearchString(), getAge());
     }

    /**
     * Perform a search based on the current aga (default : 30 days) and the search string passed
     * @param search string to search on
     * @return HTTP Response code
     */
    public int search(String search){
        return search(search, getAge());
    }

    /**
     * Performs a search based on the values of parameters for search string and age.
     * @param search
     * @param age
     * @return
     * @throws BOSSException runtime exception
     */
    public int search(String search, int age) throws BOSSException {

        setSearchString(search);

        setAge(age);

        String params = "appid=" + getAppKey();

        params += "&age="+age+"d";
        
        try {

            setResponseCode(getHttpRequest().sendGetRequest(getServer() + getEndPoint() + URLEncoder.encode(search) + "?" + params));
            if(HTTP_OK==getResponseCode()){
                JSONObject searchResults = new JSONObject(getHttpRequest().getResponseBody()).getJSONObject("ysearchresponse");
                this.parseResults(searchResults);
            }
            
        } catch (JSONException e) {
            setResponseCode(500);
            throw new BOSSException("JSON Exception parsing news search results", e);
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
	    final long count = jobj.getLong("count");
            setPagerCount(count);
            setPagerStart(jobj.getLong("start"));

            this.setResults(new ArrayList<NewsSearchResult>((int) count));

            if(jobj.has("resultset_news")){

                JSONArray res = jobj.getJSONArray("resultset_news");

                for (int i = 0; i < res.length(); i++) {
                    JSONObject thisResult = res.getJSONObject(i);
                    NewsSearchResult newsSearchResult = new NewsSearchResult();
                    newsSearchResult.setDescription(thisResult.getString("abstract"));
                    newsSearchResult.setClickUrl(thisResult.getString("clickurl"));
                    newsSearchResult.setUrl(thisResult.getString("url"));
                    newsSearchResult.setLanguage(thisResult.getString("language"));
                    newsSearchResult.setSource(thisResult.getString("source"));
                    newsSearchResult.setSourceUrl(thisResult.getString("sourceurl"));
                    newsSearchResult.setTime(thisResult.getString("time"));
                    newsSearchResult.setTitle(thisResult.getString("title"));
                    newsSearchResult.setDate(thisResult.getString("date"));
                    this.getResults().add(newsSearchResult);
                }
            }

        }
    }

    /**
     *
     * @return age
     */
    public int getAge() {
        return age;
    }

    /**
     *
     * @param age age
     */
    public void setAge(int age) {
        this.age = age;
    }

    /**
     * Get the set of news search results
     * @return a set of news search results
     */
    public List<NewsSearchResult> getResults() {
        return results;
    }

    /**
     * Sets the news search results.
     * @param results a set of news search results
     */
    public void setResults(List<NewsSearchResult> results) {
        this.results = results;
    }

    /**
     *
     * @return - the end point for an news search
     */
    public static String getEndPoint() {
        return endPoint;
    }

    /**
     * Allows you to override the news search endpoint
     *
     * Defaults to /ysearch/news/v1/
     *
     * @param endPoint - the new end point for an image search
     */
    public static void setEndPoint(String endPoint) {
        if(null!=endPoint){
            NewsSearch.endPoint = endPoint;
        }
    }
}
