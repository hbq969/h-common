package com.github.hbq969.code.common.lock.spi.redis;

import com.github.hbq969.code.common.config.LockProperties;
import com.github.hbq969.code.common.decorde.OptionalFacade;
import com.github.hbq969.code.common.decorde.OptionalFacadeAware;
import com.github.hbq969.code.common.lock.LockProvider;
import com.github.hbq969.code.common.lock.spi.LockProviderFacade;
import com.github.hbq969.code.common.utils.GsonUtils;
import com.github.hbq969.code.common.utils.StrUtils;
import com.google.common.base.Splitter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.*;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettucePoolingClientConfiguration;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.types.Expiration;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.nio.charset.Charset;
import java.time.Duration;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author : hbq969@gmail.com
 * @description : 分布式锁redis实现
 * @createTime : 2023/8/25 11:12
 */
@Slf4j
public class LockRedisProvider implements LockProvider, OptionalFacadeAware<String, LockProvider> {

    public static final String UNLOCK_LUA;

    @Autowired
    private LockProviderFacade facade;

    @Autowired
    private LockProperties conf;

    private volatile RedisTemplate<Object, Object> redis;

    /**
     * 释放锁脚本，原子操作
     */
    static {
        StringBuilder sb = new StringBuilder();
        sb.append("if redis.call(\"get\",KEYS[1]) == ARGV[1] ");
        sb.append("then ");
        sb.append("    return redis.call(\"del\",KEYS[1]) ");
        sb.append("else ");
        sb.append("    return 0 ");
        sb.append("end ");
        UNLOCK_LUA = sb.toString();
    }

    @Override
    public OptionalFacade<String, LockProvider> getOptionalFacade() {
        return this.facade;
    }

    @Override
    public String getKey() {
        return "redis";
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        if (conf.getRedis().getStandard().isEnabled()) {
            initializeStandaloneRedis();
        } else if (conf.getRedis().getSentinel().isEnabled()) {
            initializeSentinelRedis();
        } else if (conf.getRedis().getCluster().isEnabled()) {
            initializeClusterRedis();
        } else {
            throw new UnsupportedOperationException("未配置redis, 不能启用分布式锁特性");
        }
        OptionalFacadeAware.super.afterPropertiesSet();
    }

    @Override
    public boolean tryLock(String lockKey, String requestId, long expire, TimeUnit unit) {
        checkInitializeRedis();
        try {
            RedisCallback<Boolean> callback = (connection) -> connection
                    .set(lockKey.getBytes(Charset.forName("UTF-8")),
                            requestId.getBytes(Charset.forName("UTF-8")),
                            Expiration.milliseconds(unit.toMillis(expire))
                            , RedisStringCommands.SetOption.SET_IF_ABSENT);
            return redis.execute(callback);
        } catch (Exception e) {
            String msg = String.format(
                    "尝试获取分布式锁异常(lockKey: %s, requestId: %s, expire: %s, unit: %s): %s",
                    lockKey, requestId, expire, unit, e.getMessage());
            log.error(msg);
            if (e instanceof RuntimeException) {
                throw new RuntimeException(e.getCause());
            }
        }
        return false;
    }

