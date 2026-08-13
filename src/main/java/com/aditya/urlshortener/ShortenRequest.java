package com.aditya.urlshortener;
import jakarta.validation.constraints.NotBlank;


public class ShortenRequest {

    private  String url;

    public ShortenRequest() {}

    public void setUrl(String url){
        this.url = url;
    }
    public String getUrl(){
        return this.url;
    }

}
