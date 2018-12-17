package com.phantom.netty.server.controller.common.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginDTO extends BasicDTO {
    private Integer id;
    private String username;
    private String password;
    private String passwordAgain;
}
