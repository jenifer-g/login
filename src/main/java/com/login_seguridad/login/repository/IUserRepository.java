package com.login_seguridad.login.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.login_seguridad.login.models.User;


public interface IUserRepository extends MongoRepository<User, String>, ICustomUserRepository{
    User findByEmail(String email);
    boolean existsByEmailAndEmailVerifiedFalse(String email);
    // boolean existsByEmail(String email);
}
