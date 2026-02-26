package com.login_seguridad.login.services;

import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Service;

@Service
public class CookieService {
    
    public ResponseCookie configCookie(String token, Integer expiration){
        return ResponseCookie.from("token", token)
        .httpOnly(true)
        .secure(false)
        .sameSite("Strict")
        .path("/")
        // .maxAge(60 * 15)
        .maxAge(expiration)
        .build();
    }
}
