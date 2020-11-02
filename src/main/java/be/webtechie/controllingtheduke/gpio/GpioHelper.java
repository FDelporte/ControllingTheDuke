package be.webtechie.controllingtheduke.gpio;

import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.GpioPinDigitalInput;
import com.pi4j.io.gpio.GpioPinDigitalOutput;
import com.pi4j.io.gpio.Pin;
import com.pi4j.io.gpio.PinPullResistance;
import com.pi4j.io.gpio.PinState;
import com.pi4j.io.gpio.RaspiPin;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Helper class which bundles the Pi4J functions.
 */
public class GpioHelper {

    private static final Logger logger = LogManager.getLogger(GpioHelper.class);

    /**
     * The pins we are using in our example.
     */
    private static final Pin PIN_ECHO = RaspiPin.GPIO_05;       // BCM 24, Header pin 18
    private static final Pin PIN_TRIGGER = RaspiPin.GPIO_01;    // BCM 18, Header pin 12

    /**
     * The connected hardware components.
     */
    private GpioController gpioController;

    /**
     * The GPIO handlers.
     */
    private DistanceSensorMeasurement distanceSensorMeasurement = null;

    /**
     * Constructor.
     */
    public GpioHelper() {
        try {
            // Initialize the GPIO controller
            this.gpioController = GpioFactory.getInstance();

            // Initialize the pins for the distance sensor and start thread
            GpioPinDigitalOutput trigger = gpioController
                    .provisionDigitalOutputPin(PIN_TRIGGER, "Trigger", PinState.LOW);
            GpioPinDigitalInput echo = gpioController
                    .provisionDigitalInputPin(PIN_ECHO, "Echo", PinPullResistance.PULL_UP);
            this.distanceSensorMeasurement = new DistanceSensorMeasurement(trigger, echo);
        } catch (UnsatisfiedLinkError | IllegalArgumentException ex) {
            logger.error("Problem with Pi4J! Probably running on non-Pi-device or Pi4J not installed. Error: {}",
                    ex.getMessage());
        }
    }

    /**
     * @return {@link GpioController}
     */
    public GpioController getGpioController() {
        return this.gpioController;
    }

    /**
     * @return {@link DistanceSensorMeasurement}
     */
    public DistanceSensorMeasurement getDistanceSensorMeasurement() {
        return this.distanceSensorMeasurement;
    }
}