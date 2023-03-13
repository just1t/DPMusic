package com.just1t.ui;

import com.just1t.dm.util.WebUtil;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.io.IOException;
import java.util.Objects;

public class Application extends javafx.application.Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Application.class.getResource("dm-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 432, 430);
        stage.setTitle("Download Music Made By just1t！！！");
        stage.getIcons().add(new Image(Objects.requireNonNull(Application.class.getClassLoader().getResourceAsStream("Snipaste_2023-03-04_13-27-53.png"))));
        stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent windowEvent) {
                WebUtil.shutdown();
                System.exit(0);//立即关闭！！！
            }
        });
        stage.setResizable(false);
        stage.setScene(scene);
        stage.show();
        new DMController().showWring();
    }

    public static void main(String[] args) {
        launch();
    }
}