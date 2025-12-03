package com.github.hbq969.code.common.log.spi;

import cn.hutool.core.util.StrUtil;
import com.github.hbq969.code.common.config.LogProperties;
import com.github.hbq969.code.common.utils.FormatTime;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.concurrent.TimeUnit;

@Slf4j
public class DefaultLogClear {

    @Autowired
    private LogProperties conf;

    @Autowired
    private JdbcTemplate jt;

    @Scheduled(cron = "${operlog.clean.cron:0 0 1 * * ?}")
    public void clean() {
        log.debug("开始清理历史操作日志");
        clean0();
    }

    protected void clean0() {
        if (StrUtil.isNotEmpty(conf.getTableSuffix())) {
            long t = DateTime.now().plusMonths(-conf.getClean().getMonthNumForHistory()).getMillis();
            String suffix = FormatTime.format(conf.getTableSuffix(), t, TimeUnit.MILLISECONDS);
            String tn = StrUtil.format("{}{}", conf.getTablePrefix(), suffix);
            log.info("需要清理的操作日志历史表: {}", tn);
            jt.update(StrUtil.format("truncate table {}", tn));
            jt.update(StrUtil.format("drop table {}", tn));
            log.info("清理的操作日志历史表结束. {}", tn);
        }
    }
}
