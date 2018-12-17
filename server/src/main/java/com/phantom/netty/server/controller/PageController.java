package com.phantom.netty.server.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author: phantom
 * @Date: 2018/12/6 10:03
 * @Description: 页面控制器
 */
@Controller
public class PageController {

    @RequestMapping("{page}.html")
    public Object page(@PathVariable String page) {
        return page;
    }

    @RequestMapping("{module}/{page}.html")
    public Object page(@PathVariable String module, @PathVariable String page) {
        return module + "/" + page;
    }
}
