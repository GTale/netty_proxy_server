package com.phantom.netty.client;

import com.phantom.netty.client.controller.BaseController;
import com.phantom.netty.client.util.NioEventUtil;
import com.phantom.netty.client.util.ResetPositionUtil;
import com.phantom.netty.client.view.LoginView;
import de.felixroske.jfxsupport.AbstractFxmlView;
import de.felixroske.jfxsupport.AbstractJavaFxApplicationSupport;
import de.felixroske.jfxsupport.SplashScreen;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Optional;
import java.util.function.Consumer;

@SpringBootApplication
@MapperScan(basePackages = "com.phantom.netty.client.mapper")
public class Main extends AbstractJavaFxApplicationSupport {

    private static ConfigurableApplicationContext applicationContext;

    @Override
    public void start(Stage stage) throws Exception {
        super.start(stage);
        stage.setResizable(false);
        stage.setOnCloseRequest(event -> NioEventUtil.close());
    }

    @Override
    public void beforeInitialView(Stage stage, ConfigurableApplicationContext ctx) {
        applicationContext = ctx;
        super.beforeInitialView(stage, ctx);
    }

    public static BaseController getView(final Class<? extends AbstractFxmlView> window, final Modality mode, Consumer<BaseController> wrapper) {
        BaseController         baseController = null;
        final AbstractFxmlView view           = applicationContext.getBean(window);
        Stage                  newStage       = new Stage();
        Scene                  newScene;
        if (view.getView().getScene() != null) {
            newScene = view.getView().getScene();
        } else {
            newScene = new Scene(view.getView());
        }

        newStage.setScene(newScene);
        newStage.initModality(mode);

        //反射方法
        Method getDefaultTitle = ReflectionUtils.findMethod(AbstractFxmlView.class, "getDefaultTitle");
        Optional.ofNullable(getDefaultTitle).ifPresent(m -> {
            ReflectionUtils.makeAccessible(getDefaultTitle);
            newStage.setTitle((String) ReflectionUtils.invokeMethod(m, view));
        });

        // 反射获取controller
        Field loader = ReflectionUtils.findField(AbstractFxmlView.class, "fxmlLoader");
        if (loader != null) {
            ReflectionUtils.makeAccessible(loader);
            FXMLLoader fxmlLoader = (FXMLLoader) ReflectionUtils.getField(loader, view);
            if (fxmlLoader != null) {
                baseController = fxmlLoader.getController();
                baseController.setDialogStage(newStage);
                wrapper.accept(baseController);
                baseController.initController();
            }
        }

        ResetPositionUtil.moveOut(newStage, -10000, -10000);
        newStage.show();
        newStage.hide();
        ResetPositionUtil.reset(newStage);
        newStage.show();

        if (baseController != null) {
            baseController.runAfter();
        }

        return baseController;
    }


    public static void main(String[] args) {
        launch(Main.class, LoginView.class, new SplashScreen() {
            @Override
            public boolean visible() {
                return false;
            }
        }, args);
    }
}
