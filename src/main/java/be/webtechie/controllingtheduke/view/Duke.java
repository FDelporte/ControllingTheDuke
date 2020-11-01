package be.webtechie.controllingtheduke.view;

import be.webtechie.controllingtheduke.gpio.GpioHelper;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

public class Duke extends VBox {

    private final GpioHelper gpioHelper;
    private final Label lbl;

    public Duke(GpioHelper gpioHelper) {
        this.gpioHelper = gpioHelper;

        this.lbl = new Label("test");
        this.getChildren().add(lbl);
    }

    public void log(String txt) {
        this.lbl.setText(txt);
    }
}
