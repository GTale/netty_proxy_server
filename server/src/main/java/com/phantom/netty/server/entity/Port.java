package com.phantom.netty.server.entity;

import lombok.Data;

import java.util.Date;

@Data
public class Port {
    private Integer id;
    private Integer userId;
    private String  port;
    private String  portToken;
    private Integer proxyType;
    private String  remotePort;
    private String  remoteHost;
    private String  remark;
    private Integer isDel;
    private Date    createTime;
    private Date    updateTime;

    // 额外字段
    private String  username;
    private Boolean connect;
}
