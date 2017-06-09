package com.doulala.pooled;

import com.doulala.Entity;
import org.apache.commons.pool2.impl.GenericObjectPool;

/**
 * Created by doulala on 2017/6/8.
 */
class PooledObjectSample {

    static final PooledFactory factory = new PooledFactory(); //创建PooledObjectFactory
    static final GenericObjectPool<Entity> pool = new GenericObjectPool<Entity>(factory); //创建对象池

    public static void main(String[] args) throws Exception {

        for (int i = 0; i < 300; i++) {//模拟300个资源申请
            new Thread(runnable).start();
        }
        System.in.read();
    }

    public static Runnable runnable = () -> {
        Entity entity = null;
        try {
            entity = pool.borrowObject();
            System.out.println(entity.getId());
            Thread.sleep(500);
            //entity=new Entity();//千万不要修改对象地址，如果对entity重新初始化，会导致Returned object not currently part of this pool 错误
            pool.returnObject(entity);//归还对象,
        } catch (Exception e) {
            e.printStackTrace();
        }
    };

}
