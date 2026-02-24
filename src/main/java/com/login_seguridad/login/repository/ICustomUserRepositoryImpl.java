package com.login_seguridad.login.repository;

import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import com.login_seguridad.login.models.User;
import com.mongodb.client.result.UpdateResult;

@Repository
public class ICustomUserRepositoryImpl implements ICustomUserRepository{

    // final MongoTemplate mongoTemplate;
    final MongoTemplate mongoTemplate;
    
    public ICustomUserRepositoryImpl(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }


    @Override
    public void updateEmailVerified(String email) {
        Query query = new Query(Criteria.where("email").is(email));

        Update update = new Update().set("emailVerified", true);

        UpdateResult res = mongoTemplate.updateFirst(query, update, User.class);

        if(res.getMatchedCount()==0){
            throw new RuntimeException("Usuario no encontrado");
        }
        if(res.getModifiedCount()==0){
            throw new RuntimeException("Error al actualizar el registro, vuelva a verificar su email");
        }

    }

    // @Override
    // public boolean updateEmailVerified(Long id) {
    //     throw new UnsupportedOperationException("Not supported yet.");
    // }
    
}
