package com.phantom.netty.client.entity;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ClientConfig {
    private Integer id;
    private String  serverUrl;
    private Integer port;
    private String  username;
    private String  password;
    // 重试次数
    private Integer maxRetry = 5;
}
