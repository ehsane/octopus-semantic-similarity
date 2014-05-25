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
 * This class is a simple POJO that represents a single image search result
 * as returned by Yahoo! BOSS.
 * 
 */
public class ImageSearchResult extends SearchResult {

    private String filename;
    private String format;
    private long height;
    private String mimeType;
    private String refererClickUrl;
    private String refererUrl;
    private long thumbnailHeight;
    private String thumbnailUrl;
    private long thumbnailWidth;
    private long width;

    /**
     * @return the filname of the image
     */
    public String getFilename() {
        return filename;
    }

    /**
     *
     * @param filename set the filename of the image
     */
    public void setFilename(String filename) {
        this.filename = filename;
    }

    /**
     *
     * @return the image format as returned by BOSS
     */
    public String getFormat() {
        return format;
    }

    /**
     *
     * @param format set the image format
     */
    public void setFormat(String format) {
        this.format = format;
    }

    /**
     *
     * @return - the image height
     */
    public long getHeight() {
        return height;
    }

    /**
     *
     * @param height the image height
     */
    public void setHeight(long height) {
        this.height = height;
    }

    /**
     *
     * @return image mime type
     */
    public String getMimeType() {
        return mimeType;
    }

    /**
     * @param mimeType image mime type
     */
    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    /**
     *
     * @return referrer click url
     */
    public String getRefererClickUrl() {
        return refererClickUrl;
    }

    /**
     *
     * @param refererClickUrl referrer click url
     */
    public void setRefererClickUrl(String refererClickUrl) {
        this.refererClickUrl = refererClickUrl;
    }

    /**
     *
     * @return referer url
     */
    public String getRefererUrl() {
        return refererUrl;
    }

    /**
     *
     * @param refererUrl  referer url
     */
    public void setRefererUrl(String refererUrl) {
        this.refererUrl = refererUrl;
    }

    /**
     *
     * @return thumbnail height
     */
    public long getThumbnailHeight() {
        return thumbnailHeight;
    }

    /**
     *
     * @param thumbnailHeight  thumbnail height
     */
    public void setThumbnailHeight(long thumbnailHeight) {
        this.thumbnailHeight = thumbnailHeight;
    }

    /**
     *
     * @return thumbnail url
     */
    public String getThumbnailUrl() {
        return thumbnailUrl;
    }

    /**
     *
     * @param thumbnailUrl thumbnail url
     */
    public void setThumbnailUrl(String thumbnailUrl) {
        this.thumbnailUrl = thumbnailUrl;
    }

    /**
     *
     * @return thumbnail width
     */
    public long getThumbnailWidth() {
        return thumbnailWidth;
    }

    /**
     *
     * @param thumbnailWidth thumbnail width
     */
    public void setThumbnailWidth(long thumbnailWidth) {
        this.thumbnailWidth = thumbnailWidth;
    }

    /**
     *
     * @return image width
     */
    public long getWidth() {
        return width;
    }

    /**
     *
     * @param width image width
     */
    public void setWidth(long width) {
        this.width = width;
    }
}
