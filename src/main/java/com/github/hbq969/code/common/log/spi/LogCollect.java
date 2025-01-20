package com.github.hbq969.code.common.log.spi;

import com.github.hbq969.code.common.log.model.PointModel;

public interface LogCollect {
    LogData collect(PointModel point);
}
