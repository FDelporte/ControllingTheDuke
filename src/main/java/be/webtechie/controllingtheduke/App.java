package be.webtechie.controllingtheduke;

import be.webtechie.controllingtheduke.gpio.GpioHelper;
import be.webtechie.controllingtheduke.util.CleanExit;
import be.webtechie.controllingtheduke.view.Duke;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;

/**
 * MoleculeSampleApp
 */
public class App extends Application {

    private DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss.SSS");

    private GpioHelper gpioHelper;
    private Duke duke;

    private void handleKeyboard(Scene scene) {
        scene.setOnKeyPressed(event -> {
            System.out.println(event.getCode());
            switch (event.getCode()) {
                case UP:
                    this.duke.log(this.getTimestamp() + " move forward");
                    break;
                case DOWN:
                    this.duke.log(this.getTimestamp() + " move backward");
                    break;
            }
        });
    }

    private String getTimestamp() {
        return this.dateFormat.format(new Date());
    }

    @Override
    public void start(Stage stage) {
        Platform.setImplicitExit(true);

        this.gpioHelper = new GpioHelper();
        this.duke = new Duke(this.gpioHelper);

        var scene = new Scene(this.duke, 640, 480);
        stage.setScene(scene);
        stage.setTitle("JavaFX demo application on Raspberry Pi");
        stage.show();
        handleKeyboard(scene);

        // Make sure the application quits completely on close
        stage.setOnCloseRequest(t -> CleanExit.doExit(this.gpioHelper.getGpioController()));
    }

    /**
     * The main() method is ignored in correctly deployed JavaFX application. main() serves only as fallback in case the
     * application can not be launched through deployment artifacts.
     *
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
}
