package com.example.multiplesensoreventsthreadpool;


import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Environment;
import android.os.StrictMode;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;


import com.example.multiplesensoreventsthreadpool.model.AccelerometerListener;
import com.example.multiplesensoreventsthreadpool.model.GyroscopeListener;
import com.example.multiplesensoreventsthreadpool.model.SensorRecord;
import com.example.multiplesensoreventsthreadpool.model.StepDetectorListener;
import com.example.multiplesensoreventsthreadpool.model.WriteFileRunnable;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private AccelerometerListener accListener;
    private GyroscopeListener gyroListener;
    private StepDetectorListener stepListener;
    private SensorRecord sensorRecord;
    private ImageButton play, stop;
    private File finalData;
    private ThreadPoolExecutor mThreadPoolExecutor;
    private static final int MY_PERMISSIONS_WRITE_EXTERNAL_STORAGE = 1;
    private boolean playClicked;
    int NUMBER_OF_CORES = Runtime.getRuntime().availableProcessors();
    int KEEP_ALIVE_TIME = 1;
    TimeUnit KEEP_ALIVE_TIME_UNIT = TimeUnit.SECONDS;
    BlockingQueue<Runnable> taskQueue = new LinkedBlockingQueue<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        checkWritePermission();
        setContentView(R.layout.activity_main);
        playClicked = false;
        finalData = new File(Environment.getExternalStorageDirectory(), "final_data.csv");
        clearFile(finalData);
        //initialize the thread pool executor
        mThreadPoolExecutor = new ThreadPoolExecutor(NUMBER_OF_CORES, NUMBER_OF_CORES * 2, KEEP_ALIVE_TIME, KEEP_ALIVE_TIME_UNIT, taskQueue);

        play = findViewById(R.id.play);
        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playButton();
            }
        });
        stop = findViewById(R.id.stop);
        stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopButton();
            }
        });
        Log.d(TAG, "onCreate: initializing Sensor Services");
        sensorRecord = new SensorRecord();
        //initialize accelerometer listener
        accListener = new AccelerometerListener(this, mThreadPoolExecutor, finalData, sensorRecord);
        //initialize gyroscope listener
        gyroListener = new GyroscopeListener(this, mThreadPoolExecutor, finalData, sensorRecord);
        //initialize step detector listener
        stepListener = new StepDetectorListener(this, mThreadPoolExecutor, finalData, sensorRecord);
    }

    /***
     * Create or override to empty a file to be able to record sensors data
     * @param file
     */
    public void clearFile(File file) {
        try {
            FileOutputStream outputstream = new FileOutputStream(file, false);
            OutputStreamWriter oswriter = new OutputStreamWriter(outputstream);
            BufferedWriter bwriter = new BufferedWriter(oswriter);
            bwriter.append("Ax,Ay,Az,Gx,Gy,Gz,STime,Time,Rstep,Dstep,Event");
            bwriter.newLine();
            bwriter.close();
            outputstream.close();
            Log.d(TAG, "clearFile: " + file.toString());
        } catch (FileNotFoundException e) {
            //catch errors opening file
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /***
     * Check and ask permission to write on external storage
     */
    public void checkWritePermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
            } else {
                // request the permission
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, MY_PERMISSIONS_WRITE_EXTERNAL_STORAGE);
                // MY_PERMISSIONS_WRITE_EXTERNAL_STORAGE is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        } else {
            // Permission has already been granted
        }
    }

    /***
     * Register the 3 listeners allowing them to start writing on the file
     */
    public void playButton() {
        accListener.register();
        gyroListener.register();
        stepListener.register();
        Toast.makeText(this, "Start recording data", Toast.LENGTH_SHORT).show();

    }

    /***
     * Unregister the 3 listeners stopping them to write on the file
     */
    public void stopButton() {
        accListener.unregister();
        gyroListener.unregister();
        stepListener.unregister();
        sendEmail();
        //closeApp();
    }

    /***
     * Send csv file to my email
     */
    public void sendEmail() {
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder(); // A way to overcome permissions
        StrictMode.setVmPolicy(builder.build());
        Uri finalPath = Uri.fromFile(finalData);
        // Array list with uris path
        ArrayList<Uri> uris = new ArrayList<Uri>();
        uris.add(finalPath);
        Intent emailIntent = new Intent(Intent.ACTION_SEND);
        // set the type to 'email'
        emailIntent.setType("vnd.android.cursor.dir/email");
        String to[] = {"andrea.giordano991@gmail.com"};
        emailIntent.putExtra(Intent.EXTRA_EMAIL, to);
        // the attachments
        emailIntent.putExtra(Intent.EXTRA_STREAM, finalPath);
        // the mail subject
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "step_counter_df data");
        startActivity(Intent.createChooser(emailIntent, "Send email"));
    }

    /**
     * Close completely the app and goes to home
     */
    public void closeApp() {
        int pid = android.os.Process.myPid();
        android.os.Process.killProcess(pid);
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        startActivity(intent);
    }

    /***
     * Get precise instance of time
     */
    public static String getCurrentTimeWithMillisec() {
        SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        Date now = new Date();
        String strDate = sdfDate.format(now);
        return strDate;
    }

    /***
     * Record headphone button clicks
     */
    @Override
    public boolean onKeyDown (int keyCode,
                              KeyEvent event) {
        // This is the center button for headphones
        if (event.getKeyCode() == KeyEvent.KEYCODE_HEADSETHOOK) {
            Toast.makeText(this, "BUTTON PRESSED!!", Toast.LENGTH_SHORT).show();
            String entry  = sensorRecord.getaX() + "," +
                    sensorRecord.getaY() + "," +
                    sensorRecord.getaZ() + "," +
                    sensorRecord.getgX() + "," +
                    sensorRecord.getgY() + "," +
                    sensorRecord.getgZ() + "," +
                    String.valueOf(System.currentTimeMillis()) + "," +
                    getCurrentTimeWithMillisec() + "," +
                    "1," +
                    "0," +
                    "manualStepDetector";
            WriteFileRunnable writeHeadButton = new WriteFileRunnable(entry, finalData);
            mThreadPoolExecutor.execute(writeHeadButton);
            Log.d(TAG, "onKeyDown: headphones button pressed!");
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}

