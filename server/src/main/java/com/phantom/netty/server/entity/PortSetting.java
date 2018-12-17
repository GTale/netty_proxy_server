package com.phantom.netty.server.entity;

import lombok.Data;

import java.util.Date;

@Data
public class PortSetting {
    private Integer id;
    private String  startPort;
    private String  endPort;
    private String  remark;
    private Integer isDel;
    private Date    createTime;
    private Date    updateTime;
}
