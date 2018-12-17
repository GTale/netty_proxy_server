package com.phantom.netty.server.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @author: phantom
 * @Date: 2018/12/6 09:52
 * @Description: 对应yml文件的配置类
 */
@Configuration
@ConfigurationProperties(prefix = "proxy")
@Getter
@Setter
public class ProxyConfig {

    /**
     * 指令端口,默认8000
     */
    private int port = 8000;

}
