package com.phantom.netty.client.controller;

import com.phantom.netty.client.Main;
import com.phantom.netty.client.NioClient;
import com.phantom.netty.client.console.LoginConsoleCommand;
import com.phantom.netty.client.entity.ClientConfig;
import com.phantom.netty.client.service.ClientService;
import com.phantom.netty.client.util.AlertUtil;
import com.phantom.netty.client.util.GuiState;
import com.phantom.netty.client.view.IndexView;
import com.phantom.netty.client.view.ProgressFrom;
import de.felixroske.jfxsupport.FXMLController;
import de.felixroske.jfxsupport.GUIState;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;

import java.net.URL;
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.stream.Collectors;


@FXMLController
public class LoginController implements Initializable {

    @FXML
    private ComboBox<String>   serverUrl;
    @FXML
    private TextField          port;
    @FXML
    private TextField          username;
    @FXML
    private PasswordField      password;
    @FXML
    private CheckBox           remember;
    @FXML
    private Button             connect;
    @FXML
    private Button             exit;
    private List<ClientConfig> configs;
    private Bootstrap          bootstrap;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initProperties();

        serverUrl.getSelectionModel().selectedIndexProperty().addListener((observable, oldValue, newValue) -> {
            if (!Objects.equals(oldValue, newValue)) {
                String newValObj = serverUrl.getSelectionModel().getSelectedItem();
                configs.stream().filter(m -> StringUtils.equalsIgnoreCase(m.getServerUrl(), newValObj)).findFirst().ifPresent(first ->
                        initOtherPane(first)
                );
            }
        });

        connect.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
            String serverUrlStr = serverUrl.getSelectionModel().getSelectedItem();
            String portStr      = port.getText();
            String usernameStr  = username.getText();
            String passwordStr  = password.getText();

            if (StringUtils.isEmpty(serverUrlStr)) {
                AlertUtil.showErrorAlert("请输入服务器地址");
                return;
            }
            if (StringUtils.isEmpty(portStr) || !StringUtils.isNumeric(portStr)) {
                AlertUtil.showErrorAlert("请输入正确的服务器端口");
                return;
            }
            if (StringUtils.isEmpty(usernameStr)) {
                AlertUtil.showErrorAlert("请输入用户名");
                return;
            }
            if (StringUtils.isEmpty(passwordStr)) {
                AlertUtil.showErrorAlert("请输入密码");
                return;
            }

            ClientConfig config = new ClientConfig();
            config.setServerUrl(serverUrlStr);
            config.setPort(Integer.valueOf(portStr));
            config.setUsername(usernameStr);
            config.setPassword(passwordStr);

            if (remember.isSelected()) {
                clientService.saveOrUpdate(config);
            }

            ProgressFrom progress = new ProgressFrom(new Task<Object>() {
                @Override
                protected Object call() throws Exception {
                    // 访问远程服务器,进行登录工作
                    if (bootstrap == null) {
                        bootstrap = NioClient.getBootstrap(config.getServerUrl(), config.getPort());
                    }
                    Channel serverChannel = NioClient.syncConnect(bootstrap, config.getServerUrl(), config.getPort(), config.getMaxRetry());
                    if (serverChannel != null) {
                        // 发出登陆请求
                        boolean result = LoginConsoleCommand.login(serverChannel, usernameStr, passwordStr);
                        if (result) {
                            // 跳转新页面
                            Platform.runLater(() -> {
                                GuiState.setConfig(config);
                                Main.getView(IndexView.class, Modality.WINDOW_MODAL, baseController ->
                                        ((IndexController) baseController).setServerInfo(serverUrlStr + ":" + portStr)
                                );
                            });
                        }
                    } else {
                        Platform.runLater(() ->
                                AlertUtil.showErrorAlert("服务器连接失败,请检查URL和端口是否正确")
                        );
                    }
                    return 0;
                }
            }, GUIState.getStage());
            progress.activateProgressBar();
        });

        exit.addEventFilter(MouseEvent.MOUSE_CLICKED, event -> {
            Stage primaryStage = GUIState.getStage();
            if (primaryStage != null) {
                primaryStage.close();
            }
        });
    }

    /**
     * 从数据库获取所有添加过的数据
     */
    private void initProperties() {
        configs = clientService.getClientConfigs();
        if (!CollectionUtils.isEmpty(configs)) {
            initLoginPane(configs);
        }
    }

    /**
     * 初始化下拉框
     */
    private void initLoginPane(List<ClientConfig> configs) {
        ObservableList<String> data = FXCollections.observableArrayList(configs.stream().map(ClientConfig::getServerUrl).collect(Collectors.toList()));
        serverUrl.setItems(data);
        serverUrl.getSelectionModel().select(0);
        initOtherPane(configs.get(0));
    }

    /**
     * 初始化 端口、用户名、密码
     */
    private void initOtherPane(ClientConfig config) {
        port.setText(config.getPort() + "");
        username.setText(config.getUsername());
        password.setText(config.getPassword());
        remember.setSelected(true);
    }

    @Autowired
    private ClientService clientService;
}
