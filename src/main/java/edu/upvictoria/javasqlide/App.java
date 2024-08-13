package edu.upvictoria.javasqlide;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.Objects;

public class App extends Application {
    @Override
    public void start(Stage stage) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource("main.fxml"));

            Scene scene = new Scene(fxmlLoader.load(), 1280, 800);
            stage.setTitle("Java SQL Developer");
            scene.getStylesheets().add(Objects.requireNonNull(App.class.getResource("/edu/upvictoria/javasqlide/sql-keywords.css")).toExternalForm());
            stage.setScene(scene);
            stage.show();
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch();
    }
}