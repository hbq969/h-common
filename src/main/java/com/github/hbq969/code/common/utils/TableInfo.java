package com.github.hbq969.code.common.utils;

import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import java.util.concurrent.TimeUnit;

/**
 * @author : hbq969@gmail.com
 * @description : 表名包装器
 * @createTime : 15:05:09, 2023.03.31, 周五
 */
@Data
public class TableInfo {

    private RoundInfo roundInfo;
    private String tablePrefix;
    private String tableSuffix;
    private String tableName;

    public TableInfo format() {
        if (StringUtils.isEmpty(tableName)) {
            String suffix = FormatTime.format(tableSuffix, FormatTime.nowSecs(), TimeUnit.SECONDS);
            if (roundInfo != null) {
                suffix = FormatTime.format(tableSuffix, roundInfo.getStartTime().getSecs(), TimeUnit.SECONDS);
            }
            this.tableName = String.join("", tablePrefix, suffix);
        }
        return this;
    }

    public long getStartTimeSec() {
        return roundInfo == null ? -1 : roundInfo.getStartTime() == null ? -1 : roundInfo.getStartTime().getSecs();
    }

    public long getStartTimeMills() {
        return roundInfo == null ? -1 : roundInfo.getStartTime() == null ? -1 : roundInfo.getStartTime().getMills();
    }

    public long getEndTimeSec() {
        return roundInfo == null ? -1 : roundInfo.getEndTime() == null ? -1 : roundInfo.getEndTime().getSecs();
    }

    public long getEndTimeMills() {
        return roundInfo == null ? -1 : roundInfo.getEndTime() == null ? -1 : roundInfo.getEndTime().getMills();
    }

    public String getTableName() {
        if (StringUtils.isEmpty(tableName)) {
            format();
        }
        return this.tableName;
    }
}
