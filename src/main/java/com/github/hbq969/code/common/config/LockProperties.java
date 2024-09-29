package com.github.hbq969.code.common.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

/**
 * @author : hbq969@gmail.com
 * @description : 锁相关配置
 * @createTime : 2024/10/13 11:31
 */
@ConfigurationProperties(prefix = "lock")
@Data
public class LockProperties {

    /**
     * 是否启用，默认 false
     */
    private boolean enabled = false;


    /**
     * 分布式锁类型，默认 redis
     */
    private String way = "redis";

    private Redis redis = new Redis();

    @Data
    public static class Redis {

        /**
         * redis密码
         */
        private String password;

        /**
         * redis执行命令超时时间，单位：5秒
         */
        private int connectTimeout = 5;

        /**
         * 单机版redis配置
         */
        private Standard standard = new Standard();

        /**
         * 哨兵模式redis配置
         */
        private Sentinel sentinel = new Sentinel();

        /**
         * 集群模式redis配置
         */
        private Cluster cluster = new Cluster();

        /**
         * 连接池配置
         */
        private Lettuce lettuce = new Lettuce();

        @Data
        public static class Standard {
            /**
             * 是否启用，默认 false
             */
            private boolean enabled = false;
            /**
             * ip地址
             */
            private String host = "localhost";

            /**
             * 端口
             */
            private int port = 6379;
        }

        @Data
        public static class Sentinel {
            /**
             * 是否启用，默认 false
             */
            private boolean enabled = false;
            /**
             * 哨兵中定义的redis主节点名称
             */
            private String master = "mymaster";

            /**
             * 哨兵节点信息, [ip1:port,ip2:port]
             */
            private String nodes = "localhost:16379";
        }

        @Data
        public static class Cluster {
            /**
             * 是否启用，默认 false
             */
            private boolean enabled = false;
            /**
             * 集群地址
             */
            private List<Node> nodes;

            @Data
            public static class Node {
                private String ip = "localhost";
                private int port = 6379;
            }
        }

        @Data
        public static class Lettuce {

            private Pool pool = new Pool();

            @Data
            public static class Pool {

                /**
                 * 连接超时时间，秒
                 */
                private long timeout = 5;
                /**
                 * 最大连接数
                 */
                private int maxActive = 20;
                /**
                 * 最大空闲连接数
                 */
                private int maxIdle = (int) (maxActive * 0.8f);
                /**
                 * 最小空闲连接数
                 */
                private int minIdle = (int) (maxActive * 0.2f);
                /**
                 * 最大等待时间，毫秒
                 */
                private int maxWait = 60000;
            }
        }
    }
}
