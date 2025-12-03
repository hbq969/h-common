package com.github.hbq969.code.common.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "operlog")
@Data
public class LogProperties {
    /**
     * 是否启用日志采集
     */
    private boolean enabled = false;

    /**
     * 日志存储策略，默认:mysql
     */
    private String policy = "mysql";

    /**
     * 日志存储默认表名，在policy为mysql时有效
     */
    private String tableName = "log_hbq969";

    /**
     * 日志存储默认表前缀，在policy为mysql时有效
     */
    private String tablePrefix;

    /**
     * 日志存储默认表后缀，在policy为mysql时有效
     */
    private String tableSuffix;

    /**
     * 是否使用缺省的采集实现
     */
    private boolean useDefaultCollectPolicy = false;

    /**
     * 日志队列缓存容量上限
     */
    private int queueCapacity = 5000;

    /**
     * 日志批处理大小
     */
    private int batchSize = 50;

    /**
     * 日志存储超时时间，单位毫秒
     */
    private long batchTimeoutMills = 5000;

    /**
     * 日志处理线程数
     */
    private int threadNum = 1;

    /**
     * 日志清理配置
     */
    private Clean clean = new Clean();

    @Data
    public static class Clean {
        private boolean enabled = false;
        /**
         * 清理日志周期配置，默认每天的凌晨1点
         */
        private String cron = "0 0 1 * * ?";
        /**
         * 需要清理多个月前的操作日志
         */
        private int monthNumForHistory = 12;
    }
}
