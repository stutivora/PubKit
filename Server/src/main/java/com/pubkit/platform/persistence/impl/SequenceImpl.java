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
package com.pubkit.platform.persistence.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import com.pubkit.platform.model.Sequence;
import com.pubkit.platform.persistence.SequenceDao;
/**
 * Created by puran
 */
@Repository
public class SequenceImpl implements SequenceDao {

    @Autowired
    private MongoTemplate mongoTemplate;
    
    @Override
    public long getNextSequenceId(String collectionName) {
        return increaseCounter(collectionName);
    }

    private long increaseCounter(String className){
        Query query = new Query(Criteria.where("_id").is(className));
        Update update = new Update().inc("sequence", 1);
        
        FindAndModifyOptions options = FindAndModifyOptions.options();
        Sequence sequence = mongoTemplate.findAndModify(query, update, options.returnNew(true),  Sequence.class); 
        if (sequence == null) {
            sequence = new Sequence();
            sequence.setId(className);
            sequence.setSequence(1000l);
            
            mongoTemplate.save(sequence);
        }
        return sequence.getSequence();
    }
}
 