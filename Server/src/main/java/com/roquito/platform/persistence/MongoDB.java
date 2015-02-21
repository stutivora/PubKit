/* Copyright (c) 2015 32skills Inc.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.roquito.platform.persistence;

import java.net.UnknownHostException;

import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.Morphia;
import org.mongodb.morphia.query.Query;
import org.mongodb.morphia.query.UpdateOperations;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.mongodb.MongoClient;
import com.roquito.RoquitoConfig;
import com.roquito.platform.model.Application;
import com.roquito.platform.model.ApplicationConfig;
import com.roquito.platform.model.ApplicationUser;
import com.roquito.platform.model.ObjectId;
import com.roquito.platform.model.User;

/**
 * Created by puran
 */
@Repository
public class MongoDB {
    
    private static final Logger LOG = LoggerFactory.getLogger(MongoDB.class);
    /**
     * MongoDB data store *
     */
    private Datastore datastore = null;

    @Autowired
    public MongoDB(RoquitoConfig config) {
	LOG.info("Initializing mongo db connection at host {" + config.getMongoHost() + "}");
	initMongoDBConnection(config);
    }

    public Datastore getDataStore() {
	return datastore;
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

    private void initMongoDBConnection(RoquitoConfig config) {
	MongoClient mongoClient = null;
	try {
	    LOG.info("Connecting to MongoDB server");
	    mongoClient = new MongoClient(config.getMongoHost(), config.getMongoPort());
	    Morphia morphia = new Morphia();

	    morphia.map(User.class);
	    morphia.map(Application.class);
	    morphia.map(ApplicationConfig.class);
	    morphia.map(ApplicationUser.class);

	    datastore = morphia.createDatastore(mongoClient, config.getMongoDatabase());
	    datastore.ensureIndexes();

	    LOG.info("MongoDB connection setup successful");

	} catch (UnknownHostException e) {
	    LOG.error("Error connecting to MongoDB database", e);
	} catch (Exception e) {
	    LOG.error("Error connecting to MongoDB database", e);
	}
    }
}
