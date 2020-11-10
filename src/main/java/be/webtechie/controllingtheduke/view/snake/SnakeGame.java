package be.webtechie.controllingtheduke.view.snake;

import be.webtechie.controllingtheduke.gpio.DistanceSensor;
import be.webtechie.controllingtheduke.util.DistanceChangeListener;
import java.util.Random;
import javafx.animation.AnimationTimer;
import javafx.geometry.Point3D;
import javafx.scene.Group;
import javafx.scene.PerspectiveCamera;
import javafx.scene.SubScene;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Translate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * JavaFX 3D Snake game as explained by Almas Baimagambetov on https://www.youtube.com/watch?v=mjfgGJHAuvI&feature=youtu.be
 */
public class SnakeGame extends Pane implements DistanceChangeListener {

    private static final Logger logger = LogManager.getLogger(SnakeGame.class);

    private Point3D dir = new Point3D(1, 0, 0);
    private Point3D next = new Point3D(0, 0, 0);

    private double t = 0;
    private final AnimationTimer timer;

    private final Group snake;

    private Cube food = new Cube(Color.YELLOW);

    private Random random = new Random();

    public SnakeGame(int width, int height) {
        this.setStyle("-fx-background-color: green;");
        this.setMinWidth(width);
        this.setMinHeight(height);

        this.snake = new Group();
        Cube cube = new Cube(Color.BLUE);
        snake.getChildren().add(cube);

        this.getChildren().addAll(snake, food);

        SubScene scene3d = new SubScene(this, width, height);
        scene3d.setFill(Color.rgb(10, 10, 40));
        scene3d.setOnKeyPressed(e -> {
            logger.info("Key press in 3D scene: {}", e.getCode());
            switch (e.getCode()) {
                case UP:
                    dir = new Point3D(0, -1, 0);
                    break;
                case DOWN:
                    dir = new Point3D(0, 1, 0);
                    break;
                case LEFT:
                    dir = new Point3D(-1, 0, 0);
                    break;
                case RIGHT:
                    dir = new Point3D(1, 0, 0);
                    break;
            }
        });

        PerspectiveCamera camera = new PerspectiveCamera(true);
        /*camera.getTransforms().addAll(
                new Translate(0, -20, -20),
                new Rotate(-45, Rotate.X_AXIS));*/
        scene3d.setCamera(camera);

        this.timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                t += 0.016;
                if (t > 0.1) {
                    onUpdate();
                    t = 0;
                }
            }
        };
        this.timer.start();
    }

    private void moveFood() {
        food.setTranslateX(random.nextInt(10) - 5);
        food.setTranslateY(random.nextInt(10) - 5);
        food.setTranslateZ(random.nextInt(10) - 5);
    }

    private void grow() {
        this.moveFood();
        Cube cube = new Cube(Color.BLUE);
        cube.set(next.add(dir));

        snake.getChildren().add(cube);
    }

    private void onUpdate() {
        next = next.add(dir);
        Cube cube = (Cube) snake.getChildren().remove(0);
        cube.set(next);
        snake.getChildren().add(cube);

        boolean collision = snake.getChildren().stream()
                .map(n -> (Cube) n)
                .anyMatch(c -> c.isColliding(food));

        if (collision) {
            this.grow();
        }
    }

    @Override
    public void handleDistanceChange(DistanceSensor distanceSensor, DistanceChange distanceChange, float distance) {
        if (distanceSensor.getId() == 1) {
            switch (distanceChange) {
                case CLOSER:
                    // UP
                    this.dir = new Point3D(0, 1, 0);
                    break;
                case FARTHER:
                    // DOWN
                    this.dir = new Point3D(0, -1, 0);
                    break;
            }
        } else {
            switch (distanceChange) {
                case CLOSER:
                    // LEFT
                    this.dir = new Point3D(1, 0, 0);
                    break;
                case FARTHER:
                    // RIGHT
                    this.dir = new Point3D(-1, 0, 0);
                    break;
            }
        }
    }
}
