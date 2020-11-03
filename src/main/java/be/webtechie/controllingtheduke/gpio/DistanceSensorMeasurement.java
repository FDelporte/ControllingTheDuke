package be.webtechie.controllingtheduke.gpio;

import be.webtechie.controllingtheduke.util.DistanceChangeListener;
import be.webtechie.controllingtheduke.util.DistanceChangeListener.DistanceChange;
import com.pi4j.io.gpio.GpioPinDigitalInput;
import com.pi4j.io.gpio.GpioPinDigitalOutput;
import com.pi4j.io.gpio.PinState;
import java.util.ArrayList;
import java.util.List;
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
public class DistanceSensorMeasurement {

    private static final Logger logger = LogManager.getLogger(DistanceSensorMeasurement.class);

    private static final int DISTANCE_CHANGE_THRESHOLD_CM = 1;

    private DistanceChange distanceChange = DistanceChange.NONE;

    /**
     * The GPIO's connected to the distance sensor.
     */
    private final GpioPinDigitalOutput trigger;
    private final GpioPinDigitalInput echo;

    private float measuredDistance = 0;

    private List<DistanceChangeListener> listeners = new ArrayList<>();

    /**
     * Constructor
     */
    public DistanceSensorMeasurement(GpioPinDigitalOutput trigger, GpioPinDigitalInput echo) {
        if (trigger == null || echo == null) {
            throw new IllegalArgumentException("Distance sensor pins not initialized");
        }
        this.trigger = trigger;
        this.echo = echo;

        ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
        executorService.scheduleAtFixedRate(new RunMeasurement(), 1000, 250, TimeUnit.MILLISECONDS);

        logger.info("Distance measurement initialized");
    }

    /**
     * Add a listener which will get distance changes.
     *
     * @param listener {@link DistanceChangeListener}
     */
    public void addListener(DistanceChangeListener listener) {
        this.listeners.add(listener);
    }

    /**
     * Set the distance change.
     *
     * @param distanceChange {@link DistanceChange}
     * @param distance the measured distance
     */
    private void setDistanceChange(DistanceChange distanceChange, float distance) {
        this.measuredDistance = distance;
        if (this.distanceChange.equals(distanceChange)) {
            return;
        }
        this.distanceChange = distanceChange;
        for (DistanceChangeListener listener : this.listeners) {
            listener.handleDistanceChange(distanceChange, this.measuredDistance);
        }
    }

    private class RunMeasurement implements Runnable {

        /**
         * Perform a distance measurement and add the result to the data.
         */
        @Override
        public void run() {
            // Set trigger high for 0.01ms
            trigger.pulse(10, PinState.HIGH, true, TimeUnit.NANOSECONDS);

            // Start the measurement
            while (echo.isLow()) {
                // Wait until the echo pin is high, indicating the ultrasound was sent
            }
            long measurementStart = System.nanoTime();

            // Wait till measurement is finished
            while (echo.isHigh()) {
                // Wait until the echo pin is low,  indicating the ultrasound was received back
            }
            long measurementEnd = System.nanoTime();

            float measurement = (measurementEnd - measurementStart) / 1000000000F;
            float newDistance = measurement * 34300 / 2;
            logger.info("Measured distance: {}cm - previous: {}cm", newDistance, measuredDistance);
            if (newDistance < measuredDistance - DISTANCE_CHANGE_THRESHOLD_CM) {
                setDistanceChange(DistanceChange.CLOSER, newDistance);
            } else if (newDistance > measuredDistance + DISTANCE_CHANGE_THRESHOLD_CM) {
                setDistanceChange(DistanceChange.FARTHER, newDistance);
            } else {
                setDistanceChange(DistanceChange.NONE, measuredDistance);
            }
        }
    }
}