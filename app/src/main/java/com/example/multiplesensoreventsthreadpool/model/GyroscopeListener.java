package com.example.multiplesensoreventsthreadpool.model;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.util.Log;
import java.io.File;
import java.util.concurrent.ThreadPoolExecutor;
import static android.content.ContentValues.TAG;

/***
 * Class for the Gyroscope listener setting how it record and write data
 */
public class GyroscopeListener extends SensorListener {
    public GyroscopeListener(Context context, ThreadPoolExecutor pool, File file, SensorRecord record) {
        super(context, pool, file, record);
        setSensor(getSensorManager().getDefaultSensor(Sensor.TYPE_GYROSCOPE));

    }

    /***
     * Record any change in the values of the gyroscope over the 3 axes and write these in a new line of the CSV file
     * sending the runnable object to the thread pool
     * @param event
     */
    @Override
    public void onSensorChanged(SensorEvent event) {
        getRecord().setgX(event.values[0]);
        getRecord().setgY(event.values[1]);
        getRecord().setgZ(event.values[2]);
        Log.d(TAG, "PoolSize: " + pool.getPoolSize());
        String entry  = getRecord().getaX() + "," +
                getRecord().getaY() + "," +
                getRecord().getaZ() + "," +
                event.values[0] + "," +
                event.values[1] + "," +
                event.values[2] + "," +
                String.valueOf(System.currentTimeMillis()) + "," +
                getCurrentTimeWithMillisec() + "," +
                "0," +
                "0," +
                "gyroscope";
        WriteFileRunnable writeGyroscope = new WriteFileRunnable(entry, getFile());
        getPool().execute(writeGyroscope);
    }
}
