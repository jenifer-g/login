package com.login_seguridad.login.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.login_seguridad.login.models.TokenUser;

public interface ITokenRepository extends MongoRepository<TokenUser, String> {
    TokenUser findByTokenAndUsedFalse(String userId);
}
