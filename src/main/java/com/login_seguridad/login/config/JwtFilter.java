package com.login_seguridad.login.config;

import java.io.IOException;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.WebUtils;

import com.login_seguridad.login.services.JWTService;
import com.login_seguridad.login.services.MyUserDetailsService;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

// para cada request, queremos que este filtro se ejecute una sola vez

@Component
public class JwtFilter extends OncePerRequestFilter{
    final JWTService jwtService;
    final MyUserDetailsService userDetails; // instancia administrada por spring que recibe las dependencias
    public JwtFilter(JWTService jwtService, MyUserDetailsService userDetails) {
        this.jwtService = jwtService;
        this.userDetails = userDetails;
    }


    // filtro que srping nos obliga a implementar cuando creamos nuestro propio filtro
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        // String authHeader = request.getHeader("Authorization");

        Cookie cookieToken = WebUtils.getCookie(request, "token");


        String token = null;
        String name = null;


        if(cookieToken!=null){
            token = cookieToken.getValue();
            name = jwtService.extractSubject(token);
        }

        if(name!=null && SecurityContextHolder.getContext().getAuthentication() == null){
            UserDetails userInfo = this.userDetails.loadUserByUsername(name);

            // valida el token contra los datos del usuario
            if(jwtService.validateToken(token, userInfo)){
                // crea un obj de autenticacion que el spring s enctiende, representa al usuario dentro del sistema de seguridad
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(userInfo, null, userInfo.getAuthorities());
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }
        filterChain.doFilter(request, response);

    
        filterChain.doFilter(request, response);
    }
    
}
