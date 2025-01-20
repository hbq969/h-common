package com.github.hbq969.code.common.log.collect;

import com.github.hbq969.code.common.config.LogProperties;
import com.github.hbq969.code.common.decorde.DefaultOptionalFacade;
import com.github.hbq969.code.common.log.spi.LogData;
import com.github.hbq969.code.common.utils.GsonUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

@Slf4j
public class LogServiceFacade extends DefaultOptionalFacade<String, LogService> implements LogService, InitializingBean, DisposableBean {

    private BlockingQueue<LogData> q;

    private List<LogData> cache;

    @Autowired
    private LogProperties conf;

    private List<Thread> dealers;

    @Override
    public void afterPropertiesSet() throws Exception {
        log.info("日志采集配置: {}", GsonUtils.toJson(conf));
        this.q = new ArrayBlockingQueue<>(conf.getQueueCapacity());
        this.cache = new ArrayList<>(conf.getBatchSize());
        dealers = new ArrayList<>(conf.getThreadNum());
        for (int i = 0; i < conf.getThreadNum(); i++) {
            Thread dealer = new Thread(new LogDealer(q, cache, conf, this));
            dealers.add(dealer);
            dealer.start();
        }
    }

    @Override
    public boolean submit(LogData data) {
        return this.q.offer(data);
    }

    @Override
    public void deal(List<LogData> list) {
        getService().deal(list);
    }

    @Override
    public Optional<LogService> query() {
        return query(conf.getPolicy());
    }

    @Override
    public void destroy() throws Exception {
        for (Thread dealer : this.dealers) {
            if (dealer.isAlive()) {
                dealer.interrupt();
            }
        }
        this.dealers.clear();
        if (!cache.isEmpty()) {
            deal(cache);
            log.info("将缓存中最后一批的日志数据保存入库。");
        }
    }
}
