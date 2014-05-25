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

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import octopus.semantic.similarity.api.yahoo.YahooBossAPI;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.jellymold.boss.util.BOSSException;

/**
 * Encapsulates a Yahoo! BOSS web search.
 */
public class WebSearch extends BOSSSearch {
	public static void main(String[] args){
		WebSearch ws = new WebSearch();
		String sentence = "he gainned 100lb and go from a 56lb 8yr old to a 145 uncontrolable 9yr old ";
		
		System.out.println(ws.spellCheck(sentence.split(" ")).get(0).getSuggestion());
	}
    
    private List<WebSearchResult> resultWebs;

    
    YahooBossAPI yahooAPI = new YahooBossAPI();
    /**
     * Performs a search based on the values of parameters search and filters
     *
     * @param search - search string
     * @param webSearchFilters - set of filters
     * @return - HTTP response code
     * @throws BOSSException runtime exception
     */
    public int search(String search) throws BOSSException {
        try {
                JSONObject searchResults = new JSONObject(yahooAPI.search(search)).getJSONObject("bossresponse").getJSONObject("limitedweb");
                this.parseResults(searchResults);

        } catch (JSONException e) {
        	e.printStackTrace();
            setResponseCode(500);
            throw new BOSSException("JSON Exception parsing news search results", e);
        } catch (Exception ioe) {
        	ioe.printStackTrace();
            setResponseCode(500);
            throw new BOSSException("IO Exception", ioe);
        }

        return getResponseCode();

    }
    public List<SpellCheckResult> spellCheck(String[] words) throws BOSSException {
    	List<SpellCheckResult> results = new ArrayList<SpellCheckResult>();
    	StringBuilder query = new StringBuilder();
    	for(String word : words){
    		query.append(word+" ");
    	}
    	SpellCheckResult integratedResult = spellCheck(query.toString().trim());
    	if(integratedResult!=null){
	    	String[] suggestions = integratedResult.getSuggestion().split("&");
	    	for(int i=0;i<suggestions.length;i++){
	    		String suggestion = suggestions[i];
	    		SpellCheckResult curRes = new SpellCheckResult();
	    		curRes.setSuggestion(suggestion);
	    		curRes.setWord(words[i]);
	    		results.add(curRes);
	    		System.out.println(words[i]+" -> "+suggestion);
	    	}
    	}
    	return results;
    }
    
    /**
     * Performs a search based on the values of parameters search and filters
     *
     * @param search - search string
     * @param webSearchFilters - set of filters
     * @return - HTTP response code
     * @throws BOSSException runtime exception
     */
    public SpellCheckResult spellCheck(String word) throws BOSSException {
    	SpellCheckResult result = null;
         try {
                JSONObject searchResults = new JSONObject(yahooAPI.spellCheck(word)).getJSONObject("bossresponse").getJSONObject("spelling");
                long totalResult = searchResults.getLong("totalresults");
                if(totalResult>0){
                	result = new SpellCheckResult();
                	String suggestion = searchResults.getJSONArray("results").getJSONObject(0).getString("suggestion");
                	result.setSuggestion(suggestion);
                	result.setWord(word);
                }
        } catch (JSONException e) {
        	e.printStackTrace();
            setResponseCode(500);
            throw new BOSSException("JSON Exception parsing news search results", e);
        } catch (Exception ioe) {
        	ioe.printStackTrace();
            setResponseCode(500);
            throw new BOSSException("IO Exception", ioe);
        }

        return result;

    }

    protected void parseResults(JSONObject jobj) throws JSONException {

        if (jobj != null) {

            
//            setResponseCode(jobj.getInt("responsecode"));
            if (jobj.has("nextpage")) setNextPage(jobj.getString("nextpage"));
            if (jobj.has("prevpage")) setPrevPage(jobj.getString("prevpage"));
            setTotalResults(jobj.getLong("totalresults"));
            long count = jobj.getLong("count");
            setPagerCount(count);
            setPagerStart(jobj.getLong("start"));
            this.setResults(new ArrayList<WebSearchResult>(((int) count)));
            if(jobj.has("results")){
                JSONArray res = jobj.getJSONArray("results");
                for (int i = 0; i < res.length(); i++) {
                    JSONObject thisResult = res.getJSONObject(i);
                    WebSearchResult newResultWeb = new WebSearchResult();
                    newResultWeb.setDescription(thisResult.getString("abstract"));
                    newResultWeb.setClickUrl(thisResult.getString("clickurl"));
                    newResultWeb.setDate(thisResult.getString("date"));
                    newResultWeb.setTitle(thisResult.getString("title"));
                    newResultWeb.setDisplayUrl(thisResult.getString("dispurl"));
                    newResultWeb.setUrl(thisResult.getString("url"));
//                    newResultWeb.setSize(thisResult.getLong("size"));
                    this.resultWebs.add(newResultWeb);
                }
            }

        }

    }

    public List<WebSearchResult> getResults() {
        return resultWebs;
    }

    public void setResults(List<WebSearchResult> resultWebs) {
        if(null!=resultWebs){
            this.resultWebs = resultWebs;
        }
    }


    /**
     * Inner class (enum) representing the various filter options.
     * used for convenience.
     */
    public static enum WebSearchFilter {

        PORN,HATE;

        private String[] names = {"PORN", "HATE"};

        private String[] labels = {"Porn", "Hate"};

        private String[] urlParts = {"-porn", "-hate"};

        public String toString() {
            return getName();
        }

        public String getName() {
            return names[this.ordinal()];
        }

        public String getLabel() {
            return labels[this.ordinal()];
        }

        public String getUrlPart() {
            return urlParts[this.ordinal()];
        }

        public static Map<String, String> getChoices() {
            Map<String, String> choices = new LinkedHashMap<String, String>();
            for (WebSearchFilter webSearchFilter : WebSearchFilter.values()) {
                choices.put(webSearchFilter.getName(), webSearchFilter.getLabel());
            }
            return choices;
        }

    }
}
