package com.roquito.platform.service;

import com.roquito.platform.dao.MongoDB;
import com.roquito.platform.model.User;
import org.bson.types.ObjectId;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.Key;
import org.mongodb.morphia.dao.BasicDAO;

/**
 * Created by puran on 1/19/15.
 */
public class UserService extends BasicDAO<User, String> {

    public UserService() {
        this(MongoDB.getInstance().getDataStore());
    }

    private UserService(Datastore ds) {
        super(ds);
    }

    public String saveUser(User user) {
        if (user == null) {
            return null;
        }
        Key<User> userKey = this.save(user);
        ObjectId userId = (ObjectId) userKey.getId();

        return userId.toString();
    }

    public User findByEmail(String email) {
        return this.findOne("email", email);
    }

    public User findByUserId(String userId) {
        return this.findOne("userId", userId);
    }
    
    public String getNextUserId() {
        Long nextId = MongoDB.getInstance().generateNextId("com.thirtytwoskills.model.User");
        return nextId.toString();
    }
}
