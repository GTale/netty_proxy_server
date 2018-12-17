package com.phantom.netty.client.entity;

import lombok.Data;

@Data
public class PortEntity {
    private String alias;
    private String remotePort;
    private String localPort;
    private String remark;
}
