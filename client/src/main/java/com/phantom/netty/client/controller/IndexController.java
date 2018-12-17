package com.phantom.netty.client.controller;

import com.phantom.netty.client.listener.TrayListener;
import com.phantom.netty.client.util.GuiState;
import com.phantom.netty.client.util.NioEventUtil;
import de.felixroske.jfxsupport.FXMLController;
import de.felixroske.jfxsupport.GUIState;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.stage.Stage;
import javafx.util.Duration;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.net.URL;
import java.util.ResourceBundle;

@FXMLController
public class IndexController extends BaseController implements Initializable {
    //    @FXML
//    private Menu                   logout;
    @FXML
    private ListView<String>       logListView;
    @FXML
    private Label                  serverInfo;
    @FXML
    private Label                  serverStatus;
    private int                    MAX_LOG_LENGTH = 200;
    private ObservableList<String> logItems       = FXCollections.observableArrayList();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // 关闭之前的窗口
        GUIState.getStage().hide();

        // 初始化全局工具类
        GuiState.init(serverStatus);

        // 绑定退出
//        Label logoutLabel = new Label("退出");
//        logoutLabel.addEventFilter(MouseEvent.MOUSE_CLICKED, event -> {
//            NioEventUtil.close();
//            getDialogStage().close();
//        });
//        logout.setGraphic(logoutLabel);


        // 绑定控制台输出
        logListView.setItems(logItems);
        logListView.setCellFactory(param -> new ListCell<String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);

                if (item == null || empty) {
                    setText(null);
                } else {
                    setText(item);
                }
            }
        });

        // 定时任务,定时刷新控制台输出
        Timeline logTransfer = new Timeline(
                new KeyFrame(
                        Duration.seconds(1),
                        event -> {
                            GuiState.getQueue().drainTo(logItems);

                            if (logItems.size() > MAX_LOG_LENGTH) {
                                logItems.remove(0, logItems.size() - MAX_LOG_LENGTH);
                            }

                            logListView.scrollTo(logItems.size());
                        }
                )
        );
        logTransfer.setCycleCount(Timeline.INDEFINITE);
        logTransfer.play();
    }

    @Override
    public void initController() {
        // 禁止变换宽高
        getDialogStage().setResizable(false);

        // 创建托盘
        createTray(getDialogStage());

        getDialogStage().setOnCloseRequest(event -> {
            getDialogStage().hide();
            event.consume();
        });
    }

    @Override
    public void runAfter() {
        GUIState.setStage(getDialogStage());
    }

    /**
     * 创建托盘程序,awt
     */
    private void createTray(Stage stage) {

        try {
            // 阻止隐藏最后一个窗口后销毁进程
            Platform.setImplicitExit(false);

            // 要显示的菜单
            PopupMenu         popupMenu = new PopupMenu();
            java.awt.MenuItem openItem  = new java.awt.MenuItem("显示");
            java.awt.MenuItem quitItem  = new java.awt.MenuItem("退出");

            SystemTray    tray     = SystemTray.getSystemTray();
            BufferedImage image    = ImageIO.read(this.getClass().getClassLoader().getResourceAsStream("imgs/logo.png"));
            TrayIcon      trayIcon = new TrayIcon(image, "酸酸乳");
            tray.add(trayIcon);
            trayIcon.setImageAutoSize(true);
            trayIcon.addMouseListener(new TrayListener(stage));

            // 事件监听器
            ActionListener acl = event -> {
                java.awt.MenuItem item = (java.awt.MenuItem) event.getSource();

                if (item.getLabel().equals("退出")) {
                    SystemTray.getSystemTray().remove(trayIcon);
                    NioEventUtil.close();
                    Platform.exit();
                    System.exit(0);
                    return;
                }

                if (item.getLabel().equals("显示")) {
                    Platform.runLater(this::showDialogStage);
                }
            };

            openItem.addActionListener(acl);
            quitItem.addActionListener(acl);

            popupMenu.add(openItem);
            popupMenu.add(quitItem);

            trayIcon.setPopupMenu(popupMenu);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 设置当前访问的服务器
     */
    public void setServerInfo(String info) {
        this.serverInfo.setText(info);
    }
}