    @Override
    public boolean getLock(String lockKey, String requestId, long expire, long checkTime,
                           TimeUnit unit) {
        checkInitializeRedis();
        long allTime = 0;
        while (!tryLock(lockKey, requestId, expire, unit)) {
            try {
                unit.sleep(checkTime);
                allTime += checkTime;
                if (allTime >= expire) {
                    return false;
                }
            } catch (InterruptedException e) {
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean getLock(String lockKey, String requestId, long expire, TimeUnit unit) {
        checkInitializeRedis();
        long expireSecs = TimeUnit.SECONDS.convert(expire, unit);
        long allTime = 0;
        while (!tryLock(lockKey, requestId, expire, unit)) {
            try {
                TimeUnit.SECONDS.sleep(1);
                if ((++allTime) >= expireSecs) {
                    return false;
                }
            } catch (InterruptedException e) {
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean releaseLock(String lockKey, String requestId) {
        checkInitializeRedis();
        try {
            RedisCallback<Boolean> callback = (connection) -> connection
                    .eval(UNLOCK_LUA.getBytes(), ReturnType.BOOLEAN, 1,
                            lockKey.getBytes(Charset.forName("UTF-8")),
                            requestId.getBytes(Charset.forName("UTF-8")));
            return redis.execute(callback);
        } catch (Exception e) {
            String msg = String.format("释放分布式锁异常(lockKey: %s, requestId: %s): %s",
                    lockKey, requestId, e.getMessage());
            log.error(msg);
        }
        return false;
    }

    @Override
    public String get(String lockKey) {
        checkInitializeRedis();
        try {
            RedisCallback<String> callback = (connection) -> new String(
                    connection.get(lockKey.getBytes()), Charset.forName("UTF-8"));
            return redis.execute(callback);
        } catch (Exception e) {
            String msg = String.format("获取分布式锁值异常(lockKey: %s): %s", lockKey, e.getMessage());
            log.error(msg);
            if (e instanceof RuntimeException) {
                throw new RuntimeException(e.getCause());
            }
        }
        return null;
    }

    @Override
    public RedisTemplate<Object, Object> getRedisTemplate() {
        return this.redis;
    }

    private void checkInitializeRedis() {
        if (redis == null) {
            throw new UnsupportedOperationException("未启用redis自动装配,不支持分布式锁");
        }
    }

    private void initializeStandaloneRedis() {
        try {
            LettuceConnectionFactory factory = null;
            GenericObjectPoolConfig genericObjectPoolConfig = initializeGenericObjectPoolConfig();

            long readTimeout = conf.getRedis().getConnectTimeout();
            LettucePoolingClientConfiguration lettuceClientConfiguration = LettucePoolingClientConfiguration.builder()
                    .commandTimeout(Duration.ofSeconds(readTimeout)).poolConfig(genericObjectPoolConfig)
                    .build();

            String ip = conf.getRedis().getStandard().getHost();
            int port = conf.getRedis().getStandard().getPort();
            RedisStandaloneConfiguration redisStandaloneConfiguration = new RedisStandaloneConfiguration(
                    ip, port);
            String pwd = conf.getRedis().getPassword();
            if (StrUtils.strNotEmpty(pwd)) {
                redisStandaloneConfiguration.setPassword(RedisPassword.of(pwd.toCharArray()));
            }
            log.info("初始化单点redis, 命令读超时: {} 秒, reids节点ip: {}, redis节点端口: {}, 密码: {}",
                    readTimeout, ip, port, StrUtils.desensitive(pwd, 1));
            factory = initializeLettuceConnectionFactory(lettuceClientConfiguration,
                    redisStandaloneConfiguration);

            initializeRedisTemplate(factory);
        } catch (Throwable e) {
            log.error("初始化单点redis异常", e);
        }
    }

    private void initializeSentinelRedis() {
        try {
            LettuceConnectionFactory factory = null;
            GenericObjectPoolConfig genericObjectPoolConfig = initializeGenericObjectPoolConfig();

            long readTimeout = conf.getRedis().getConnectTimeout();
            LettucePoolingClientConfiguration lettuceClientConfiguration = LettucePoolingClientConfiguration.builder()
                    .commandTimeout(Duration.ofSeconds(readTimeout)).poolConfig(genericObjectPoolConfig)
                    .build();

            RedisSentinelConfiguration redisSentinelConfiguration = new RedisSentinelConfiguration();
            String master = conf.getRedis().getSentinel().getMaster();
            redisSentinelConfiguration.setMaster(master);
            String servers = conf.getRedis().getSentinel().getNodes();
            List<RedisNode> nodes = getRedisNodes(servers);
            for (RedisNode node : nodes) {
                redisSentinelConfiguration.addSentinel(node);
            }
            String pwd = conf.getRedis().getPassword();
            if (StrUtils.strNotEmpty(pwd)) {
                redisSentinelConfiguration.setPassword(RedisPassword.of(pwd.toCharArray()));
            }
            log.info("初始化哨兵redis, 命令读超时: {} 秒, master: {}, 哨兵集群列表: {}, 密码: {}",
                    readTimeout, master, GsonUtils.toJson(nodes), StrUtils.desensitive(pwd, 1));
            factory = initializeLettuceConnectionFactory(lettuceClientConfiguration,
                    redisSentinelConfiguration);

            initializeRedisTemplate(factory);
        } catch (Throwable e) {
            log.error("初始化哨兵模式redis异常", e);
        }
    }

    private GenericObjectPoolConfig initializeGenericObjectPoolConfig() {
        GenericObjectPoolConfig genericObjectPoolConfig = new GenericObjectPoolConfig();
        genericObjectPoolConfig.setMaxIdle(conf.getRedis().getLettuce().getPool().getMaxIdle());
        genericObjectPoolConfig.setMinIdle(conf.getRedis().getLettuce().getPool().getMinIdle());
        genericObjectPoolConfig.setMaxTotal(conf.getRedis().getLettuce().getPool().getMaxActive());
        genericObjectPoolConfig.setMaxWaitMillis(conf.getRedis().getLettuce().getPool().getMaxWait());
        return genericObjectPoolConfig;
    }

    private void initializeClusterRedis() {
        try {
            LettuceConnectionFactory factory = null;
            GenericObjectPoolConfig genericObjectPoolConfig = initializeGenericObjectPoolConfig();

            long readTimeout = conf.getRedis().getConnectTimeout();
            LettucePoolingClientConfiguration lettuceClientConfiguration = LettucePoolingClientConfiguration.builder()
                    .commandTimeout(Duration.ofSeconds(readTimeout)).poolConfig(genericObjectPoolConfig)
                    .build();

            RedisClusterConfiguration redisClusterConfiguration = new RedisClusterConfiguration();
            List<LockProperties.Redis.Cluster.Node> nodes = conf.getRedis().getCluster().getNodes();
            for (LockProperties.Redis.Cluster.Node node : nodes) {
                RedisNode rn = new RedisNode(node.getIp(), node.getPort());
                redisClusterConfiguration.addClusterNode(rn);
            }
            String pwd = conf.getRedis().getPassword();
            if (StrUtils.strNotEmpty(pwd)) {
                redisClusterConfiguration.setPassword(RedisPassword.of(pwd.toCharArray()));
            }
            log.info("初始化集群redis, 命令读超时: {} 秒, 集群列表: {}, 密码: {}", readTimeout,
                    GsonUtils.toJson(nodes), StrUtils.desensitive(pwd, 1));
            factory = initializeLettuceConnectionFactory(lettuceClientConfiguration,
                    redisClusterConfiguration);

            initializeRedisTemplate(factory);
        } catch (Throwable e) {
            log.error("初始化集群模式redis异常", e);
        }
    }

    private LettuceConnectionFactory initializeLettuceConnectionFactory(
            LettucePoolingClientConfiguration lettuceClientConfiguration,
            RedisConfiguration redisClusterConfiguration) {
        LettuceConnectionFactory factory;
        factory = new LettuceConnectionFactory(redisClusterConfiguration, lettuceClientConfiguration);
        factory.afterPropertiesSet();
        return factory;
    }

    private List<RedisNode> getRedisNodes(String servers) {
        return Splitter.on(",").splitToList(servers).stream().map(server -> {
            int lastIndex = server.lastIndexOf(":");
            String ip = server.substring(0, lastIndex);
            int port = Integer.valueOf(server.substring(lastIndex + 1));
            RedisNode node = new RedisNode(ip, port);
            return node;
        }).collect(Collectors.toList());
    }

    private void initializeRedisTemplate(LettuceConnectionFactory factory) {
        StringRedisSerializer serializer = new StringRedisSerializer();
        redis = new RedisTemplate<>();
        redis.setKeySerializer(serializer);
        redis.setValueSerializer(serializer);
        redis.setHashKeySerializer(serializer);
        redis.setHashValueSerializer(serializer);
        redis.setConnectionFactory(factory);
        redis.afterPropertiesSet();
    }
}
