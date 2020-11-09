package be.webtechie.controllingtheduke.view;

import be.webtechie.controllingtheduke.gpio.DistanceSensor;
import be.webtechie.controllingtheduke.util.DistanceChangeListener;
import be.webtechie.controllingtheduke.view.snake.SnakeGame;
import eu.hansolo.tilesfx.Tile;
import eu.hansolo.tilesfx.Tile.SkinType;
import eu.hansolo.tilesfx.TileBuilder;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class MainScreen extends HBox implements DistanceChangeListener {

    private final Label lblKey;
    private final Label lblDistance1;
    private final Label lblDistance2;

    private final Tile distance1Tile;
    private final Tile distance2Tile;

    public MainScreen() {
        VBox tiles = new VBox();
        tiles.setPadding(new Insets(5));
        tiles.setSpacing(5);
        this.getChildren().add(tiles);
        this.distance1Tile = TileBuilder.create()
                .skinType(SkinType.GAUGE)
                .prefSize(200, 200)
                .title("Distance sensor 1")
                .unit("cm")
                .maxValue(255)
                .build();
        this.distance2Tile = TileBuilder.create()
                .skinType(SkinType.GAUGE)
                .prefSize(200, 200)
                .title("Distance sensor 2")
                .unit("cm")
                .maxValue(255)
                .build();
        tiles.getChildren().addAll(
                TileBuilder.create()
                        .skinType(SkinType.TEXT)
                        .prefSize(2, 200)
                        .title("System info")
                        .description("Java: " + System.getProperty("java.version")
                                + "\nJavaFX: " + System.getProperty("javafx.version")
                                + "\nArchitecture: " + System.getProperty("os.arch")
                                + "\nBits: " + System.getProperty("sun.arch.data.model"))
                        .descriptionAlignment(Pos.TOP_CENTER)
                        .textVisible(true)
                        .build(),
                this.distance1Tile,
                this.distance2Tile);

        VBox labels = new VBox();
        this.getChildren().add(labels);
        this.lblKey = new Label();
        this.lblDistance1 = new Label();
        this.lblDistance2 = new Label();
        labels.getChildren().addAll(this.lblKey, this.lblDistance1, this.lblDistance2,
                new SnakeGame());
    }

    public void handleKeyChange(KeyCode keyCode, String txt) {
        this.lblKey.setText(this.getTimestamp() + " - " + keyCode + " - Key change: " + txt);
    }

    @Override
    public void handleDistanceChange(DistanceSensor distanceSensor, DistanceChange distanceChange, float distance) {
        Platform.runLater(() -> {
            if (distanceSensor.getId() == 1) {
                lblDistance1.setText(getTimestamp()
                        + " + Distance change: " + distanceChange.name() + " to " + distance + "cm");
                distance1Tile.setValue(distance);
            } else {
                lblDistance2.setText(getTimestamp()
                        + " + Distance change: " + distanceChange.name() + " to " + distance + "cm");
                distance2Tile.setValue(distance);
            }
        });
    }


    private String getTimestamp() {
        DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss.SSS");
        return dateFormat.format(new Date());
    }
}
