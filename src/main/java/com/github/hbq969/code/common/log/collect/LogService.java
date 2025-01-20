package com.github.hbq969.code.common.log.collect;

import com.github.hbq969.code.common.log.spi.LogData;

import java.util.List;

public interface LogService {
    default boolean submit(LogData data) {
        return true;
    }

    void deal(List<LogData> list);
}
