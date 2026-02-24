package com.login_seguridad.login.services;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.login_seguridad.login.models.User;
import com.login_seguridad.login.models.UserPrincipal;
import com.login_seguridad.login.repository.IUserRepository;

@Service
public class MyUserDetailsService implements UserDetailsService{ // como obtener los datos

    private IUserRepository userRepository;

    
    public MyUserDetailsService(IUserRepository userRepository) {
        this.userRepository = userRepository;
    }
    // public MyUserDetailsService(){}


    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException { // unico metodo en donde le decimos a spring como obtener la información de un usuario real
        User user = userRepository.findByEmail(email); 
        if(user==null){
            System.out.println("El usuario no existe");
            throw new UsernameNotFoundException("Usuario no encontrado");
        }
        return new UserPrincipal(user); // representacion estandar de un usuario en spring security
    }
    
}
