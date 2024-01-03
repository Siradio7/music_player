package com.music_player;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;

public class HelloApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("hello-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        Image icon = new Image(String.valueOf(HelloApplication.class.getResource("la-musique.png")));
        stage.getIcons().add(icon);
        stage.setTitle("Music player");
        stage.setResizable(false);
        stage.centerOnScreen();
        stage.setAlwaysOnTop(true);
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}