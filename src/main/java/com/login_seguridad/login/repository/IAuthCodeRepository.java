package com.login_seguridad.login.repository;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.login_seguridad.login.models.AuthCode;

public interface IAuthCodeRepository extends MongoRepository<AuthCode, String>{
    Optional<AuthCode> findByUserIdAndCode(String userId, int Code);
}
