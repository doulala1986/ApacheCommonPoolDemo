package com.doulala.keyed;

import com.doulala.Entity;
import org.apache.commons.pool2.impl.GenericKeyedObjectPool;

/**
 * Created by doulala on 2017/6/9.
 */
public class KeyedObjectPoolSample {
    static final KeyedFactory factory = new KeyedFactory(); //创建PooledObjectFactory
    static final GenericKeyedObjectPool<String, Entity> pool = new GenericKeyedObjectPool(factory); //创建对象池

    public static void main(String[] args) throws Exception {

        for (int i = 0; i < 50; i++) {//模拟300个资源申请
            int finalI = i;
            new Thread(() -> {
                Entity entity = null;
                try {
                    entity = pool.borrowObject("one");
                    System.out.println(finalI + " index of one");
                    Thread.sleep(500);
                    //entity=new Entity();//千万不要修改对象地址，如果对entity重新初始化，会导致Returned object not currently part of this pool 错误
//            pool.returnObject("one"entity);//归还对象,
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }).start();

            new Thread(() -> {
                Entity entity = null;
                try {
                    entity = pool.borrowObject("two");
                    System.out.println(finalI + " index of two");
                    Thread.sleep(500);
                    //entity=new Entity();//千万不要修改对象地址，如果对entity重新初始化，会导致Returned object not currently part of this pool 错误
                    pool.returnObject("two", entity);//归还对象,
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }).start();
        }
        System.in.read();
    }

}
