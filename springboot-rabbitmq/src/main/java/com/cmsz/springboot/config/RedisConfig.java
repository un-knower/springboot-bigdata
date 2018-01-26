package com.cmsz.springboot.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.JedisShardInfo;
import redis.clients.jedis.ShardedJedisPool;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by le on 2018/1/12.
 */
@Configuration
public class RedisConfig {
    @Value("${redis.pool.maxTotal}")
    private int maxTotal;

    @Value("${redis.pool.maxIdle}")
    private int maxIdle;

    @Value("${redis.pool.maxWait}")
    private long maxWaitMillis;

    @Value("${redis.pool.testOnBorrow}")
    private boolean testOnBorrow;

    @Value("${redis.ip}")
    private String redis_host;

    @Value("${redis.port}")
    private String redis_port;


    @Bean(name = "jedisPoolConfig")
    public JedisPoolConfig getJedisPoolConfig() {
        JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
        jedisPoolConfig.setMaxTotal(maxTotal);
        jedisPoolConfig.setMaxIdle(maxIdle);
        jedisPoolConfig.setMaxWaitMillis(maxWaitMillis);
        jedisPoolConfig.setTestOnBorrow(testOnBorrow);
        return jedisPoolConfig;

    }

    @Bean(name = "jedisShardInfo")
    public JedisShardInfo getJedisShardInfo() {
        JedisShardInfo jedisShardInfo = new JedisShardInfo(redis_host,redis_port);
        return jedisShardInfo;
    }

    @Bean(name = "shardedJedisPool")
    public ShardedJedisPool getShardedJedisPool(@Qualifier("jedisPoolConfig") JedisPoolConfig jedisPoolConfig,
                                                @Qualifier("jedisShardInfo") JedisShardInfo jedisShardInfo) {
        List<JedisShardInfo> shards = new ArrayList<>();
        shards.add(jedisShardInfo);

        ShardedJedisPool shardedJedisPool = new ShardedJedisPool(jedisPoolConfig,shards);
        return shardedJedisPool;
    }
}
