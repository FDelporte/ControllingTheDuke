package be.webtechie.controllingtheduke.util;

import be.webtechie.controllingtheduke.gpio.GpioHelper;
import javafx.application.Platform;

/**
 * Helper class to nicely close the GPIO controller and JavaFX application.
 */
public class CleanExit {

    private CleanExit() {
        // Hide constructor
    }

    /**
     * Close the GPIO controller and application.
     *
     * @param gpioHelper {@link GpioHelper}
     */
    public static void doExit(GpioHelper gpioHelper) {
        if (gpioHelper != null) {
            gpioHelper.getGpioController().shutdown();
        }
        Platform.exit();
        System.exit(0);
    }
}
