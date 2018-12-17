package com.phantom.netty.client.controller;

import com.phantom.netty.client.util.AlertUtil;
import de.felixroske.jfxsupport.FXMLController;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import lombok.Setter;
import org.apache.commons.lang.StringUtils;

import java.net.URL;
import java.util.ResourceBundle;

@FXMLController
public class PortController extends BaseController implements Initializable {

    @Setter
    private IndexController indexController;
    @FXML
    private TextField alias;
    @FXML
    private TextField remotePort;
    @FXML
    private TextField port;
    @FXML
    private TextField remark;
    @FXML
    private Button save;
    @FXML
    private Button cancel;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        cancel.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
            getDialogStage().close();
        });

        save.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
            String aliasStr = alias.getText();
            String remotePortStr = remotePort.getText();
            String portStr = port.getText();
            String remarkStr = remark.getText();

            if (StringUtils.isEmpty(aliasStr)) {
                AlertUtil.showErrorAlert("请输入别名");
                return;
            }

            if (StringUtils.isEmpty(remotePortStr)) {
                AlertUtil.showErrorAlert("请输入远程端口");
                return;
            }

            if (StringUtils.isEmpty(portStr)) {
                AlertUtil.showErrorAlert("请输入本地映射的端口");
                return;
            }



        });
    }

    @Override
    public void initController() {

    }

    @Override
    public void runAfter() {

    }
}
