package com.phantom.netty.client.util;

import de.felixroske.jfxsupport.GUIState;
import javafx.stage.Stage;

/**
 * 重新设置相对位置
 */
public class ResetPositionUtil {

    public static void reset(Stage stage) {
        Stage primaryStage = GUIState.getStage();
        double centerXPosition = primaryStage.getX() + primaryStage.getWidth() / 2d;
        double centerYPosition = primaryStage.getY() + primaryStage.getHeight() / 2d;
        stage.setX(centerXPosition - stage.getWidth() / 2d);
        stage.setY(centerYPosition - stage.getHeight() / 2d);
    }

    public static void moveOut(Stage stage, double x, double y) {
        stage.setX(x);
        stage.setY(y);
    }
}
