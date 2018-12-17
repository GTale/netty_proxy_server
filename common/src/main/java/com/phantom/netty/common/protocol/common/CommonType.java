package com.phantom.netty.common.protocol.common;

/**
 * @author: phantom
 * @Date: 2018/12/6 16:01
 * @Description:
 */
public class CommonType {

    public static String sequence = "netty_proxy_sequence_id";
    public static String HTTP_REQUEST_DECODER = "netty_http_request_decoder";
    public static String HTTP_AGGREGATOR = "netty_http_aggregator";

    public class MessageType {
        public static final byte TYPE_CONNECT_REAL_SERVER = 0;
        public static final byte TYPE_MESSAGE             = 1;
    }

    public class ProxyType {
        public static final byte TCP  = 0;
        public static final byte HTTP = 1;
        public static final byte TUNNEL = 2;
    }
}
