package com.phantom.netty.server.util;

import lombok.Data;

@Data
public class Response {
    private Integer code;
    private String message;
    private String data;

    public Response(Integer code, String message, String data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }
}
