package com.aditya.urlshortener;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;

@Component
public class RateLimitFilter extends OncePerRequestFilter {


    private final RateLimiter rateLimiter;
    private final ObjectMapper objectMapper;

    @Value("${ratelimit.shorten.capacity}")
    private int shortenCapacity;
    @Value("${ratelimit.shorten.refill-rate-per-second}")
    private double shortenRefillRate;

    @Value("${ratelimit.login.capacity}")
    private int loginCapacity;
    @Value("${ratelimit.login.refill-rate-per-second}")
    private double loginRefillRate;

    @Value("${ratelimit.register.capacity}")
    private int registerCapacity;
    @Value("${ratelimit.register.refill-rate-per-second}")
    private double registerRefillRate;

    public RateLimitFilter(RateLimiter rateLimiter, ObjectMapper objectMapper){
        this.rateLimiter=rateLimiter;
        this.objectMapper=objectMapper;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain) throws ServletException, IOException {

        String path = request.getRequestURI();
        String method = request.getMethod();

        String rateLimitKey = null;
        int capacity = 0;
        double refillRate = 0;

        if("POST".equals(method) && "/api/shorten".equals(path)){

            String username = getUsername();
            if(username != null){
                rateLimitKey = "ratelimit:user:" + username + ":shorten";
                capacity=shortenCapacity;
                refillRate=shortenRefillRate;
            }
        }
        else if("POST".equals(method) && "/api/auth/login".equals(path)){
            String ip = getClientIp(request);
            rateLimitKey= "ratelimit:ip:" + ip + ":login";
            capacity=loginCapacity;
            refillRate=loginRefillRate;
        } else if ("POST".equals(method) && "/api/auth/register".equals(path)) {
             String ip = getClientIp(request);
             rateLimitKey= "ratelimit:ip:" + ip + ":register";
             capacity=registerCapacity;
             refillRate=registerRefillRate;
        }
        if(rateLimitKey!=null){
            boolean allowed = rateLimiter.tryConsume(rateLimitKey,capacity,refillRate);
            if(!allowed){
                write429(response);
                return;
            }
        }

        chain.doFilter(request, response);

    }

    private String getUsername(){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getPrincipal())) {
            return auth.getName();
        }
        return null;
    }
    private String getClientIp(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            return xForwardedFor.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }

    private void write429(HttpServletResponse response) throws IOException {
        ErrorResponse body = new ErrorResponse(
                429,
                "Too Many Requests",
                "Rate limit exceeded. Try again shortly."
        );
        response.setStatus(429);
        response.setHeader("Retry-After", "60");
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.getWriter().write(objectMapper.writeValueAsString(body));
    }
}
