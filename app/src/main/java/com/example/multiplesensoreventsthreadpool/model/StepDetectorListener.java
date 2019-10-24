package com.example.multiplesensoreventsthreadpool.model;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.util.Log;
import java.io.File;
import java.util.concurrent.ThreadPoolExecutor;
import static android.content.ContentValues.TAG;

/***
 * Class for the Android Step detector listener setting how it record and write data
 */
public class StepDetectorListener extends SensorListener {
    public StepDetectorListener(Context context, ThreadPoolExecutor pool, File file, SensorRecord record) {
        super(context, pool, file, record);
        setSensor(getSensorManager().getDefaultSensor(Sensor.TYPE_STEP_DETECTOR));

    }

    /***
     * Record any change detected step and write that in a new line of the CSV file
     * sending the runnable object to the thread pool
     * @param event
     */
    @Override
    public void onSensorChanged(SensorEvent event) {
        // Log.d(TAG, "onSensorChanged: Step Count Detected!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
        Log.d(TAG, "PoolSize: " + pool.getPoolSize());
        String entry  = getRecord().getaX() + "," +
                getRecord().getaY() + "," +
                getRecord().getaZ() + "," +
                getRecord().getgX() + "," +
                getRecord().getgY() + "," +
                getRecord().getgZ() + "," +
                String.valueOf(System.currentTimeMillis()) + "," +
                getCurrentTimeWithMillisec() + "," +
                "0," +
                "1," +
                "androidStepDetector";
        WriteFileRunnable writeStepCounter = new WriteFileRunnable(entry, getFile());
        getPool().execute(writeStepCounter);
    }
}
