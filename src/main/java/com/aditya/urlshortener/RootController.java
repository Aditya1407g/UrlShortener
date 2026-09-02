package com.aditya.urlshortener;


import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class RootController {

    @GetMapping("/")
    public Map<String, String>  info(){
        return Map.of(
                "service", "URL Shortener API",
                "docs", "https://github.com/Aditya1407g/UrlShortener",
                "status", "operational"
        );
    }
}
