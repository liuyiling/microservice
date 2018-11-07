package com.liuyiling.microservice.core.config;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.Data;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.client.RedisConnectionException;
import org.redisson.codec.JsonJacksonCodec;
import org.redisson.config.ClusterServersConfig;
import org.redisson.config.Config;
import org.redisson.config.ReadMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * @author liuyiling
 * @date on 2018/11/7
 */
@Configuration
@ConfigurationProperties(prefix = "spring.redisson")
@EnableCaching
@Data
public class RedisConfig extends CachingConfigurerSupport {

    public static Logger logger = LoggerFactory.getLogger(RedisConfig.class);

    /**
     * 连接超时
     */
    private int connectTimeout;

    /**
     * 等待节点回复命令的时间。该时间从命令发送成功时开始计时
     */
    private int timeout;

    /**
     * 默认值： SLAVE（只在从服务节点里读取）设置读取操作选择节点的模式。 可用值为：
     * SLAVE - 只在从服务节点里读取。
     * MASTER - 只在主服务节点里读取。
     * MASTER_SLAVE - 在主从服务节点里都可以读取
     */
    private String readMode;

    /**
     * 密码
     */
    private String password;

    /**
     * 命令失败重试次数 默认值：3
     */
    private String retryAttempts;

    /**
     * 客户端名称
     */
    private String clientName;

    /**
     * 空闲连接超时关闭的时间大小
     */
    private String idleConnectionTimeout;

    /**
     * 最大连接数
     */
    private int maxConnectionSize;

    /**
     * 与master保持的最小空闲连接数
     */
    private int masterConnectionMinimumIdleSize;

    /**
     * 与slave保持的最小空闲连接数
     */
    private int slaveConnectionMinimumIdleSize;

    /**
     * 集群节点
     */
    private List<String> nodes;


    @Bean
    public RedissonClient initRedissonClient() {
        Config config = new Config();

        /**
         * 分布式锁的失效时间
         */
        config.setLockWatchdogTimeout(20000);

        /**
         * 序列化配置
         */
        JsonJacksonCodec codec = JsonJacksonCodec.INSTANCE;
        ObjectMapper objectMapper = codec.getObjectMapper();
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
                .configure(DeserializationFeature.ADJUST_DATES_TO_CONTEXT_TIME_ZONE, false)
                .registerModule(new JavaTimeModule());
        config.setCodec(codec);

        /**
         * 集群配置
         */
        ClusterServersConfig serversConfig = config.useClusterServers();
        if (!(getPassword() == null || getPassword().equals(""))) {
            serversConfig.setPassword(password);
        }
        serversConfig.setReadMode(ReadMode.MASTER_SLAVE);

        serversConfig.setMasterConnectionMinimumIdleSize(masterConnectionMinimumIdleSize);
        serversConfig.setSlaveConnectionMinimumIdleSize(slaveConnectionMinimumIdleSize);
        serversConfig.setSubscriptionConnectionMinimumIdleSize(5);
        serversConfig.setScanInterval(3000);
        serversConfig.setTimeout(timeout);
        serversConfig.setConnectTimeout(connectTimeout);
        serversConfig.setClientName(clientName);
        serversConfig.setSlaveConnectionPoolSize(maxConnectionSize);
        serversConfig.setMasterConnectionPoolSize(maxConnectionSize);

        /**
         * 加入集群节点
         */
        nodes.forEach((node) -> serversConfig.addNodeAddress(node));

        try {
            return Redisson.create(config);
        } catch (RedisConnectionException e) {
            logger.error("redis初始化异常", e);
            return null;
        }
    }
}
