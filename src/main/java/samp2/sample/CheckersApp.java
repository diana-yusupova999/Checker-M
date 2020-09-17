package samp2.sample;

import javafx.application.Application;
import javafx.stage.Stage;

public class CheckersApp extends Application {

    static GameStart gameMain;

    @Override
    public void start(Stage primaryStage) {
        gameMain = new GameStart();
    }

    public static void main(String[] args) {
        launch(args);
    }
}