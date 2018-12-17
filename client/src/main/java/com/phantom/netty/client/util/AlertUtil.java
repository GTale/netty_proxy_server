package com.phantom.netty.client.util;


import javafx.beans.property.ObjectProperty;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;

import java.util.function.Supplier;

public class AlertUtil {
    /**
     * 信息提示框
     *
     * @param message
     */
    public static void showInfoAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setContentText(message);
        show(alert, false);
    }

    /**
     * 等待信息提示框
     *
     * @param message
     */
    public static void showAndWaitInfoAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setContentText(message);
        show(alert, true);
    }

    /**
     * 注意提示框
     *
     * @param message
     */
    public static void showWarnAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setContentText(message);
        alert.show();
    }

    /**
     * 异常提示框
     *
     * @param message
     */
    public static void showErrorAlert(String message) {
        showErrorAlert(message, null);
    }

    /**
     * 异常提示框
     *
     * @param message
     */
    public static void showErrorAlert(String message, Supplier action) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setContentText(message);
        show(alert, true, action);
    }

    /**
     * 确定提示框
     *
     * @param message
     */
    @SuppressWarnings("unchecked")
    public static void showConfirmAlert(String message, Supplier success, Supplier error) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setContentText(message);
        show(alert, true, success, error);
    }

    private static void show(Alert alert, boolean shouldWait) {
        show(alert, shouldWait, null, null);
    }

    private static void show(Alert alert, boolean shouldWait, Supplier action) {
        show(alert, shouldWait, action, null);
    }

    private static void show(Alert alert, boolean shouldWait, Supplier success, Supplier error) {
        Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
        ResetPositionUtil.moveOut(stage, -10000, -10000);
        stage.show();
        stage.hide();
        ResetPositionUtil.reset(stage);
        stage.show();
        if (shouldWait) {
            alert.resultProperty().addListener(listener -> {
                ObjectProperty property   = (ObjectProperty) listener;
                ButtonType     buttonType = (ButtonType) property.getValue();
                if (buttonType == ButtonType.OK) {
                    if (success != null) {
                        success.get();
                    }
                } else {
                    if (error != null) {
                        error.get();
                    }
                }
            });
        }
    }
}
