package com.phantom.netty.server.controller;

import com.phantom.netty.server.controller.common.dto.LoginDTO;
import com.phantom.netty.server.controller.common.dto.PortDTO;
import com.phantom.netty.server.controller.common.dto.PortSettingDTO;
import com.phantom.netty.server.server.MainServer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

/**
 * @author: phantom
 * @Date: 2018/12/6 10:01
 * @Description:
 */
@Controller
public class MainController {

    //*********************登陆相关*************************************************************************************************************

    @RequestMapping("index.html")
    public Object index(ModelMap modelMap, HttpServletRequest request) {
        return server.index(request, modelMap);
    }

    @PostMapping("login.html")
    public Object login(LoginDTO dto, ModelMap modelMap, HttpServletRequest request) {
        return server.login(dto, modelMap, request);
    }

    @RequestMapping("logout.html")
    public Object logout(HttpServletRequest request) {
        return server.logout(request);
    }

    //*********************用户管理*************************************************************************************************************

    @RequestMapping("user/list.html")
    public Object userList(LoginDTO dto, ModelMap modelMap) {
        return server.userList(dto, modelMap);
    }

    @ResponseBody
    @RequestMapping("user/add")
    public Object userAdd(@RequestBody LoginDTO dto, ModelMap modelMap) {
        return server.userAdd(dto, modelMap);
    }

    @ResponseBody
    @RequestMapping("user/del")
    public Object userDel(@RequestBody LoginDTO dto, ModelMap modelMap) {
        return server.userDel(dto, modelMap);
    }

    @ResponseBody
    @RequestMapping("user/password")
    public Object userChangePassword(@RequestBody LoginDTO dto) {
        return server.userChangePassword(dto);
    }

    //*********************端口封禁*************************************************************************************************************

    @RequestMapping("port/setting.html")
    public Object portSetting(ModelMap modelMap) {
        return server.portSetting(modelMap);
    }

    @RequestMapping("port/setting/edit.html")
    public Object portBanGet(PortSettingDTO dto, ModelMap modelMap) {
        return server.portBanGet(dto, modelMap);
    }

    @ResponseBody
    @RequestMapping("port/setting/save")
    public Object portBanSave(@RequestBody PortSettingDTO dto, ModelMap modelMap) {
        return server.portBanSave(dto, modelMap);
    }

    @ResponseBody
    @RequestMapping("port/setting/del")
    public Object portBanDel(@RequestBody PortSettingDTO dto, ModelMap modelMap) {
        return server.portBanDel(dto, modelMap);
    }

    //*********************端口管理*************************************************************************************************************

    /**
     * 端口列表
     */
    @RequestMapping("port/list.html")
    public Object portList(PortDTO dto, HttpServletRequest request, ModelMap model) {
        return server.getPortList(dto, request, model);
    }

    /**
     * 修改绑定的端口
     */
    @RequestMapping("port/edit.html")
    public Object getPort(PortDTO dto, HttpServletRequest request, ModelMap model) {
        return server.getPort(dto, request, model);
    }

    /**
     * 绑定端口
     */
    @ResponseBody
    @RequestMapping("port/save")
    public Object addPort(@RequestBody PortDTO dto, HttpServletRequest request, BindingResult result) {
        return server.addPort(dto, request);
    }

    /**
     * 删除端口
     */
    @ResponseBody
    @RequestMapping("port/delete")
    public Object deletePort(@RequestBody PortDTO dto, HttpServletRequest request, BindingResult result) {
        return server.deletePort(dto, request);
    }

    /**
     * 启动服务
     */
    @ResponseBody
    @RequestMapping("port/start")
    public Object startPort(@RequestBody PortDTO dto, HttpServletRequest request, BindingResult result) {
        return server.startPort(dto, request);
    }

    /**
     * 释放端口
     */
    @ResponseBody
    @RequestMapping("port/stop")
    public Object stopPort(@RequestBody PortDTO dto, HttpServletRequest request, BindingResult result) {
        return server.stopPort(dto, request);
    }

    /**
     * 返回与当前端口相关的信息
     */
    @RequestMapping("port/info")
    public Object infoPort(@RequestBody PortDTO dto, BindingResult result) {
        return server.infoPort(dto);
    }

    @Autowired
    private MainServer server;
}
