package com.roquito.platform.model;

import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;

/**
 * Created by puran on 2/8/15.
 */
@Entity
public class ObjectId {
    @Id
    private String className;

    // this is the actual ID counter, will
    // be incremented atomically
    private Long counter = 1000L;

    public ObjectId() {
    }

    public ObjectId(String className) {
        this.className = className;
    }

    public Long getCounter() {
        return counter;
    }

    public void setCounter(Long counter) {
        this.counter = counter;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }
}
