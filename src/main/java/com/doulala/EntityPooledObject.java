package com.doulala;

import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.impl.DefaultPooledObject;


/**
 * The type Entity pooled object.
 * 继承了DefaultPooledObject<Entity>
 *
 */
public class EntityPooledObject extends DefaultPooledObject<Entity> {
    /**
     * Create a new instance that wraps the provided object so that the pool can
     * track the state of the pooled object.
     *
     * @param object The object to wrap
     */
    public EntityPooledObject(Entity object) {
        super(object);
    }
}
