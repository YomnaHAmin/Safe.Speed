package com.yomnahamin.safetyspeed;

/**
 * Created by user on 18-Aug-18.
 */

public class Segment {
    private double startLat, startLng, endLat, endLng, safeSpeed;

    public Segment(double startLat, double startLng, double endLat, double endLng, double safeSpeed) {
        this.startLat = startLat;
        this.startLng = startLng;
        this.endLat = endLat;
        this.endLng = endLng;
        this.safeSpeed = safeSpeed;
    }

    public double getStartLat() {
        return startLat;
    }

    public double getStartLng() {
        return startLng;
    }

    public double getEndLat() {
        return endLat;
    }

    public double getEndLng() {
        return endLng;
    }

    public double getSafeSpeed() {
        return safeSpeed;
    }

    public void setStartLat(double startLat) {
        this.startLat = startLat;
    }

    public void setStartLng(double startLng) {
        this.startLng = startLng;
    }

    public void setEndLat(double endLat) {
        this.endLat = endLat;
    }

    public void setEndLng(double endLng) {
        this.endLng = endLng;
    }

    public void setSafeSpeed(double safeSpeed) {
        this.safeSpeed = safeSpeed;
    }
}
