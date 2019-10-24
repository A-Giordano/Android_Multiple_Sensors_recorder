package com.example.multiplesensoreventsthreadpool.model;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.util.Log;
import java.io.File;
import java.util.concurrent.ThreadPoolExecutor;
import static android.content.ContentValues.TAG;

/***
 * Class for the Accelerometer listener setting how it record and write data
 */
public class AccelerometerListener extends SensorListener {
    public AccelerometerListener(Context context, ThreadPoolExecutor pool, File file, SensorRecord record) {
        super(context, pool, file, record);
        setSensor(getSensorManager().getDefaultSensor(Sensor.TYPE_ACCELEROMETER));

    }

    /***
     * Record any change in the values of the accelerometer over the 3 axes and write these in a new line of the CSV file
     * sending the runnable object to the thread pool
     * @param event
     */
    @Override
    public void onSensorChanged(SensorEvent event) {
        getRecord().setaX(event.values[0]);
        getRecord().setaY(event.values[1]);
        getRecord().setaZ(event.values[2]);
        Log.d(TAG, "PoolSize: " + pool.getPoolSize());
        String entry  = event.values[0] + "," +
                event.values[1] + "," +
                event.values[2] + "," +
                getRecord().getgX() + "," +
                getRecord().getgY() + "," +
                getRecord().getgZ() + "," +
                String.valueOf(System.currentTimeMillis()) + "," +
                getCurrentTimeWithMillisec() + "," +
                "0," +
                "0," +
                "accelerometer";
        WriteFileRunnable writeAccelerometer = new WriteFileRunnable(entry, getFile());
        getPool().execute(writeAccelerometer);
    }
}
