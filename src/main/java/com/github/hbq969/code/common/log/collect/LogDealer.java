package com.github.hbq969.code.common.log.collect;

import com.github.hbq969.code.common.config.LogProperties;
import com.github.hbq969.code.common.log.spi.LogData;
import com.github.hbq969.code.common.utils.Count;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

@Slf4j
public class LogDealer implements Runnable {
    private final BlockingQueue<LogData> q;
    private final List<LogData> cache;
    private final LogProperties conf;
    private final LogServiceFacade facade;

    public LogDealer(BlockingQueue<LogData> q, List<LogData> cache, LogProperties conf, LogServiceFacade facade) {
        this.q = q;
        this.cache = cache;
        this.conf = conf;
        this.facade = facade;
    }

    @Override
    public void run() {
        String tn = Thread.currentThread().getName();
        log.info("{} 启动。", tn);
        LogData operLog;
        Count c = Count.unsafe();
        int batchSize = conf.getBatchSize();
        long timeout = conf.getBatchTimeoutMills();
        while (true) {
            try {
                operLog = q.poll();
                if (Objects.isNull(operLog)) {
                    if (c.compare(batchSize, timeout) && !cache.isEmpty()) {
                        facade.deal(cache);
                        c.reset();
                        continue;
                    }
                    try {
                        TimeUnit.SECONDS.sleep(1);
                    } catch (InterruptedException e) {
                        log.info("{} 被中断，正常退出。", tn);
                        break;
                    }
                } else {
                    cache.add(operLog);
                    c.incrementAndGet();
                    if (c.compare(batchSize, timeout)) {
                        facade.deal(cache);
                        c.reset();
                    }
                }
            } catch (Throwable e) {
                log.error("", e);
                try {
                    TimeUnit.SECONDS.sleep(1);
                } catch (InterruptedException ex) {
                    log.info("{} 被中断，正常退出。", tn);
                    break;
                }
            }
        }

    }
}
