package com.phantom.netty.server.controller.common.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PortSettingDTO extends BasicDTO {
    private Integer id;
    private Integer startPort;
    private Integer endPort;
    private String  remark;
}
