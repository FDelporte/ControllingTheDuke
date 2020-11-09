package be.webtechie.controllingtheduke.gpio;

import com.pi4j.io.gpio.GpioPinDigitalInput;
import com.pi4j.io.gpio.GpioPinDigitalOutput;

public class DistanceSensor {

    private final int id;
    private final GpioPinDigitalOutput trigger;
    private final GpioPinDigitalInput echo;

    public DistanceSensor(int id, GpioPinDigitalOutput trigger, GpioPinDigitalInput echo) {
        if (trigger == null || echo == null) {
            throw new IllegalArgumentException("Distance sensor pins not initialized");
        }

        this.id = id;
        this.trigger = trigger;
        this.echo = echo;
    }

    public int getId() {
        return id;
    }

    public GpioPinDigitalOutput getTrigger() {
        return trigger;
    }

    public GpioPinDigitalInput getEcho() {
        return echo;
    }
}
