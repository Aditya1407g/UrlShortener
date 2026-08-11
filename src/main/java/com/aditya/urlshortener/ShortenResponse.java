package com.aditya.urlshortener;

public class ShortenResponse {

    private String shortUrl;

    public ShortenResponse() {}

    public ShortenResponse(String shortUrl){
        this.shortUrl=shortUrl;
    }

    public String getShortUrl() {
        return shortUrl;
    }

    public void setShortUrl(String shortUrl) {
        this.shortUrl = shortUrl;
    }
}
