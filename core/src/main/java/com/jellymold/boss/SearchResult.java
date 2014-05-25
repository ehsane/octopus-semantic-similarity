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
 * Abstract POJO search result that the other searches (news, image & web) extend.
 *
 */
public abstract class SearchResult {

    private String title = "";
    private String url = "";
    private String date = "";
    private String description = "";
    private Long size = 0l;
    private String clickUrl = "";

    /**
     * The result title
     * @return result title
     */
    public String getTitle() {
        return title;
    }

    /**
     * The result title
     * @param title the result title
     */
    public void setTitle(String title) {
        if(null!=title){
            this.title = title;
        }
    }

    /**
     * result url
     * @return the result url
     */
    public String getUrl() {
        return url;
    }

    /**
     * result url
     * @param url the result url
     */
    public void setUrl(String url) {
        if(null!=url){
            this.url = url;
        }
    }

    /**
     * result date
     * @return the result date
     */
    public String getDate() {
        return date;
    }

    /**
     * result date
     * @param date the result date
     */
    public void setDate(String date) {
        if(null!=date){
            this.date = date;
        }
    }

    /**
     * result description string
     * @return result description string
     */
    public String getDescription() {
        return description;
    }

    /**
     * result description string
     * @param description result description string
     */
    public void setDescription(String description) {
        if(null!=description){
            this.description = description;
        }
    }

    /**
     * size
     * @return size
     */
    public Long getSize() {
        return size;
    }

    /**
     * size
     * @param size size
     */
    public void setSize(Long size) {
        this.size = size;
    }

    /**
     * *NOTE* There is a requirement to include the clickurl in anchor link of your search results.
     * @return the click url
     */
    public String getClickUrl() {
        return clickUrl;
    }

    /**
     * *NOTE* There is a requirement to include the clickurl in anchor link of your search results.
     * @param clickUrl the click url
     */
    public void setClickUrl(String clickUrl) {
        if(null!=clickUrl){
            this.clickUrl = clickUrl;
        }
    }
}
