package com.login_seguridad.login.DTOs;

public class UserLoginDto {
    private String email;
    private String password;

    @Override
    public String toString(){
        return "email del usuario: "+email;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    
}
