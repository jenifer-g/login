package com.login_seguridad.login.services;

import java.util.List;

import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.login_seguridad.login.DTOs.UserLoginDto;
import com.login_seguridad.login.DTOs.UserRegistrationDto;
import com.login_seguridad.login.models.TokenUser;
import com.login_seguridad.login.models.User;
import com.login_seguridad.login.repository.ITokenRepository;
import com.login_seguridad.login.repository.IUserRepository;


@Service

public class UserService {
    final IUserRepository userRepository;
    final AuthenticationManager authManager;
    final JWTService jwtService; 
    final CookieService cookieService;
    final MailService mailService;
    final MyUserDetailsService userDetailsService;
    final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(12);
    final ITokenRepository tokenRepository;


    public UserService(IUserRepository userRepository, AuthenticationManager authenticationManager, JWTService jwtService, CookieService cookieService, MailService mailService, MyUserDetailsService userDetailsService, ITokenRepository tokenRepository){
        this.userRepository = userRepository; 
        this.authManager = authenticationManager;
        this.jwtService = jwtService;
        this.cookieService = cookieService;
        this.mailService = mailService;
        this.userDetailsService = userDetailsService;
        this.tokenRepository = tokenRepository;
    }

    public List<User> getAllUsers(){
        return this.userRepository.findAll();
    }

    public ResponseCookie loginUser(UserLoginDto user){
        // aqui hay que consultar primero que su email este verificado
        if(userRepository.existsByEmailAndEmailVerifiedFalse(user.getEmail())){
            throw new RuntimeException("Verifica tu correo para poder iniciar sesión");
        }
        
        Authentication auth = authManager.authenticate(new UsernamePasswordAuthenticationToken(user.getEmail(), user.getPassword()));  
        if(auth.isAuthenticated()){ 
            String token = jwtService.generateToken(user.getEmail());
            ResponseCookie cookie = this.cookieService.configCookie(token, 60*15);

            return cookie;
        }
        return null;
    }


    public User registrarUsuario(UserRegistrationDto userInfo){

        User userExists = userRepository.findByEmail(userInfo.getEmail());
    
        if(userExists!=null){
            boolean emailVerified = userExists.isEmailVerified();
            if(emailVerified){
                throw new RuntimeException("Ya existe un usuario registrado con este correo");
            }else{ 
                throw new RuntimeException("Verifica tu correo para poder iniciar sesión");
            }
        }
        // si no es ninguna
        // el dto se tiene que convertir en entidad, porque el repository espera eso
        User user = new User();
        user.setName(userInfo.getName());
        user.setEmail(userInfo.getEmail());
        user.setPassword(encoder.encode(userInfo.getPassword()));
        user.setEmailVerified(false);

        String token = jwtService.generateToken(userInfo.getEmail());
        // enviar correo de confirmacion
        mailService.sendEmail(userInfo.getEmail(), token, "De click en el link para verificar su cuenta");

        return this.userRepository.save(user);

    }

    public void updatePassword(String id, String newPassword){
        String passEncoded = encoder.encode(newPassword);
        userRepository.updatePassword(id, passEncoded);
    }


    public ResponseCookie logout(){
        return cookieService.configCookie(null, 0);

    }

    public void verifyEmail(String token){
        
        String email = jwtService.extractSubject(token);
         User user = userRepository.findByEmail(email);

         if(user==null){
            throw new RuntimeException("Usuario no encontrado");
         }
        
        if(!jwtService.validateToken(token, user)){
            throw new RuntimeException("Token inválido");
        }

        userRepository.updateEmailVerified(email);


    }

    public void forgotPassword(String email){
     
        User user = this.userRepository.findByEmail(email.trim().toLowerCase());

      
        if(user==null){
            throw new RuntimeException("Correo inválido");
        }
        String token = jwtService.generateToken(email);
        String userId = user.getId();
        // insertamos el token con expiracion en la bd
        TokenUser tokenObj = new TokenUser(userId, token);
        this.tokenRepository.save(tokenObj);
            
        mailService.recoverPasswordEmail(user.getEmail(), token);

    }

    public boolean recoverPassword(String token, String newPassword){

        String email = jwtService.extractSubject(token);

        User user = userRepository.findByEmail(email);
      
        if(!jwtService.validateToken(token, user)){
            throw new RuntimeException("Token inválido");
        }
        
        TokenUser tokenInfo = tokenRepository.findByTokenAndUsedFalse(token);
        if(tokenInfo==null){
            throw new RuntimeException("Token ya utilizado o invàlido");
        }
        this.updatePassword(user.getId(), newPassword);
      
        tokenInfo.setUsed(true);
        tokenRepository.save(tokenInfo);
        
        return true;
    }
}