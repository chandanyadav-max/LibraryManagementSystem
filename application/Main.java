package application;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import ui.LoginView;

public class Main extends Application {

    @Override
    public void start(Stage stage) {
        LoginView login = new LoginView(stage);
        Scene scene = new Scene(login.getView(),1200,700);

        stage.setTitle("Library Management System");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}