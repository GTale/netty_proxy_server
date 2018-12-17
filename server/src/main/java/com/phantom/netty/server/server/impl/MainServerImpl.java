package com.phantom.netty.server.server.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.phantom.netty.common.protocol.common.CommonType;
import com.phantom.netty.common.util.IdUtil;
import com.phantom.netty.server.config.Constant;
import com.phantom.netty.server.controller.common.dto.LoginDTO;
import com.phantom.netty.server.controller.common.dto.PortDTO;
import com.phantom.netty.server.controller.common.dto.PortSettingDTO;
import com.phantom.netty.server.entity.*;
import com.phantom.netty.server.mapper.MainMapper;
import com.phantom.netty.server.server.MainServer;
import com.phantom.netty.server.util.HttpSessionUtil;
import com.phantom.netty.server.util.Response;
import com.phantom.netty.server.util.ServerHelper;
import com.phantom.netty.server.util.server.RemoteServer;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.ui.ModelMap;
import org.springframework.util.DigestUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.net.InetAddress;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Properties;

/**
 * @author: phantom
 * @Date: 2018/12/6 10:35
 * @Description:
 */
@Slf4j
@Service
public class MainServerImpl implements MainServer {
    @Autowired
    private       MainMapper mainMapper;
    private final Object     lock = new Object();

    //*********************登陆相关*************************************************************************************************************

    @Override
    public Object index(HttpServletRequest request, ModelMap model) {
        HttpSession session = request.getSession();
        if (session == null || session.getAttribute(Constant.userInfoField) == null) {
            model.addAttribute(Constant.errorField, "请先登录后再操作");
            return "redirect:/login.html";
        } else {
            try {
                InetAddress address = InetAddress.getLocalHost();
                model.addAttribute("serverIp", address.getHostAddress());
            } catch (Exception e) {
                //NOTHING
            }

            Properties props        = System.getProperties();
            Runtime    runtime      = Runtime.getRuntime();
            long       freeMemory   = runtime.freeMemory();
            long       totalMemory  = runtime.totalMemory();
            long       usedMemory   = totalMemory - freeMemory;
            long       maxMemory    = runtime.maxMemory();
            long       usableMemory = maxMemory - totalMemory + freeMemory;
            model.addAttribute("props", props);
            model.addAttribute("freeMemory", freeMemory);
            model.addAttribute("totalMemory", totalMemory);
            model.addAttribute("usedMemory", usedMemory);
            model.addAttribute("maxMemory", maxMemory);
            model.addAttribute("usableMemory", usableMemory);
            model.addAttribute(Constant.userInfoField, session.getAttribute(Constant.userInfoField));
            return "index";
        }
    }

    @Override
    public Object login(LoginDTO dto, ModelMap model, HttpServletRequest request) {
        if (StringUtils.isEmpty(dto.getUsername())) {
            model.addAttribute(Constant.errorField, "用户名不能为空");
            return "login";
        }

        if (StringUtils.isEmpty(dto.getPassword())) {
            model.addAttribute(Constant.errorField, "密码不能为空");
            return "login";
        }

        UserInfo userInfo = mainMapper.findUserByUsername(dto.getUsername());
        if (null == userInfo) {
            model.addAttribute(Constant.errorField, "用户不存在");
            return "login";
        } else {
            String encodePwd = DigestUtils.md5DigestAsHex((dto.getPassword() + Constant.encodeField).getBytes());
            if (!encodePwd.equals(userInfo.getPassword())) {
                model.addAttribute(Constant.errorField, "密码错误");
                return "login";
            } else {
                request.getSession().setAttribute(Constant.userInfoField, userInfo);
                model.addAttribute(Constant.userInfoField, userInfo);
                return "redirect:index.html";
            }
        }
    }

    @Override
    public String login(String username, String password) {
        if (StringUtils.isEmpty(username) || StringUtils.isEmpty(password)) {
            return null;
        }

        UserInfo userInfo = mainMapper.findUserByUsername(username);
        if (null != userInfo) {
            String encodePwd = DigestUtils.md5DigestAsHex((password + Constant.encodeField).getBytes());
            if (encodePwd.equals(userInfo.getPassword())) {
                return userInfo.getUserToken();
            }
        }

        return null;
    }

