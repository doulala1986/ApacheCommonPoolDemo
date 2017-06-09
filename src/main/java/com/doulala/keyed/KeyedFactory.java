package com.doulala.keyed;

import com.doulala.Entity;
import com.doulala.EntityPooledObject;
import org.apache.commons.pool2.KeyedPooledObjectFactory;
import org.apache.commons.pool2.PooledObject;

import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by doulala on 2017/6/9.
 */
public class KeyedFactory implements KeyedPooledObjectFactory<String, Entity> {


    /**
     * PooledObject创建方法
     *
     * @return
     * @throws Exception
     */
    @Override
    public PooledObject makeObject(String key) throws Exception {

        if (key.equals("one")) {

            Entity entity = new Entity();
            entity.setId(1l);
            EntityPooledObject object = new EntityPooledObject(entity);
            System.out.println(object.toString() + "   on makeObject for one");
            return object;
        } else {
            Entity entity = new Entity();
            entity.setId(2l);
            EntityPooledObject object = new EntityPooledObject(entity);
            System.out.println(object.toString() + "   on makeObject for " + key);
            return object;
        }
    }

    /**
     * @param p 待销毁的池对象
     * @throws Exception 池对象销毁方法
     */
    @Override
    public void destroyObject(String key, PooledObject p) throws Exception {
        EntityPooledObject obj = (EntityPooledObject) p;
        Entity entity = obj.getObject();
        entity.clear();

        System.out.println(p.toString() + "   on destroyObject");
    }

    /**
     * 判断资源对象是否有效
     * may be invoked on activated instances to make sure they can be borrowed from the pool.
     * validateObject(org.apache.commons.pool2.PooledObject<T>) may also be used to test an instance being returned to the pool before it is passivated.
     * It will only be invoked on an activated instance.
     *
     * @param p
     * @return
     */
    @Override
    public boolean validateObject(String key, PooledObject p) {
        System.out.println(p.toString() + "   on validateObject");
        return p instanceof EntityPooledObject;
    }

    /**
     * activateObject is invoked on every instance that has been passivated before it is borrowed from the pool.
     * 从资源池中取出已经创建过的池对象之前
     *
     * @param p
     * @throws Exception
     */
    @Override
    public void activateObject(String key, PooledObject p) throws Exception {
        System.out.println(p.toString() + "  borrowed on activateObject for " + key);
        Entity entity = (Entity) p.getObject();
        if (key.equals("one")) {
            entity.setId(1l);
        } else {
            entity.setId(2l);
        }
    }

    /**
     * passivateObject is invoked on every instance when it is returned to the pool.
     * 将资源返还给资源池时，调用此方法。
     *
     * @param p
     * @throws Exception
     */
    @Override
    public void passivateObject(String key, PooledObject p) throws Exception {

        Entity entity = (Entity) p.getObject();
        entity.clear();
        System.out.println(p.toString() + "  on passivateObject for " + key);

    }

}
