package com.phantom.netty.common.util.ssl;

import lombok.Data;

@Data
public class SslConfig {
    private String certFilePath = "";
    private String keyFilePath  = "";
    private String host         = "nps.phantomscloud.top";
}
