package com.login_seguridad.login.repository;

public interface  ICustomUserRepository {

    void updateEmailVerified(String email);
    void updatePassword(String id, String newPassword);
    
}
