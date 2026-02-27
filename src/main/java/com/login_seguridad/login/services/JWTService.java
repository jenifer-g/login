package com.login_seguridad.login.services;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import com.login_seguridad.login.models.User;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

@Service
public class JWTService {
    
    @Value("${jwt.secret}")
    private String secretKey;

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
        try {
            return  Jwts.parser()
            //.verifyWith(getSecretKey())
            .setSigningKey(getSecretKey())
            .build()
            .parseSignedClaims(token).getPayload();
            
        } catch (io.jsonwebtoken.security.SignatureException e) {
            throw new RuntimeException("Firma inválida");
            // excepciones para que muestre mensajes mas cortos
        }catch(io.jsonwebtoken.ExpiredJwtException e){
            throw new RuntimeException("El token ha expirado");
        }
        
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
    public boolean validateToken(String token, User user) {

        String name = extractSubject(token);
        return (name.equals(user.getEmail()) && ! isTokenExpired(token));
        
    }
   

}
