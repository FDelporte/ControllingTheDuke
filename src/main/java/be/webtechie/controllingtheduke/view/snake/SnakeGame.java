package be.webtechie.controllingtheduke.view.snake;

import be.webtechie.controllingtheduke.gpio.DistanceSensor;
import be.webtechie.controllingtheduke.util.DistanceChangeListener;
import javafx.animation.AnimationTimer;
import javafx.geometry.Point3D;
import javafx.scene.Group;
import javafx.scene.PerspectiveCamera;
import javafx.scene.SubScene;
import javafx.scene.paint.Color;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Translate;

public class SnakeGame extends Group implements DistanceChangeListener {

    private Point3D dir = new Point3D(1, 0, 0);
    private Point3D next = new Point3D(0, 0, 0);
    
    private double t = 0;
    private final AnimationTimer timer;

    private final Group snake;

    public SnakeGame() {
        this.snake = new Group();
        Cube cube = new Cube(Color.BLUE);
        snake.getChildren().add(cube);
        this.getChildren().add(snake);

        SubScene scene3d = new SubScene(this, 400, 400);
        scene3d.setFill(Color.rgb(10, 10, 40));

        PerspectiveCamera camera = new PerspectiveCamera(true);
        camera.getTransforms().addAll(
                new Translate(0, -20, -20),
                new Rotate(-45, Rotate.X_AXIS));
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

    private void onUpdate() {
        next = next.add(dir);
        Cube cube = (Cube) snake.getChildren().remove(0);
        cube.set(next);
        snake.getChildren().add(cube);
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
