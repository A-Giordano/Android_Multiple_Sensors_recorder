The aim of this project is to record sensor data during running activities to then use them as a training dataset to build a Deep Learning model able to detect steps.

It has been choosen to record ccelerometer and gyroscope data on a CSV file that will be sent by email pressing the stop recording button on the app.
Android build-in StepDetector sensor is also recorded to have a performance benchmark.

Moreover, to have a precise label of when a step is performed, the actual steps are recorded by the user pressing the headphone button on each of them.
To avoid delays in the recording process has been used a different SensorManager and SensorEventListener for each sensor.

While to prevent delays in the writing on the CSV process a ThreadPoolExecutor is used assigning the workload of appending a new line to the file to different Threads.
The nature of the work that needs to be offloaded (high number of small distinct task) seems more appropriate to the ThreadPool than AsyncTask or HandlerThread.

With this configuration it is possible to record as many sensors is liked even with the lowest delay possible (SENSOR_DELAY_FASTEST) without causing the app to not be able to respond or crash.

Finally it is advisable to do not lock the phone in the recording phase, since on Android official documentation it is not recommended when listening sensors, also headphones button clicks won’t be detected anymore.

