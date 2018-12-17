package com.phantom.netty.client.mapper;

import com.phantom.netty.client.entity.ClientConfig;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ClientMapper {
    ClientConfig findServer(@Param("serverUrl") String serverUrl);

    void saveClientConfig(ClientConfig config);

    void updateClientConfig(ClientConfig config);

    List<ClientConfig> getClientConfigs();
}
