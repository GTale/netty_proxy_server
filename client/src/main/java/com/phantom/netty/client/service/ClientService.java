package com.phantom.netty.client.service;

import com.phantom.netty.client.entity.ClientConfig;
import com.phantom.netty.client.mapper.ClientMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ClientService {
    public void saveOrUpdate(ClientConfig param) {
        boolean      saveOrUpdate = false;
        ClientConfig config       = clientMapper.findServer(param.getServerUrl().trim());

        if (config == null) {
            saveOrUpdate = true;
            config = new ClientConfig();
        }

        config.setServerUrl(param.getServerUrl().trim());
        config.setPort(param.getPort());
        config.setUsername(param.getUsername());
        config.setPassword(param.getPassword());

        if (saveOrUpdate) {
            clientMapper.saveClientConfig(config);
        } else {
            clientMapper.updateClientConfig(config);
        }
    }

    public List<ClientConfig> getClientConfigs() {
        return clientMapper.getClientConfigs();
    }

    @Autowired
    private ClientMapper clientMapper;
}
