package com.cmsz.springboot.service;

/**
 * Created by le on 2018/1/12.
 */
public interface RedisLockService {

    boolean tryLock(String key, long timeout);

    boolean unlock(String key);

}
