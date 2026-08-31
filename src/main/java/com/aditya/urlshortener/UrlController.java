package com.aditya.urlshortener;


import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class UrlController {

    private final UrlRepository urlRepository;
    private final UserRepository userRepository;
    @Value("${app.base-url}")
    private String baseUrl;


    public UrlController(UrlRepository urlRepository, UserRepository userRepository) {
        this.urlRepository = urlRepository;
        this.userRepository = userRepository;
    }

    @PostMapping("/shorten")
    @ResponseStatus(HttpStatus.CREATED)
    public ShortenResponse shorten(@Valid @RequestBody ShortenRequest request) {

        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User currentUser = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalStateException("Authenticated user not found in DB"));

        Url url = new Url(request.getUrl());
        url.setUserId(currentUser.getId());
        url = urlRepository.save(url);

        String code = Base62Encoder.encode(url.getId());

        url.setShortCode(code);

        urlRepository.save(url);

        String fullShortUrl = baseUrl+"/" + code;
        return new ShortenResponse(fullShortUrl);
    }


    @GetMapping("/urls")
    public List<UrlSummaryResponse> listMyUrls(){

        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User currentUser = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalStateException("Authenticated user not found in DB"));


        List<Url> urls = urlRepository.findByUserId(currentUser.getId());

        List<UrlSummaryResponse> result = urls.stream()
                .map(url -> new UrlSummaryResponse(
                        url.getLongUrl(),
                        baseUrl+ "/" + url.getShortCode(),
                        url.getCreatedAt(),
                        url.getClickCount()
                ))
                .toList();


        return result;
    }
}
