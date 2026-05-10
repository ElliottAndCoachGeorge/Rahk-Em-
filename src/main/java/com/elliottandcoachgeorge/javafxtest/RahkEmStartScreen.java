package com.elliottandcoachgeorge.javafxtest;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class RahkEmStartScreen extends javafx.application.Application {

    @Override
    public void start(Stage stage) {

        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("StartScreen.fxml")
            );

            loader.setController(new StartScreenController(stage));

            Parent root = loader.load();

            Scene scene = new Scene(root);

            stage.setTitle("Rahk Em'");
            stage.setResizable(false);
            stage.setScene(scene);
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}