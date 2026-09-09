package com.aditya.urlshortener;


import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import java.net.URI;
import java.util.Optional;

@RestController
public class RedirectController {


    private final UrlRepository urlRepository;
    private final UrlCacheService urlCacheService;
    private final ClickCountService clickCountService;


    public RedirectController( UrlRepository urlRepository,
                               UrlCacheService urlCacheService,
                               ClickCountService clickCountService) {

        this.urlRepository = urlRepository;
        this.urlCacheService= urlCacheService;
        this.clickCountService=clickCountService;
    }


    @GetMapping("/{shortCode}")
    public ResponseEntity<Void> redirect(@PathVariable String shortCode){

        Optional<String> cachedUrl = urlCacheService.gelLongUrl(shortCode);


        String longUrl;
        if(cachedUrl.isPresent()){
            longUrl=cachedUrl.get();
        }
        else{
            Url url = urlRepository.findByShortCode(shortCode)
                    .orElseThrow(() -> new UrlNotFoundException(shortCode));

            longUrl = url.getLongUrl();
            urlCacheService.put(shortCode, longUrl);
        }

        clickCountService.incrementAsync(shortCode);

        return ResponseEntity
                .status(HttpStatus.FOUND)
                .location(URI.create(longUrl))
                .build();

    }
}
