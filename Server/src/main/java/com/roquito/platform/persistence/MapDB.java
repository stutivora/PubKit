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

import java.io.File;
import java.util.concurrent.TimeUnit;

import javax.annotation.PreDestroy;

import org.mapdb.DB;
import org.mapdb.DBMaker;
import org.mapdb.HTreeMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import com.roquito.web.exception.RoquitoServerException;

/**
 * Created by puran
 */
@Repository
public class MapDB {
    private static final Logger LOG = LoggerFactory.getLogger(MapDB.class);
    
    /* MAP DB reference */
    private DB internalDB;

    /* Access token store */
    private HTreeMap<String, String> tokenStore = null;
    
    @Autowired
    public MapDB(@Value("${mapdb.filepath}") String filePath, @Value("${mapdb.encryptedPassword}") String password) {
	initMapDB(filePath, password);
    }
    
    @PreDestroy
    public void close() {
        this.internalDB.commit();
        this.internalDB.close();
        LOG.debug("closed disk storage");
    }

    public void initMapDB(String mapdbFilePath, String encryptedMapdbPassword) {
	LOG.info("Initializing MapDB using mapdbFilePath {" + mapdbFilePath + "}");
	if (mapdbFilePath == null || encryptedMapdbPassword == null) {
	    throw new RoquitoServerException("Missing mapdb configuration.");
	}
	// configure and open database using builder pattern.
	// all options are available with code auto-completion.
	internalDB = DBMaker.newFileDB(new File(mapdbFilePath)).closeOnJvmShutdown().transactionDisable()
		.mmapFileEnableIfSupported().encryptionEnable(encryptedMapdbPassword).make();

	tokenStore = internalDB.createHashMap("tokenStore").expireAfterWrite(2, TimeUnit.HOURS).makeOrGet();
    }

    public boolean saveAccessToken(String clientId, String accessToken) {
	boolean success = false;
	if (!tokenStore.containsKey(accessToken)) {
	    tokenStore.put(accessToken, clientId);
	    tokenStore.put(clientId, accessToken);
	    
	    internalDB.commit();
	    success = true;
	}
	return success;
    }
    
    public void invalidateSessionToken(String clientId) {
	if (tokenStore.containsKey(clientId)) {
	    String accessToken = tokenStore.get(clientId);
	    tokenStore.remove(accessToken);
	    tokenStore.remove(clientId);
	    
	    internalDB.commit();
	}
    }

    public boolean isAccessTokenValid(String accessToken) {
	return tokenStore.containsKey(accessToken);
    }
    
}


