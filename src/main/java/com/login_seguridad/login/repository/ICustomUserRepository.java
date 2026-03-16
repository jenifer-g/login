package com.login_seguridad.login.repository;

public interface  ICustomUserRepository {

    boolean updateEmailVerified(String email);
    void updatePassword(String id, String newPassword);
    
}
