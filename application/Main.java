package application;

import javafx.application.Application;
import javafx.stage.Stage;
import view.LoginView;

public class Main extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Library Management System");
        primaryStage.setMinWidth(1100);
        primaryStage.setMinHeight(700);
        new LoginView(primaryStage).show();
    }
}