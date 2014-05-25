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
 * This class is a simple POJO that represents a single news search result
 * as returned by Yahoo! BOSS.
 *
 */
public class NewsSearchResult extends SearchResult {

    private String language;
    private String source;
    private String sourceUrl;
    private String time;

    /**
     * @return the language of the result
     */
    public String getLanguage() {
        return language;
    }

    /**
     * @param language  the language of the result
     */
    public void setLanguage(String language) {
        this.language = language;
    }

    /**
     *
     * @return the source
     */
    public String getSource() {
        return source;
    }

    /**
     *
     * @param source the source
     */
    public void setSource(String source) {
        this.source = source;
    }

    /**
     *
     * @return source url
     */
    public String getSourceUrl() {
        return sourceUrl;
    }

    /**
     *
     * @param sourceUrl the source url
     */
    public void setSourceUrl(String sourceUrl) {
        this.sourceUrl = sourceUrl;
    }

    /**
     *
     * @return time
     */
    public String getTime() {
        return time;
    }

    /**
     *
     * @param time time
     */
    public void setTime(String time) {
        this.time = time;
    }
    
}
