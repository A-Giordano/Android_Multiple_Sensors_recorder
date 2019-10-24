package com.example.multiplesensoreventsthreadpool.model;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.ThreadPoolExecutor;
import static android.support.constraint.Constraints.TAG;

/***
 * Abstract class for Sensor Listeners with the common methods
 */
public abstract class SensorListener implements SensorEventListener {
    private SensorManager sensorManager;
    private Sensor sensor;
    public ThreadPoolExecutor pool;
    private File file;
    private SensorRecord record;

    public SensorListener(Context context, ThreadPoolExecutor pool, File file, SensorRecord record){
        sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        sensor = null;
        this.pool = pool;
        this.file = file;
        this.record = record;
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy){
        Log.d(TAG,"onAccuracyChanged: " + sensor + ", accuracy: " + accuracy);
    }

    @Override
    public abstract void onSensorChanged(SensorEvent event);

    /***
     * Register the listener
     */
    public void register(){
        sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_FASTEST);
    }

    /***
     * Unregister the listener
     */
    public void unregister(){
        sensorManager.unregisterListener(this);
    }
    /***
     *  Get precise instance of time
     */
    public static String getCurrentTimeWithMillisec() {
        SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        Date now = new Date();
        String strDate = sdfDate.format(now);
        return strDate;
    }

    public SensorManager getSensorManager() {
        return sensorManager;
    }

    public Sensor getSensor() {
        return sensor;
    }

    public ThreadPoolExecutor getPool() {
        return pool;
    }

    public File getFile() {
        return file;
    }

    public void setSensor(Sensor sensor) {
        this.sensor = sensor;
    }

    public SensorRecord getRecord() {
        return record;
    }

    public void setRecord(SensorRecord record) {
        this.record = record;
    }
}