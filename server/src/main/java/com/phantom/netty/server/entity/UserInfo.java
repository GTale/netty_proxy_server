package com.phantom.netty.server.entity;

import lombok.Data;

@Data
public class UserInfo {
    private Integer id;
    private String  username;
    private String  password;
    private String  userToken;
    private Integer isAdmin;
    private Integer isDel;
}
