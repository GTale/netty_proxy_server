package com.phantom.netty.server.controller.common.dto;

import lombok.Getter;
import lombok.Setter;

/**
 * @author: phantom
 * @Date: 2018/12/6 10:31
 * @Description:
 */
@Getter
@Setter
public class BasicDTO {
    private String token;
    private Integer pageNo = 1;
    private Integer pageSize = 8;
    private String keyWord;
}