    @Override
    public Object logout(HttpServletRequest request) {
        HttpSession session = request.getSession();
        if (session == null) {
            return "500";
        } else {
            session.removeAttribute(Constant.userInfoField);
            return "redirect:/login.html";
        }
    }

    //*********************用户管理*************************************************************************************************************

    @Override
    public Object userAdd(LoginDTO dto, ModelMap model) {
        if (StringUtils.isEmpty(dto.getUsername())) {
            return new Response(Constant.error, "用户名不能为空", null);
        }

        if (StringUtils.isEmpty(dto.getPassword())) {
            return new Response(Constant.error, "密码不能为空", null);
        }

        UserInfo userInfo = mainMapper.findUserByUsername(dto.getUsername());
        if (null != userInfo) {
            return new Response(Constant.error, "用户已经存在", null);
        } else {
            userInfo = new UserInfo();
            userInfo.setUserToken(IdUtil.getId());
            userInfo.setIsAdmin(Constant.falseVal);
            userInfo.setIsDel(Constant.falseVal);
            userInfo.setUsername(dto.getUsername());
            userInfo.setPassword(DigestUtils.md5DigestAsHex((dto.getPassword() + Constant.encodeField).getBytes()));

            int effectRow = mainMapper.saveUser(userInfo);
            if (effectRow > 0) {
                return new Response(Constant.success, "操作成功", null);
            } else {
                return new Response(Constant.error, "新增用户失败", null);
            }
        }
    }

    @Override
    public Object userList(LoginDTO dto, ModelMap modelMap) {
        Page<UserInfo> page = PageHelper.startPage(dto.getPageNo(), dto.getPageSize());
        mainMapper.getUserList(dto.getKeyWord());
        modelMap.addAttribute("data", page.toPageInfo());
        modelMap.addAttribute("keyWord", dto.getKeyWord());
        return "user/list";
    }

    @Override
    public Object userDel(LoginDTO dto, ModelMap modelMap) {
        if (dto.getId() == null) {
            return new Response(Constant.error, "请选择要删除的用户", null);
        }

        UserInfo userInfo = mainMapper.findUserById(dto.getId());
        if (userInfo == null) {
            return new Response(Constant.error, "用户不存在", null);
        } else {
            mainMapper.delUser(dto.getId());
            return new Response(Constant.success, "操作成功", null);
        }
    }

    @Override
    public Object userChangePassword(LoginDTO dto) {
        if (dto.getId() == null) {
            return new Response(Constant.error, "请选择要操作的用户", null);
        }

        if (StringUtils.isEmpty(dto.getPassword())) {
            return new Response(Constant.error, "密码不能为空", null);
        }

        if (!StringUtils.equals(dto.getPassword(), dto.getPasswordAgain())) {
            return new Response(Constant.error, "两次密码不一致", null);
        }

        UserInfo userInfo = mainMapper.findUserById(dto.getId());
        if (userInfo == null) {
            return new Response(Constant.error, "用户不存在", null);
        } else {
            String password = DigestUtils.md5DigestAsHex((dto.getPassword() + Constant.encodeField).getBytes());
            mainMapper.changePassword(dto.getId(), password);
            return new Response(Constant.success, "操作成功", null);
        }
    }

    //*********************端口封禁*************************************************************************************************************

    @Override
    public Object portSetting(ModelMap modelMap) {
        List<PortSetting> data = mainMapper.getPortSettingList();
        modelMap.addAttribute("data", data);
        return "port/setting";
    }

    @Override
    public Object portBanGet(PortSettingDTO dto, ModelMap modelMap) {
        PortSetting data = mainMapper.findPortSettingById(dto.getId());
        if (data == null) {
            modelMap.addAttribute(Constant.errorField, "操作失败,记录不存在");
            return "error";
        } else {
            modelMap.addAttribute("data", data);
            return "port/setting-edit";
        }
    }

