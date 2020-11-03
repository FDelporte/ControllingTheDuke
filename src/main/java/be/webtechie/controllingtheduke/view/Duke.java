package be.webtechie.controllingtheduke.view;

import be.webtechie.controllingtheduke.util.DistanceChangeListener;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import javafx.application.Platform;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

public class Duke extends VBox implements DistanceChangeListener {

    private final Label lblKey;
    private final Label lblDistance;

    public Duke() {
        this.lblKey = new Label();
        this.lblDistance = new Label();
        this.getChildren().addAll(this.lblKey, this.lblDistance);
    }

    public void handleKeyChange(String txt) {
        this.lblKey.setText(this.getTimestamp() + " + Key change: " + txt);
    }

    @Override
    public void handleDistanceChange(DistanceChange distanceChange, float distance) {
        Platform.runLater(() -> lblDistance.setText(getTimestamp()
                + " + Distance change: " + distanceChange.name() + " to " + distance + "cm"));
    }


    private String getTimestamp() {
        DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss.SSS");
        return dateFormat.format(new Date());
    }
}
