package com.github.hbq969.code.common.log.spi;

import lombok.Data;

@Data
public class DefaultLogData implements LogData {
    @DbCol("req_id")
    private String id;

    @DbCol("oper_name")
    private String user;

    @DbCol("oper_time")
    private long time;

    @DbCol("url")
    private String url;

    @DbCol("method_name")
    private String methodName;

    @DbCol("method_desc")
    private String methodDesc;

    @DbCol("get_paras")
    private String parameters;

    @DbCol("post_body")
    private String body;

    @DbCol("result")
    private String result;
}