    @Override
    public Object portBanSave(PortSettingDTO dto, ModelMap modelMap) {
        PortSetting data;
        boolean     updateOrSave = false;

        if (dto.getStartPort() > dto.getEndPort()) {
            return new Response(Constant.error, "端口参数错误", null);
        }

        if (dto.getId() != null) {
            data = mainMapper.findPortSettingById(dto.getId());
            if (data == null) {
                return new Response(Constant.error, "操作失败,记录不存在", null);
            }
            updateOrSave = true;
        } else {
            data = new PortSetting();
        }

        data.setStartPort(dto.getStartPort() + "");
        data.setEndPort(dto.getEndPort() + "");
        data.setRemark(dto.getRemark() + "");
        data.setIsDel(Constant.falseVal);
        data.setUpdateTime(new Date());

        if (updateOrSave) {
            mainMapper.updatePortSetting(data);
        } else {
            data.setCreateTime(new Date());
            mainMapper.savePortSetting(data);
        }

        return new Response(Constant.success, "操作成功", null);
    }

    @Override
    public Object portBanDel(PortSettingDTO dto, ModelMap modelMap) {
        PortSetting data = mainMapper.findPortSettingById(dto.getId());
        if (data == null) {
            return new Response(Constant.error, "操作失败,记录不存在", null);
        } else {
            mainMapper.delPortSetting(dto.getId());
            return new Response(Constant.success, "操作成功", null);
        }
    }

    //*********************端口管理*************************************************************************************************************

    @Override
    public Object getPortList(PortDTO dto, HttpServletRequest request, ModelMap model) {
        HttpSession session = request.getSession();
        if (session == null || session.getAttribute(Constant.userInfoField) == null) {
            model.addAttribute(Constant.errorField, "请先登录后再操作");
            return "redirect:/login.html";
        } else {
            UserInfo   info = (UserInfo) session.getAttribute(Constant.userInfoField);
            Page<Port> page = PageHelper.startPage(dto.getPageNo(), dto.getPageSize());
            if (Objects.equals(info.getIsAdmin(), 1)) {
                mainMapper.getPortList(Objects.equals(dto.getFilter(), true) ? info.getId() : null, dto.getKeyWord());
            } else {
                mainMapper.getPortList(info.getId(), dto.getKeyWord());
            }

            page.getResult().forEach(p ->
                    p.setConnect(ServerHelper.containPort(p.getPortToken()))
            );

            model.addAttribute("data", page.toPageInfo());
            model.addAttribute("keyWord", dto.getKeyWord());
            return "port/list";
        }
    }

    @Override
    public Object getPort(PortDTO dto, HttpServletRequest request, ModelMap model) {
        UserInfo userInfo = HttpSessionUtil.getUserInfo(request);

        if (dto.getId() != null) {
            Port port = mainMapper.findPortById(dto.getId(), Objects.equals(userInfo.getIsAdmin(), 1) ? null : userInfo.getId());
            if (port != null) {
                model.addAttribute("data", port);
                return "port/edit";
            }
        }

        model.addAttribute(Constant.errorField, "操作失败,记录不存在");
        return "error";
    }

    @Override
    public Object addPort(PortDTO dto, HttpServletRequest request) {
        Port     port;
        boolean  updateOrSave = false;
        UserInfo userInfo     = HttpSessionUtil.getUserInfo(request);

        if (dto.getId() != null) {
            port = mainMapper.findPortById(dto.getId(), Objects.equals(userInfo.getIsAdmin(), 1) ? null : userInfo.getId());
            if (port == null) {
                return new Response(Constant.error, "操作失败,记录不存在", null);
            }
            updateOrSave = true;
        } else {
            port = new Port();
        }

        port.setPort(dto.getPort() + "");
        port.setUserId(userInfo.getId());
        port.setProxyType(dto.getType().getType() & 0xff);

        if (dto.getType() == PortType.TCP) {
            port.setRemoteHost(dto.getRemoteHost() + "");
            port.setRemotePort(dto.getRemotePort() + "");
        } else {
            port.setRemoteHost("");
            port.setRemotePort("");
        }

        port.setRemark(dto.getRemark());
        port.setIsDel(Constant.falseVal);
        port.setUpdateTime(new Date());

        if (updateOrSave) {
            mainMapper.updatePort(port);
        } else {
            port.setPortToken(IdUtil.getId());
            port.setCreateTime(new Date());
            mainMapper.savePort(port);
        }

        return new Response(Constant.success, "端口已经添加", null);
    }

