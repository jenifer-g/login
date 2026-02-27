package com.login_seguridad.login.repository;

import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import com.login_seguridad.login.models.User;
import com.mongodb.client.result.UpdateResult;
// import com.mongodb.client.result.UpdateResult;

@Repository
public class ICustomUserRepositoryImpl implements ICustomUserRepository{

    // final MongoTemplate mongoTemplate;
    final MongoTemplate mongoTemplate;
    
    public ICustomUserRepositoryImpl(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }


    @Override
    public boolean updateEmailVerified(String email) {
        Query query = new Query(Criteria.where("email").is(email));

        Update update = new Update().set("emailVerified", true);

        UpdateResult res = mongoTemplate.updateFirst(query, update, User.class);

        if(res.getMatchedCount()==0){
            throw new RuntimeException("No se encontró el email");
        }
        
        return true;
    }

    @Override
    public void updatePassword(String id, String newPassword){
        Query query = new Query(Criteria.where("_id").is(id));
        Update update = new Update().set("password", newPassword);

        UpdateResult res = mongoTemplate.updateFirst(query, update, User.class);

        if(res.getMatchedCount()==0){
            throw new RuntimeException("No se encontró el email");
        }
        
        if(res.getModifiedCount()==0){
            throw new RuntimeException("Hubo un error al verificar tu correo");
        }

        mongoTemplate.updateFirst(query, update, User.class);
    }

}
