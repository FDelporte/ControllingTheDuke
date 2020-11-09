package be.webtechie.controllingtheduke;

import be.webtechie.controllingtheduke.gpio.GpioHelper;
import be.webtechie.controllingtheduke.util.CleanExit;
import be.webtechie.controllingtheduke.view.MainScreen;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * MoleculeSampleApp
 */
public class App extends Application {

    private static Logger logger = LogManager.getLogger(App.class);

    private GpioHelper gpioHelper;
    private MainScreen mainScreen;

    private void handleKeyboard(Scene scene) {
        scene.setOnKeyPressed(event -> {
            logger.info("Key event: {}", event.getCode());
            switch (event.getCode()) {
                case UP:
                    this.mainScreen.handleKeyChange(event.getCode(), "move forward");
                    break;
                case DOWN:
                    this.mainScreen.handleKeyChange(event.getCode(), "move backward");
                    break;
                default:
                    // Key not used in this project
            }
        });
    }


    @Override
    public void start(Stage stage) {
        Platform.setImplicitExit(true);

        this.mainScreen = new MainScreen();

        //this.gpioHelper = new GpioHelper();
        //this.gpioHelper.getDistanceSensorMeasurement().addListener(this.mainScreen);

        var scene = new Scene(this.mainScreen, 640, 480);
        stage.setScene(scene);
        stage.setTitle("JavaFX demo application on Raspberry Pi");
        stage.show();
        handleKeyboard(scene);

        // Make sure the application quits completely on close
        stage.setOnCloseRequest(t -> CleanExit.doExit(this.gpioHelper));

        logger.info("Stage initialized");
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
