package com.login_seguridad.login.services;

import java.security.SecureRandom;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.login_seguridad.login.DTOs.UserDto;
import com.login_seguridad.login.DTOs.UserLoginDto;
import com.login_seguridad.login.DTOs.UserRegistrationDto;
import com.login_seguridad.login.models.AuthCode;
import com.login_seguridad.login.models.TokenUser;
import com.login_seguridad.login.models.User;
import com.login_seguridad.login.models.UserPrincipal;
import com.login_seguridad.login.repository.IAuthCodeRepository;
import com.login_seguridad.login.repository.ITokenRepository;
import com.login_seguridad.login.repository.IUserRepository;


@Service

public class UserService {
    final IUserRepository userRepository;
    final AuthenticationManager authManager;
    final JWTService jwtService; 
    final CookieService cookieService;
    final MailService mailService;
    // final MyUserDetailsService userDetailsService;
    
    final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(12);
    final ITokenRepository tokenRepository;
    final IAuthCodeRepository authCodeRepository;


    public UserService(IUserRepository userRepository, AuthenticationManager authenticationManager, JWTService jwtService, CookieService cookieService, MailService mailService, ITokenRepository tokenRepository, IAuthCodeRepository authCodeRepository){
        this.userRepository = userRepository; 
        this.authManager = authenticationManager;
        this.jwtService = jwtService;
        this.cookieService = cookieService;
        this.mailService = mailService;
        // this.userDetailsService = userDetailsService;
        this.tokenRepository = tokenRepository;
        this.authCodeRepository = authCodeRepository;
    }

    private UserDto getUserFromToken(String token){

        String email = jwtService.extractSubject(token);

        User user = userRepository.findByEmail(email);
        return new UserDto(user.getName(), user.getEmail(), user.getId());
    }

    public String primaryAuthentication(UserLoginDto user){
        // aqui hay que consultar primero que su email este verificado
        if(userRepository.existsByEmailAndEmailVerifiedFalse(user.getEmail())){
            throw new RuntimeException("Verifica tu correo para poder iniciar sesión");
        }
        
        Authentication auth = authManager.authenticate(new UsernamePasswordAuthenticationToken(user.getEmail(), user.getPassword()));  
        if(auth.isAuthenticated()){ 

            // si las credenciales son validas, generamos codigo
            SecureRandom secureRandom = new SecureRandom();
            int code = 1000+secureRandom.nextInt(9000);

            UserPrincipal userDetails = (UserPrincipal) auth.getPrincipal();

            Instant expiration = Instant.now().plus(5, ChronoUnit.MINUTES);
            
            // almacenar en la bd el codigo
            AuthCode authCode = new AuthCode(userDetails.getId(), code, expiration);
            authCodeRepository.save(authCode);
            // enviamos el codigo
            mailService.sendMfaEmail(userDetails.getUsername(), code);

            return jwtService.generateToken(userDetails.getUsername());
        }
        return null;
    }

    public ResponseCookie verifyAuthCode(int code, String token){
      
        UserDto user = getUserFromToken(token);


        if(!jwtService.validateToken(token, user)){
            throw new RuntimeException("Token inválido");
        }

        // validar si existe
        AuthCode authCode = authCodeRepository.findByUserIdAndCode(user.getId(), code).orElseThrow(()-> new RuntimeException("Código inválido")); // como devueelve optional
        // validar si no está expirado
        if(!authCode.getTimeExpiration().isAfter(Instant.now())){
            throw new RuntimeException("Código expirado");
        }
        // si el código es válido, entonces, lo eliminamos de bs y generamos y retornamos un reponse cookie

        authCodeRepository.deleteById(authCode.getId());

        String newToken = jwtService.generateToken(user.getEmail());
        return cookieService.configCookie(newToken, 60*15);
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

         UserDto user = getUserFromToken(token);

         if(user==null){
            throw new RuntimeException("Usuario no encontrado");
         }
        
        if(!jwtService.validateToken(token, user)){
            throw new RuntimeException("Token inválido");
        }

        userRepository.updateEmailVerified(user.getEmail());


    }

    public void forgotPassword(String email){
     
        User user = this.userRepository.findByEmail(email.trim().toLowerCase());

      
        if(user==null){
            throw new RuntimeException("Correo inválido");
        }
        String token = jwtService.generateToken(email);

        // insertamos el token con expiracion en la bd
        TokenUser tokenObj = new TokenUser(user.getId(), token);
        this.tokenRepository.save(tokenObj);
            
        mailService.recoverPasswordEmail(user.getEmail(), token);

    }

    public boolean recoverPassword(String token, String newPassword){

        UserDto user = getUserFromToken(token);
      
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