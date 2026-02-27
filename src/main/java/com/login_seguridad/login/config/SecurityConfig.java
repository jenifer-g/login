package com.login_seguridad.login.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    final UserDetailsService userDetailsService; // para tener user detail service y no default
    final JwtFilter jwtFilter;

    public SecurityConfig(UserDetailsService userDetailsService, JwtFilter jwtFilter){
        this.userDetailsService = userDetailsService;
        this.jwtFilter = jwtFilter;
    }
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws  Exception{

        httpSecurity
            .csrf(customizer->customizer.disable())
            .authorizeHttpRequests(request->request
            .requestMatchers("/signup", "/login", "/verifyEmail/**", "/forgotPassword", "/recoverPassword/**")
            .permitAll() // las unicas rutas que no necesitan autenticacion
            .anyRequest().authenticated()) // las demas sis
            .httpBasic(Customizer.withDefaults())
            .sessionManagement(session->session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class); // añadimos un filtro antes de user pass auth filter
            
        return httpSecurity.build();
    }

    // esto no autentica al momento, si no que RETORNA el motor que spring usara para autenticar cuando llegue la peticio de login
    @Bean //-> le indicamos a spring que lo sig. va a devolver un obj que el tiene que gestionar en el contexto de la app, asi autom spring lo usa en el flujo de seguridad
    public AuthenticationProvider authenticationProvider(){ // obtiene la info para autenticar
        // nuestro userDetailsService es quien sabe como buscar usuarios en la bd
        // el provider se encarga de preguntar a nuestro service por el usuario, comparar y asignar roles en caso de que sean correctos
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider(userDetailsService);
        provider.setPasswordEncoder(new BCryptPasswordEncoder(12));
        // provider.setUserDetailsService(userDetailsService);;
        return provider;
        // hay muchos authentication providers, uno de ellos es dow authentication que es para BD
    }
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws 
     Exception{ 
        return config.getAuthenticationManager();
    }
}