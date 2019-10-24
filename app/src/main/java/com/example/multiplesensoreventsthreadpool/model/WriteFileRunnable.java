package com.example.multiplesensoreventsthreadpool.model;

import android.util.Log;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import static android.content.ContentValues.TAG;

/***
 * Class for a runnable object setting how the data are recorded when run on the thread pool
 */
public class WriteFileRunnable implements Runnable {
    private String entry;
    private File file;

    public WriteFileRunnable(String entry, File file) {
        this.entry = entry;
        this.file = file;
    }

    @Override
    public void run() {
        writeFileData();
    }

    /***
     * Append recorded data to the CSV file and provide console feedback on threads
     */
    public void writeFileData(){

        try {
            FileOutputStream outputstream = new FileOutputStream(file, true);
            OutputStreamWriter oswriter = new OutputStreamWriter(outputstream);
            BufferedWriter bwriter = new BufferedWriter(oswriter);
            bwriter.append(entry);
            bwriter.newLine();
            bwriter.close();
            outputstream.close();
            Log.d(TAG, "Number of Threads: " + Thread.getAllStackTraces().keySet().size());
            int runningThreads = 0;
            for (Thread t : Thread.getAllStackTraces().keySet()) {
                if (t.getState()==Thread.State.RUNNABLE) runningThreads++;
            }
            Log.d(TAG, "Working Threads: " + runningThreads);
            //Toast.makeText(getApplicationContext(),"Recording Accelerometer on CSV file...", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
