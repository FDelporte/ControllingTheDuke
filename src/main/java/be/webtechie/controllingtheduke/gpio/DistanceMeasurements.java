package be.webtechie.controllingtheduke.gpio;

import be.webtechie.controllingtheduke.util.DistanceChangeListener;
import be.webtechie.controllingtheduke.util.DistanceChangeListener.DistanceChange;
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
public class DistanceMeasurements {

    private static final Logger logger = LogManager.getLogger(DistanceMeasurements.class);

    private static final int DISTANCE_CHANGE_THRESHOLD_CM = 1;

    private DistanceChange distanceChange = DistanceChange.NONE;

    private float measuredDistance = 0;

    private final List<DistanceSensor> distanceSensors;
    private final List<DistanceChangeListener> listeners = new ArrayList<>();

    /**
     * Constructor
     */
    public DistanceMeasurements(List<DistanceSensor> distanceSensors) {
        this.distanceSensors = distanceSensors;

        ScheduledExecutorService executorService = Executors.newScheduledThreadPool(distanceSensors.size());
        for (DistanceSensor distanceSensor : distanceSensors) {
            executorService.scheduleAtFixedRate(new RunMeasurement(distanceSensor), 1000, 1000, TimeUnit.MILLISECONDS);
        }

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
     * @param distanceSensor {@link DistanceSensor}
     * @param distanceChange {@link DistanceChange}
     * @param distance       the measured distance
     */
    private void setDistanceChange(DistanceSensor distanceSensor, DistanceChange distanceChange, float distance) {
        this.measuredDistance = distance;
        if (this.distanceChange.equals(distanceChange)) {
            return;
        }
        this.distanceChange = distanceChange;
        for (DistanceChangeListener listener : this.listeners) {
            listener.handleDistanceChange(distanceSensor, distanceChange, this.measuredDistance);
        }
    }

    private class RunMeasurement implements Runnable {

        private static final long TIME_OUT = 250 * 1000000000;
        private final DistanceSensor distanceSensor;

        public RunMeasurement(DistanceSensor distanceSensor) {
            this.distanceSensor = distanceSensor;
        }

        /**
         * Perform a distance measurement and add the result to the data.
         */
        @Override
        public void run() {
            // Set trigger high for 0.01ms
            this.distanceSensor.getTrigger().pulse(10, PinState.HIGH, true, TimeUnit.NANOSECONDS);
            long preventTimeOut = System.nanoTime();

            // Start the measurement
            while (this.distanceSensor.getEcho().isLow()) {
                // Wait until the echo pin is high, indicating the ultrasound was sent
                if (System.nanoTime() > preventTimeOut + TIME_OUT) {
                    logger.error("Measurement time-out on first wait");
                    return;
                }
            }
            long measurementStart = System.nanoTime();

            // Wait till measurement is finished
            while (this.distanceSensor.getEcho().isHigh()) {
                // Wait until the echo pin is low,  indicating the ultrasound was received back
                if (System.nanoTime() > preventTimeOut + TIME_OUT) {
                    logger.error("Measurement time-out on second wait");
                    return;
                }
            }
            long measurementEnd = System.nanoTime();

            float measurement = (measurementEnd - measurementStart) / 1000000000F;
            float newDistance = measurement * 34300 / 2;
            logger.info("Measured distance: {}cm - previous: {}cm", newDistance, measuredDistance);
            if (newDistance < measuredDistance - DISTANCE_CHANGE_THRESHOLD_CM) {
                setDistanceChange(this.distanceSensor, DistanceChange.CLOSER, newDistance);
            } else if (newDistance > measuredDistance + DISTANCE_CHANGE_THRESHOLD_CM) {
                setDistanceChange(this.distanceSensor, DistanceChange.FARTHER, newDistance);
            } else {
                setDistanceChange(this.distanceSensor, DistanceChange.NONE, measuredDistance);
            }
        }
    }
}