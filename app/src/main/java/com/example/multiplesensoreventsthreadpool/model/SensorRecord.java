package com.example.multiplesensoreventsthreadpool.model;

/***
 * Class where to store the latest values of the Accelerometer and Gyroscope values
 */
public class SensorRecord {
    private float aX;
    private float aY;
    private float aZ;
    private float gX;
    private float gY;
    private float gZ;

    public SensorRecord() {
        aX = 0;
        aY = 0;
        aZ = 0;
        gX = 0;
        gY = 0;
        gZ = 0;
    }

    public float getaX() {
        return aX;
    }

    public void setaX(float aX) {
        this.aX = aX;
    }

    public float getaY() {
        return aY;
    }

    public void setaY(float aY) {
        this.aY = aY;
    }

    public float getaZ() {
        return aZ;
    }

    public void setaZ(float aZ) {
        this.aZ = aZ;
    }

    public float getgX() {
        return gX;
    }

    public void setgX(float gX) {
        this.gX = gX;
    }

    public float getgY() {
        return gY;
    }

    public void setgY(float gY) {
        this.gY = gY;
    }

    public float getgZ() {
        return gZ;
    }

    public void setgZ(float gZ) {
        this.gZ = gZ;
    }
}
