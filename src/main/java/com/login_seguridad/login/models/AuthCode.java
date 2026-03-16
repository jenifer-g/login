package com.login_seguridad.login.models;

import java.time.Instant;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection="auth_codes")
public class AuthCode {
    @Id
    private String id;
    private String userId;
    private int code;
    private Instant timeExpiration;

    public AuthCode(String userId, int code, Instant timeExpiration) {
        this.code = code;
        this.timeExpiration = timeExpiration;
        this.userId = userId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public Instant getTimeExpiration() {
        return timeExpiration;
    }

    public void setTimeExpiration(Instant timeExpiration) {
        this.timeExpiration = timeExpiration;
    }




  
    
    
}
