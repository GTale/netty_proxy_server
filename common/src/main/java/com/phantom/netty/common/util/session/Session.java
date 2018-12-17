package com.phantom.netty.common.util.session;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author: phantom
 * @Date: 2018/11/29 11:15
 * @Description:
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Session {
    private String userId;
    private String userName;
}
