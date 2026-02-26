package com.login_seguridad.login.DTOs;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class NewPassword {
    @NotNull 
    @Size(min=8, message="La contraseña debe de contener mínimo 8 caracteres") 
    @Pattern(regexp="^(?=.*[a-zA-Z])(?=.*\\d)(?=.*[@$!%*#?&])[A-Za-z\\d@$!%*?&]{8,}$", message="La contraseña debe contener números, letras y al menos un caracter especial")
    private String password;

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }


}
