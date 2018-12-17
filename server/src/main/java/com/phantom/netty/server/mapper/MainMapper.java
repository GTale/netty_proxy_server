package com.phantom.netty.server.mapper;

import com.phantom.netty.server.entity.Port;
import com.phantom.netty.server.entity.PortSetting;
import com.phantom.netty.server.entity.UserInfo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface MainMapper {
    UserInfo findUserByUsername(@Param("username") String username);

    Integer saveUser(UserInfo userInfo);

    List<UserInfo> getUserList(@Param("keyWord") String keyWord);

    UserInfo findUserById(@Param("userId") Integer id);

    void delUser(@Param("userId") Integer id);

    void changePassword(@Param("userId") Integer id, @Param("password") String password);

    List<Port> getPortList(@Param("userId") Integer id, @Param("port") String keyWord);

    Port findPortById(@Param("portId") Integer portId, @Param("userId") Integer userId);

    void savePort(Port port);

    void delPort(@Param("portId") Integer id);

    List<PortSetting> getPortSettingList();

    PortSetting findPortSettingById(@Param("id") Integer id);

    void updatePortSetting(PortSetting data);

    void savePortSetting(PortSetting data);

    void delPortSetting(@Param("id") Integer id);

    void updatePort(Port port);

    Boolean queryPortIsBan(@Param("port") String port);
}
