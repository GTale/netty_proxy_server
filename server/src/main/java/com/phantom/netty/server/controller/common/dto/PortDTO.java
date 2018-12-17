package com.phantom.netty.server.controller.common.dto;

import com.phantom.netty.server.entity.PortType;
import lombok.Getter;
import lombok.Setter;

/**
 * @author: phantom
 * @Date: 2018/12/6 10:23
 * @Description:
 */
@Getter
@Setter
public class PortDTO extends BasicDTO {
    private Integer  id;
    private Integer  port;
    private PortType type;
    private String   remoteHost;
    private Integer  remotePort;
    private String   remark;
    private String   serverToken;
    private Boolean  filter;
}
