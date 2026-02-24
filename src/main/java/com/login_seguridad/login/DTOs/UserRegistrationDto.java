package com.login_seguridad.login.DTOs;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class UserRegistrationDto {
    @NotNull(message="El nombre es obligatorio")
    @NotBlank(message="El nombre es obligatorio")
    @Pattern(regexp="^[A-Za-zÁÉÍÓÚÑáéíóúñ ]+$", message="El nombre no puede contener números")
    private String name;

    @NotBlank(message="El correo es obligatorio")
    @NotNull(message="El correo es obligatorio")
    @Email(message="El correo no tiene el formato correcto")
    private String email;

    @NotNull(message="La contraseña es obligatoria")
    @NotBlank(message="La contraseña es obligatoria")
    @Size(min=8, message="La conntraseña debe de contener mímino 8 caracteres")
    @Pattern(regexp="^(?=.*[a-zA-Z])(?=.*\\d)(?=.*[@$!%*#?&])[A-Za-z\\d@$!%*?&]{8,}$", message="La contraseña debe contener números, letras y al menos un caracter especial")
    private String password;
    private boolean emailVerified;

    public UserRegistrationDto(){

    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isEmailVerified() {
        return emailVerified;
    }

    public void setEmailVerified(boolean emailVerified) {
        this.emailVerified = emailVerified;
    }

    
}
