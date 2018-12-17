package com.phantom.netty.client.view;

import javafx.concurrent.Task;
import javafx.scene.Scene;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.Background;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;


public class ProgressFrom {

    private Stage dialogStage;
    private ProgressIndicator progressIndicator;

    public ProgressFrom(final Task<?> task, Stage primaryStage) {
        dialogStage = new Stage();
        progressIndicator = new ProgressIndicator();

        // 窗口父子关系
        dialogStage.initOwner(primaryStage);
        dialogStage.initStyle(StageStyle.UNDECORATED);
        dialogStage.initStyle(StageStyle.TRANSPARENT);
        dialogStage.initModality(Modality.APPLICATION_MODAL);

        // 进度
        progressIndicator.setProgress(-1F);

        // 计算父窗口的中心位置
        double centerXPosition = primaryStage.getX() + primaryStage.getWidth() / 2d;
        double centerYPosition = primaryStage.getY() + primaryStage.getHeight() / 2d;

        VBox vBox = new VBox();
        vBox.setSpacing(10);
        vBox.setBackground(Background.EMPTY);
        vBox.getChildren().addAll(progressIndicator);

        Scene scene = new Scene(vBox);
        scene.setFill(null);
        dialogStage.setScene(scene);

        // 重新设定子窗口的相对位置
        dialogStage.setOnShowing(ev -> dialogStage.hide());

        // Relocate the pop-up Stage
        dialogStage.setOnShown(ev -> {
            dialogStage.setX(centerXPosition - dialogStage.getWidth() / 2d);
            dialogStage.setY(centerYPosition - dialogStage.getHeight() / 2d);
            dialogStage.show();
        });

        Thread inner = new Thread(task);
        inner.start();

        task.setOnSucceeded(event ->
                dialogStage.close()
        );
    }

    public void activateProgressBar() {

        dialogStage.show();
    }

    public Stage getDialogStage() {
        return dialogStage;
    }

    public void cancelProgressBar() {
        dialogStage.close();
    }
}
