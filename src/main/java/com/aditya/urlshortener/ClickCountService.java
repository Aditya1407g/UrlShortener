package com.aditya.urlshortener;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class ClickCountService {
    private static final Logger log = LoggerFactory.getLogger(ClickCountService.class);

    private final UrlRepository urlRepository;

    public ClickCountService(UrlRepository urlRepository) {
        this.urlRepository = urlRepository;
    }

    @Async
    public void incrementAsync(String shortcode){
        try {
            urlRepository.incrementClickCount(shortcode);
        } catch (Exception ex) {
            log.warn("Async click count increment failed for shortcode {}", shortcode, ex);
        }
    }
}
