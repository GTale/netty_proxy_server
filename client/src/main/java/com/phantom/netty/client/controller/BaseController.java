package com.phantom.netty.client.controller;

import javafx.fxml.Initializable;
import javafx.stage.Stage;
import lombok.Getter;
import lombok.Setter;

/**
 * @Auther: phantom
 * @Date: 2018/5/14 17:29
 * @Description:
 */
public abstract class BaseController implements Initializable {

    @Getter
    @Setter
    private Stage dialogStage;

    public abstract void initController();

    public abstract void runAfter();

    public void showDialogStage() {
        if (dialogStage != null) {
            dialogStage.show();
        }
    }

    public void closeDialogStage() {
        if (dialogStage != null) {
            dialogStage.close();
        }
    }
}
