package com.login_seguridad.login.controllers;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.login_seguridad.login.DTOs.EmailDto;
import com.login_seguridad.login.DTOs.NewPassword;
import com.login_seguridad.login.DTOs.UserLoginDto;
import com.login_seguridad.login.DTOs.UserRegistrationDto;
import com.login_seguridad.login.services.UserService;

import jakarta.validation.Valid;


@RestController
@CrossOrigin(origins="http://localhost:5173")
public class UserController {
final UserService userService;

    public UserController(UserService userService){
        this.userService = userService;
    }
    
   
    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody UserLoginDto user) {
        try {
            ResponseCookie cookie = userService.loginUser(user);
            if(cookie==null){

                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Email o contraseña incorrectos");
            }
            return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, cookie.toString()).body("Login exitoso");
        
            
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/signup")
    public ResponseEntity<?> registrar(@Valid @RequestBody UserRegistrationDto user) {
        try {
            this.userService.registrarUsuario(user);
            return ResponseEntity.ok("Registro exitoso, verifica tu correo para poder inciar sesión");
            
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }

    }
    @GetMapping("/admins")
    public String getMoreInfo() {
        return "Si ves esto, estas autorizado";
    }
    
    @GetMapping("/verifyEmail") 
    public ResponseEntity<?> verifyEmail(@RequestParam String token) {
        try {
            userService.verifyEmail(token);
            return ResponseEntity.ok("Email verificado correctamente, ya puedes iniciar sesión");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // cambiar contraseña cuando está loggeado
    @PutMapping("users/{id}/password")
    public ResponseEntity<?> updatePassword(@PathVariable String id, @RequestBody @Valid NewPassword password) {
        try {
            userService.updatePassword(id, password.getPassword());
            return ResponseEntity.ok("La contraseña se actualizó correctamente");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/forgotPassword")
    public ResponseEntity<String> forgotPassword(@RequestBody EmailDto emailDto) {
        try {
            userService.forgotPassword(emailDto.getEmail());
            return ResponseEntity.ok("Enviamos un enlace de recuperación a tu correo");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/recoverPassword")
    public ResponseEntity<String> recoverPassword(@RequestBody @Valid NewPassword infoResetPassword) {

        try {
            return  userService.recoverPassword(infoResetPassword.getToken(), infoResetPassword.getPassword()) ? 
            ResponseEntity.ok("Contraseña actualizada correctamente") : ResponseEntity.badRequest().body("Hubo un error al actualizar la contraseña");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }

    }


    @GetMapping("/logout")
    public ResponseEntity<?> logout() {
        try {
            ResponseCookie cookie = userService.logout();
            return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, cookie.toString()).body("Sesión cerrada correctamente");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    

    // en caso de que alguna de las anotaciones no se cumplan
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Map<String, String> handleValidations(MethodArgumentNotValidException ex){
        Map<String, String> errors = new HashMap<>();

        ex.getBindingResult().getAllErrors().forEach(error->{
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        return errors;
    }

}
