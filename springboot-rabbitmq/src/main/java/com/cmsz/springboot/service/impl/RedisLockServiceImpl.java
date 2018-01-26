package com.cmsz.springboot.service.impl;

import com.cmsz.springboot.service.RedisLockService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import redis.clients.jedis.ShardedJedis;
import redis.clients.jedis.ShardedJedisPool;

import java.util.concurrent.TimeUnit;

/**
 * Created by le on 2018/1/12.
 */
@Service(value = "redisLockService")
public class RedisLockServiceImpl implements RedisLockService {

    private final int TTL=5;

    @Autowired
    private ShardedJedisPool pool;

    @Override
    public boolean tryLock(String key, long timeout) {
        ShardedJedis shardedJedis = pool.getResource();
        if (shardedJedis == null) {
            return Boolean.FALSE;
        }
        long nano = System.nanoTime();
        do{
            long flag=shardedJedis.setnx(key,key);
            if(flag==1){
                shardedJedis.expire(key,TTL);
                return Boolean.TRUE;
            }else {
                /*存在锁*/
                if (timeout == 0) { //取不到锁时,不等待,直接返回.
                    break;
                }
                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }while ((System.nanoTime() - nano)<TimeUnit.NANOSECONDS.toNanos(timeout));//取不到锁时等待,直到timeout
         return Boolean.FALSE;
    }

    @Override
    public boolean unlock(String key) {
        ShardedJedis shardedJedis = pool.getResource();
        if (shardedJedis == null) {
            return Boolean.FALSE;
        }
        shardedJedis.del(key);
        return Boolean.TRUE;
    }


}