    @Override
    public Object deletePort(PortDTO dto, HttpServletRequest request) {
        UserInfo userInfo = HttpSessionUtil.getUserInfo(request);

        if (dto.getId() != null) {
            Port port = mainMapper.findPortById(dto.getId(), Objects.equals(userInfo.getIsAdmin(), 1) ? null : userInfo.getId());
            if (port != null) {
                mainMapper.delPort(dto.getId());
                return new Response(Constant.success, "操作成功", null);
            }
        }

        return new Response(Constant.error, "操作失败,记录不存在", null);
    }

    @Override
    public Object startPort(PortDTO dto, HttpServletRequest request) {
        UserInfo userInfo = HttpSessionUtil.getUserInfo(request);
        if (dto.getId() != null) {
            Port port = mainMapper.findPortById(dto.getId(), Objects.equals(userInfo.getIsAdmin(), 1) ? null : userInfo.getId());
            if (port != null) {
                // 判断当前端口是否处于禁用范围
                Boolean isBan = mainMapper.queryPortIsBan(port.getPort());
                if (isBan) {
                    return new Response(Constant.error, "开启失败,该端口已被禁用", null);
                } else {
                    ProxyServer server;
                    synchronized (lock) {
                        server = ServerHelper.getProxyServer(port.getPortToken());
                        if (server != null) {
                            return new Response(Constant.error, "操作失败,该端口已被占用", null);
                        } else {
                            server = new ProxyServer();
                            server.setPort(Integer.valueOf(port.getPort()));
                            byte proxyType = port.getProxyType().byteValue();
                            server.setProxyType(proxyType);
                            server.setServerToken(port.getPortToken());
                            server.setUserToken(userInfo.getUserToken());
                            if (proxyType == CommonType.ProxyType.TCP) {
                                server.setRemoteHost(port.getRemoteHost());
                                server.setRemotePort(Integer.valueOf(port.getRemotePort()));
                            }
                            ServerHelper.addProxyServer(server);
                        }
                    }
                    // 开启端口
                    RemoteServer.buildServer(server);
                    if (server.getConnectStatus() == ConnectStatus.ONLINE) {
                        return new Response(Constant.success, "开启成功", null);
                    } else {
                        return new Response(Constant.success, "开启失败,端口冲突", null);
                    }
                }
            }
        }

        return new Response(Constant.error, "操作失败,记录不存在", null);
    }

    @Override
    public Object stopPort(PortDTO dto, HttpServletRequest request) {
        UserInfo userInfo = HttpSessionUtil.getUserInfo(request);
        if (dto.getId() != null) {
            Port port = mainMapper.findPortById(dto.getId(), Objects.equals(userInfo.getIsAdmin(), 1) ? null : userInfo.getId());
            if (port != null) {
                try {
                    ProxyServer server = new ProxyServer();
                    server.setServerToken(port.getPortToken());
                    server.setPort(Integer.valueOf(port.getPort()));
                    ServerHelper.remove(server);
                    RemoteServer.closeServer(server);
                    log.info("[{},用户{}尝试关闭{}端口,端口token:{}]", LocalDateTime.now().toString(), userInfo.getUsername(), port.getPort(), port.getPortToken());
                    return new Response(Constant.success, "关闭成功", null);
                } catch (Exception e) {
                    log.info("[{},用户{}尝试关闭{}端口,端口token:{},关闭失败：{}]",
                            LocalDateTime.now().toString(), userInfo.getUsername(), port.getPort(),
                            port.getPortToken(), e.getMessage());
                    return new Response(Constant.success, "关闭失败,发生未知错误", null);
                }
            }
        }
        return new Response(Constant.error, "操作失败,记录不存在", null);
    }

    @Override
    public Object infoPort(PortDTO dto) {
        return null;
    }
}
