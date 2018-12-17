package com.phantom.netty.common.util;

import java.util.UUID;

/**
 * @author: phantom
 * @Date: 2018/11/29 15:57
 * @Description:
 */
public class IdUtil {
    public static String getId() {
        return UUID.randomUUID().toString().split("-")[0];
    }
}
