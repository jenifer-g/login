package com.login_seguridad.login.DTOs;

public class VerifyCode {
    private String token;
    private Integer code;
    public String getToken() {
        return token;
    }
    public void setToken(String token) {
        this.token = token;
    }
    public int getCode() {
        return code;
    }
    public void setCode(int code) {
        this.code = code;
    }
    public VerifyCode() {
    }

    
}
