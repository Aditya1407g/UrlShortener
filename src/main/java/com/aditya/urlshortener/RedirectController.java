package com.aditya.urlshortener;


import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import java.net.URI;

@RestController
public class RedirectController {


    private final UrlRepository urlRepository;


    public RedirectController( UrlRepository urlRepository) {
        this.urlRepository = urlRepository;
    }


    @GetMapping("/{shortCode}")
    public ResponseEntity<Void> redirect(@PathVariable String shortCode){
        if (shortCode.equals("crash")) throw new RuntimeException("test crash");
        Url url = urlRepository.findByShortCode(shortCode)
                .orElseThrow(() -> new UrlNotFoundException(shortCode));

        url.setClickCount(url.getClickCount() + 1);
        urlRepository.save(url);

        return ResponseEntity
                .status(HttpStatus.FOUND)
                .location(URI.create(url.getLongUrl()))
                .build();

    }
}
