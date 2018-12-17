package com.phantom.netty.server.server;

import com.phantom.netty.server.controller.common.dto.LoginDTO;
import com.phantom.netty.server.controller.common.dto.PortDTO;
import com.phantom.netty.server.controller.common.dto.PortSettingDTO;
import org.springframework.ui.ModelMap;

import javax.servlet.http.HttpServletRequest;

/**
 * @author: phantom
 * @Date: 2018/12/6 10:35
 * @Description:
 */
public interface MainServer {

    //*********************登陆相关*************************************************************************************************************

    Object index(HttpServletRequest request, ModelMap modelMap);

    Object login(LoginDTO dto, ModelMap modelMap, HttpServletRequest request);

    String login(String username, String password);

    Object logout(HttpServletRequest request);

    //*********************用户管理*************************************************************************************************************

    Object userAdd(LoginDTO dto, ModelMap modelMap);

    Object userList(LoginDTO dto, ModelMap modelMap);

    Object userDel(LoginDTO dto, ModelMap modelMap);

    Object userChangePassword(LoginDTO dto);

    //*********************端口封禁*************************************************************************************************************

    Object portSetting(ModelMap modelMap);

    Object portBanGet(PortSettingDTO dto, ModelMap modelMap);

    Object portBanSave(PortSettingDTO dto, ModelMap modelMap);

    Object portBanDel(PortSettingDTO dto, ModelMap modelMap);

    //*********************端口管理*************************************************************************************************************

    Object getPortList(PortDTO dto, HttpServletRequest request, ModelMap model);

    Object addPort(PortDTO dto, HttpServletRequest request);

    Object getPort(PortDTO dto, HttpServletRequest request, ModelMap model);

    Object deletePort(PortDTO dto, HttpServletRequest request);

    Object stopPort(PortDTO dto, HttpServletRequest request);

    Object startPort(PortDTO dto, HttpServletRequest request);

    Object infoPort(PortDTO dto);
}
