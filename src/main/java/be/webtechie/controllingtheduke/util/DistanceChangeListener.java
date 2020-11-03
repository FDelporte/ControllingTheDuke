package be.webtechie.controllingtheduke.util;

public interface DistanceChangeListener {

    enum DistanceChange {
        CLOSER,
        FARTHER,
        NONE
    }

    void handleDistanceChange(DistanceChange distanceChange, float distance);

}
