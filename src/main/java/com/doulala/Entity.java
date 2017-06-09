package com.doulala;

/**
 * Created by doulala on 2017/6/8.
 */
public class Entity {


    public Entity() {
    }

    Long id;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void clear() {
        setId(null);

    }


}
