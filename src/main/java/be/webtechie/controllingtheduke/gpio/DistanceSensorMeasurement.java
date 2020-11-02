package be.webtechie.controllingtheduke.gpio;

import com.pi4j.io.gpio.GpioPinDigitalInput;
import com.pi4j.io.gpio.GpioPinDigitalOutput;
import com.pi4j.io.gpio.PinState;
import com.pi4j.io.gpio.event.GpioPinDigitalStateChangeEvent;
import com.pi4j.io.gpio.event.GpioPinListenerDigital;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Measures the distance value which is done in three steps:
 *
 * <ul>
 *     <li>Set trigger high for 0.01ms</li>
 *     <li>Wait until the echo pin is high, indicating the ultrasound was sent</li>
 *     <li>Wait until the echo pin is low,  indicating the ultrasound was received back</li>
 *     <li>Time difference between high and low indicates duration of distance measurement</li>
 * </ul>
 */
public class DistanceSensorMeasurement implements GpioPinListenerDigital {

    private static final Logger logger = LogManager.getLogger(DistanceSensorMeasurement.class);

    private static final int DISTANCE_CHANGE_THRESHOLD_CM = 1;

    enum MeasurementState {
        NEED_TRIGGER,
        WAITING_FOR_HIGH,
        WAITING_FOR_LOW
    }

    enum DistanceChange {
        CLOSER,
        FARTHER,
        NONE
    }

    private MeasurementState currentState = MeasurementState.NEED_TRIGGER;
    private DistanceChange distanceChange = DistanceChange.NONE;

    /**
     * The GPIO's connected to the distance sensor.
     */
    private final GpioPinDigitalOutput trigger;
    private final GpioPinDigitalInput echo;

    private long measurementStart = 0;
    private long measurementEnd = 0;
    private int previousDistance = 0;

    /**
     * Constructor
     */
    public DistanceSensorMeasurement(GpioPinDigitalOutput trigger, GpioPinDigitalInput echo) {
        if (trigger == null || echo == null) {
            throw new IllegalArgumentException("Distance sensor pins not initialized");
        }
        this.trigger = trigger;
        this.echo = echo;
        this.echo.addListener(this);

        ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
        executorService.scheduleAtFixedRate(new SendTrigger(), 1000, 250, TimeUnit.MILLISECONDS);
    }

    /**
     * Get the measurement distance (in cm). The calculation is based on the speed of sound which is 34300 cm/s. Since
     * the sound is making a round trip, the distance is divided by 2.
     *
     * @return Distance in cm
     */
    public int getDistance() {
        return this.previousDistance;
    }

    /**
     * The distance change between two last measurements.
     *
     * @return {@link DistanceChange}
     */
    public DistanceChange getChange() {
        return this.distanceChange;
    }

    /**
     * Event handler for the button
     */
    @Override
    public void handleGpioPinDigitalStateChangeEvent(GpioPinDigitalStateChangeEvent event) {
        logger.info("Button state changed to {}", event.getState().isHigh() ? "high" : "low");
        if (this.currentState.equals(MeasurementState.WAITING_FOR_HIGH) && event.getState().isHigh()) {
            this.measurementStart = System.nanoTime();
            logger.info("High state received at {}", this.measurementStart);
            this.currentState = MeasurementState.WAITING_FOR_LOW;
            return;
        }
        if (this.currentState.equals(MeasurementState.WAITING_FOR_LOW) && event.getState().isLow()) {
            this.measurementEnd = System.nanoTime();
            logger.info("Low state received at {}", this.measurementStart);
            float measurement = (this.measurementEnd - this.measurementStart) / 1000000000F;
            int newDistance = Math.round(measurement * 34300 / 2);
            logger.info("Measured distance: {}cm - previous: {}cm", newDistance, this.previousDistance);
            if (newDistance < this.previousDistance - DISTANCE_CHANGE_THRESHOLD_CM) {
                this.distanceChange = DistanceChange.CLOSER;
                this.previousDistance = newDistance;
            } else if (newDistance > this.previousDistance + DISTANCE_CHANGE_THRESHOLD_CM) {
                this.distanceChange = DistanceChange.FARTHER;
                this.previousDistance = newDistance;
            } else {
                this.distanceChange = DistanceChange.NONE;
            }
            logger.info("Distance change: {}", this.distanceChange);
            this.currentState = MeasurementState.NEED_TRIGGER;
            return;
        }
        logger.warn("Unexpected event state");
        this.currentState = MeasurementState.NEED_TRIGGER;
    }

    private class SendTrigger implements Runnable {

        /**
         * Perform a distance measurement and add the result to the data.
         */
        @Override
        public void run() {
            if (currentState.equals(MeasurementState.NEED_TRIGGER)) {
                measurementStart = 0;
                measurementEnd = 0;
                // Set trigger high for 0.01ms
                trigger.pulse(10, PinState.HIGH, true, TimeUnit.NANOSECONDS);
            }
        }
    }
}