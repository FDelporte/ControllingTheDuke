package be.webtechie.controllingtheduke;

import be.webtechie.controllingtheduke.view.snake.SnakeGame;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class TestSnake extends Application {
    @Override
    public void start(Stage stage) {
        Scene scene = new Scene(new SnakeGame(600, 600), 600, 600, true);
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
