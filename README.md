# Apache Common Pool

## 类图

![](http://swiftlet.net/wp-content/themes/swiftlet/page-images/common-pool/objectpool-class.jpeg?_=5120623)


## 三种对象池

####   ObjectPool

    标准的对象池, 默认16个池对象，可以通过config进行配置

 ####   KeyedObjectPool
 
    基于Key-Value的对象池，可以通过key获取不同的对象(在Factory内进行区分)。默认每个key
    对应16个池对象，可以通过config进行配置。
 
 ####   SoftReferenceObjectPool
 
    基于软引用的对象池。
  
  
### 对象池的基本方法
  
 - **borrowObject()**;
     
   向对象池内申请一个池对象，如果池对象已经全部用完，则产生阻塞等待，可以使用 **borrowObject(long borrowMaxWaitMillis)** 方法设置等待超时时间。
  
 - **returnObject(T object)**
  
   对象在使用结束后，一种处理是使用return方法把对象归还到对象池。这里需要注意的是，归还的对象的引用要和borrow时的对象的引用保持一致，否则会抛出异常。

 - **invalidateObject(T object)**
 
  对象使用结束后，另一种处理是调用该把这个对象作废掉。

 - **clear()**
  清除所有状态是IDLE的对象


---

## 对象工厂

 - PooledObjectFactory
 - KeyedPooledObjectFactory

   这两个工厂类区别不大，分别对应ObjectPool(SoftReferenceObjectPook) 与 KeyedObjectPool
   
   
### 关键方法

  ```java
  /**
     * PooledObject创建方法
     */
    @Override
    public PooledObject makeObject() throws Exception

    /**
     * 池对象销毁方法
     */
    @Override
    public void destroyObject(PooledObject p) throws Exception

    /**
     * 判断资源对象是否有效
     * may be invoked on activated instances to make sure they can be borrowed from the pool.
     * validateObject(org.apache.commons.pool2.PooledObject<T>) may also be used to test an instance being returned to the pool before it is passivated.
     * It will only be invoked on an activated instance.
     */
    @Override
    public boolean validateObject(PooledObject p)

    /**
     * activateObject is invoked on every instance that has been passivated before it is borrowed from the pool.
     * 从资源池中取出已经创建过的池对象之前
     */
    @Override
    public void activateObject(PooledObject p)

    /**
     * passivateObject is invoked on every instance when it is returned to the pool.
     * 将资源返还给资源池时，调用此方法。
     */
    @Override
    public void passivateObject(PooledObject p) throws Exception 
  ```
  
  --- 
  
  ## DEMO
  
  
  Factory:
  ```java
  
  package com.doulala.pooled;

import com.doulala.Entity;
import com.doulala.EntityPooledObject;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.PooledObjectFactory;

import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by doulala on 2017/6/8.
 * <p>
 * 线程池工厂类
 */
public class PooledFactory implements PooledObjectFactory {

    AtomicLong generator = new AtomicLong(0);


    /**
     * PooledObject创建方法
     *
     * @return
     * @throws Exception
     */
    @Override
    public PooledObject makeObject() throws Exception {

        Entity entity = new Entity();
        entity.setId(generator.addAndGet(1));
        EntityPooledObject object = new EntityPooledObject(entity);
        System.out.println(object.toString() + "   on makeObject");
        return object;
    }

    /**
     * @param p 待销毁的池对象
     * @throws Exception 池对象销毁方法
     */
    @Override
    public void destroyObject(PooledObject p) throws Exception {
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
    public boolean validateObject(PooledObject p) {
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
    public void activateObject(PooledObject p) throws Exception {
        System.out.println(p.toString() + "  borrowed on activateObject");
        Entity entity = (Entity) p.getObject();
        entity.setId(generator.addAndGet(1));
    }

    /**
     * passivateObject is invoked on every instance when it is returned to the pool.
     * 将资源返还给资源池时，调用此方法。
     *
     * @param p
     * @throws Exception
     */
    @Override
    public void passivateObject(PooledObject p) throws Exception {

        Entity entity = (Entity) p.getObject();
        entity.clear();
        System.out.println(p.toString() + "  on passivateObject");

    }
}

  ```
  
  Main.java
  
  
  ```java
  
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

  
  ```
