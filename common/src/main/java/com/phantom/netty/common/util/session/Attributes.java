package com.phantom.netty.common.util.session;

import io.netty.util.AttributeKey;

/**
 * @author: phantom
 * @Date: 2018/11/28 17:59
 * @Description:
 */
public interface Attributes {
    AttributeKey<Session> SESSION    = AttributeKey.newInstance("session");
    AttributeKey<Byte>    PROXY_TYPE = AttributeKey.newInstance("proxy_type");
    AttributeKey<Boolean> ShouldSSL  = AttributeKey.newInstance("proxy_should_ssl");
    AttributeKey<String>  USER_TOKEN = AttributeKey.newInstance("user_token");
}
