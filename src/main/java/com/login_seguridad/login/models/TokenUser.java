package com.login_seguridad.login.models;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "tokens")
public class TokenUser {
    @Id
    private String id;
    private String userId;
    private String token;
    // private Instant expiration;
    private boolean used;

    public TokenUser(String userId, String token){
        this.userId = userId;
        this.token = token;
        // this.expiration = Instant.now().plusSeconds(5*60); // despues de 5 min
        this.used = false;
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

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public boolean isUsed() {
        return used;
    }

    public void setUsed(boolean used) {
        this.used = used;
    }

}
