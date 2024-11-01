package com.infoman;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

import static javafx.application.Application.launch;

public class Main extends Application{
    @Override
    public void start(Stage primaryStage) throws IOException {
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/fxml/main.fxml")));
        primaryStage.setTitle("New Student");
        primaryStage.setScene(new Scene(root, 876, 736 ));
        primaryStage.show();
    }
    public static void main(String[] args) {
        launch(args);
        DatabaseConnection connection= new DatabaseConnection();

    }
}

