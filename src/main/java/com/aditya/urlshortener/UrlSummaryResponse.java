package com.aditya.urlshortener;

import java.time.LocalDateTime;

public class UrlSummaryResponse {

    private String longUrl;
    private String shortUrl;
    private LocalDateTime createdAt;
    private Long clickCount;

    public UrlSummaryResponse() {};

    public UrlSummaryResponse(String longUrl, String shortUrl, LocalDateTime createdAt, Long clickCount) {
        this.longUrl = longUrl;
        this.shortUrl = shortUrl;
        this.createdAt = createdAt;
        this.clickCount = clickCount;
    }

    public String getLongUrl() {
        return longUrl;
    }

    public void setLongUrl(String longUrl) {
        this.longUrl = longUrl;
    }

    public String getShortUrl() {
        return shortUrl;
    }

    public void setShortUrl(String shortUrl) {
        this.shortUrl = shortUrl;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public Long getClickCount() {
        return clickCount;
    }

    public void setClickCount(Long clickCount) {
        this.clickCount = clickCount;
    }
}
