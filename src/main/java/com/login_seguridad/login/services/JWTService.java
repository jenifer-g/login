package com.login_seguridad.login.services;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

@Service
public class JWTService {
    // eliminar estp
    // private static final Key key = Keys.secretKeyFor(SignatureAlgorithm.ES256);
    @Value("${jwt.secret}")
    private String secretKey;
    // private final SecretKey key = Keys.hmacShaKeyFor(SECRE)

    private Key getSecretKey(){
        Key key = Keys.hmacShaKeyFor(secretKey.getBytes());
        return key;

    }
    public String generateToken(String email){

        Map<String, Object> claims = new HashMap<>();

        return Jwts.builder()
        .claims()
        .add(claims)
        .subject(email)
        .issuedAt(new Date(System.currentTimeMillis()))
        .expiration(new Date(System.currentTimeMillis()+1000*60*15)) // ms s m h
        .and()
        .signWith(getSecretKey())
        .compact();

    }
   

    public Claims extractAllClaims(String token){
        return  Jwts.parser()
                //.verifyWith(getSecretKey())
        .setSigningKey(getSecretKey())
        .build()
        .parseSignedClaims(token).getPayload();
        
    }
    public String extractSubject(String token){
        return extractAllClaims(token).getSubject();
    }

    public boolean isTokenExpired(String token){
        return extractAllClaims(token).getExpiration().before(new Date());
    }

    public boolean validateToken(String token, UserDetails userDetails) {
        String name = extractSubject(token);
        
        return (name.equals(userDetails.getUsername()) && ! isTokenExpired(token));
        
    }
   

}
