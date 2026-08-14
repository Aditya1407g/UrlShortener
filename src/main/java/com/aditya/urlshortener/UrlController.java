package com.aditya.urlshortener;


import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class UrlController {

    private final UrlRepository urlRepository;


    public UrlController(UrlRepository urlRepository) {
        this.urlRepository = urlRepository;
    }

    @PostMapping("/shorten")
    @ResponseStatus(HttpStatus.CREATED)
    public ShortenResponse shorten(@Valid @RequestBody ShortenRequest request) {

        Url url = new Url(request.getUrl());

        url = urlRepository.save(url);

        String code = Base62Encoder.encode(url.getId());

        url.setShortCode(code);

        urlRepository.save(url);

        String fullShortUrl = "http://localhost:8080/" + code;
        return new ShortenResponse(fullShortUrl);
    }
}
