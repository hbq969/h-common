package com.github.hbq969.code.common.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Objects;

/**
 * @author : hbq969@gmail.com
 * @description : spring上下文相关配置项
 * @createTime : 2024/10/13 11:03
 */
@ConfigurationProperties(prefix = "spring")
@Data
public class SpringContextProperties {

    private Cache cache = new Cache();
    private DataSource datasource = new DataSource();

    @Data
    public static class Cache {

        private Ext ext = new Ext();

        @Data
        public static class Ext {
            /**
             * 是否启用，默认 true
             */
            private boolean enabled = true;

            /**
             * 缓存清理配置
             */
            private Clean clean = new Clean();

            /**
             * 默认缓存扩展配置
             */
            private LinkedHashMap juc = new LinkedHashMap();

            /**
             * guava缓存扩展配置
             */
            private Guava guava = new Guava();

            /**
             * ehcache缓存扩展配置
             */
            private Ehcache ehcache = new Ehcache();

            /**
             * Caffeine缓存扩展配置
             */
            private Caffeine caffeine = new Caffeine();

            public boolean useJuc() {
                return Objects.nonNull(juc) && juc.isEnabled();
            }

            public boolean useGuava() {
                return Objects.nonNull(guava) && guava.isEnabled();
            }

            public boolean useEhcache() {
                return Objects.nonNull(ehcache) && ehcache.isEnabled();
            }

            public boolean useCaffeine() {
                return Objects.nonNull(caffeine) && caffeine.isEnabled();
            }

            @Data
            public static class Guava {
                /**
                 * 是否启用，默认 false
                 */
                private boolean enabled = false;

                /**
                 * 缓存最大容量
                 */
                private int maxCapacity = 5000;

                /**
                 * 缓存初始化容量
                 */
                private int initialCapacity = (int) (maxCapacity * 0.3f);

                /**
                 * 缓存并发查询线程数
                 */
                private int concurrencyLevel = 4;

                /**
                 * 是否允许值为空
                 */
                private boolean allowNullValues = true;
            }

            @Data
            public static class Caffeine {
                /**
                 * 是否启用，默认 false
                 */
                private boolean enabled = false;

                /**
                 * 缓存最大容量
                 */
                private int maxCapacity = 5000;

                /**
                 * 缓存初始化容量
                 */
                private int initialCapacity = (int) (maxCapacity * 0.3f);

                /**
                 * 是否允许值为空
                 */
                private boolean allowNullValues = true;
            }

            @Data
            public static class Ehcache {
                /**
                 * 是否启用，默认 false
                 */
                private boolean enabled = false;

                /**
                 * ehcache配置文件路径
                 */
                private String xmlConfigFile = "classpath*:ehcache.xml";

                /**
                 * ehcache.xml中定义的name
                 */
                private String name;

                /**
                 * 是否允许值为空
                 */
                private boolean allowNullValues = true;
            }

            @Data
            public static class LinkedHashMap {
                /**
                 * 是否启用，默认 true
                 */
                private boolean enabled = true;

                /**
                 * 是否允许值为空
                 */
                private boolean allowNullValues = true;

                /**
                 * 缓存最大容量
                 */
                private int maxCapacity = 5000;
            }

            @Data
            public static class Clean {
                /**
                 * 是否启用定期清理，默认 true
                 */
                private boolean enabled = true;

                /**
                 * 定期清理过期的缓存
                 */
                private String cron = "*/5 * * * * *";
            }
        }
    }

    @Data
    public static class DataSource {

        private Dynamic dynamic = new Dynamic();

        @Data
        public static class Dynamic {
            /**
             * 是否启用动态多数据源
             */
            private boolean enabled = false;

            /**
             * 缺省使用的数据源KEY
             */
            private String defaultLookupKey = "default";

            /**
             * 动态多数据源扫描Mapper接口包路径
             */
            private String[] basePackages = {"com.github.hbq969"};
        }
    }
}
