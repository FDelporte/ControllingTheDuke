package be.webtechie.controllingtheduke.util;

import be.webtechie.controllingtheduke.gpio.DistanceSensor;

public interface DistanceChangeListener {

    enum DistanceChange {
        CLOSER,
        FARTHER,
        NONE
    }

    void handleDistanceChange(DistanceSensor distanceSensor, DistanceChange distanceChange, float distance);
}
