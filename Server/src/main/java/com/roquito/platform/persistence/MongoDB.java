package com.roquito.platform.persistence;

import com.roquito.platform.model.*;
import com.mongodb.MongoClient;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.Morphia;
import org.mongodb.morphia.query.Query;
import org.mongodb.morphia.query.UpdateOperations;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.UnknownHostException;

/**
 * Created by puran on 1/19/15.
 */
public final class MongoDB {
    private static final MongoDB mongoDB = new MongoDB();
    private static final Logger LOG = LoggerFactory.getLogger(MongoDB.class);
    private static final String DEFAULT_HOST = "104.155.200.250";
    private static final String DEFAULT_DB = "roquitodb";
    private static final int DEFAULT_PORT = 27017;

    /**
     * MongoDB data store *
     */
    private Datastore datastore = null;

    public static MongoDB getInstance() {
        return mongoDB;
    }

    public Datastore getDataStore() {
        return datastore;
    }

    public void initMongoDBConnection(boolean test) {
        MongoClient mongoClient = null;
        try {
            LOG.info("Connecting to MongoDB server");
            mongoClient = new MongoClient(DEFAULT_HOST, DEFAULT_PORT);
            Morphia morphia = new Morphia();

            morphia.map(User.class);
            morphia.map(Application.class);
            morphia.map(ApplicationConfig.class);
            morphia.map(ApplicationUser.class);

            String dbName = DEFAULT_DB;
            if (test) {
                dbName = "roquitotestdb";
                mongoClient.dropDatabase(dbName);
            }
            datastore = morphia.createDatastore(mongoClient, dbName);
            datastore.ensureIndexes();

            LOG.info("MongoDB connection setup successful");

        } catch (UnknownHostException e) {
            LOG.error("Error connecting to MongoDB database", e);
        } catch (Exception e) {
            LOG.error("Error connecting to MongoDB database", e);
        }
    }

    public Long generateNextId(String collectionName) {
        // find any existing counters for the type
        Query<ObjectId> q = this.datastore.find(ObjectId.class, "_id", collectionName);
        // create an update operation which increments the counter
        UpdateOperations<ObjectId> update = this.datastore.createUpdateOperations(ObjectId.class).inc("counter");
        // execute on server, if not found null is return,
        // else the counter is incremented atomically
        ObjectId counter = this.datastore.findAndModify(q, update);
        if (counter == null) {
            // so just create one
            counter = new ObjectId(collectionName);
            this.datastore.save(counter);
        }
        // return new id
        return counter.getCounter();
    }
}
