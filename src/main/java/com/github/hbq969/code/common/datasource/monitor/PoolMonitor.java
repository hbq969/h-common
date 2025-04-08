package com.github.hbq969.code.common.datasource.monitor;

import com.github.hbq969.code.common.datasource.DynamicDataSource;
import com.zaxxer.hikari.HikariDataSource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;

import javax.sql.DataSource;
import java.util.Map;

@Slf4j
public class PoolMonitor {

    private DynamicDataSource dynamicDataSource;

    public PoolMonitor(DataSource dynamicDataSource) {
        this.dynamicDataSource = (DynamicDataSource) dynamicDataSource;
    }

    @Scheduled(cron = "${spring.datasource.dynamic.monitor.cron:0 * * * * ?}")
    public void printPoolStatus() {
        if (dynamicDataSource != null) {
            log.debug("\n");
            log.debug("HikariCP  Pool Status:");
            Map<Object, DataSource> map = dynamicDataSource.getResolvedDataSources();
            HikariDataSource hds;
            for (Map.Entry<Object, DataSource> entry : map.entrySet()) {
                hds = (HikariDataSource) entry.getValue();
                log.debug("{}, Active  connections: {}", entry.getKey(), hds.getHikariPoolMXBean().getActiveConnections());
                log.debug("{}, Idle  connections: {}", entry.getKey(), hds.getHikariPoolMXBean().getIdleConnections());
                log.debug("{}, Total  connections: {}", entry.getKey(), hds.getHikariPoolMXBean().getTotalConnections());
                log.debug("{}, Threads  awaiting connection: {}", entry.getKey(), hds.getHikariPoolMXBean().getThreadsAwaitingConnection());
                log.debug("{}, Maximum  pool size: {}", entry.getKey(), hds.getMaximumPoolSize());
                log.debug("{}, Minimum  idle connections: {}", entry.getKey(), hds.getMinimumIdle());
            }
            log.debug("\n");
        }
    }
}
