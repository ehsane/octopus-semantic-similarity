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

/**
 * A simple POJO that represents a single web search result
 * as returned by Yahoo! BOSS.
 */
public class WebSearchResult extends SearchResult{

    private String displayUrl;

    /**
     * The display url
     * @return the display url
     */
    public String getDisplayUrl() {
        return displayUrl;
    }

    /**
     * set display url
     * @param displayUrl the new display url
     */
    public void setDisplayUrl(String displayUrl) {
        if(null!=displayUrl){
            this.displayUrl = displayUrl;
        }
    }

}
