package com.example.panicbutton;


import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Handler;
import android.widget.Toast;

public class Shake implements SensorEventListener {

    private Context context;
    private SensorManager sensorManager;
    private Sensor accelerometer;
    private boolean sosFeatureActive = false;
    private boolean shakeDetected = false;
    private int shakeCount = 0;
    private long lastShakeTime = 0;
    private Handler handler;
    private Runnable runnable;

    public Shake(Context context) {
        this.context = context;
        this.sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        this.accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        this.handler = new Handler();
        this.runnable = new Runnable() {
            @Override
            public void run() {
                unregisterListener();
                showToast("Shake feature deactivated.");
            }
        };
    }

    public void toggleSOSFeature() {
        sosFeatureActive = !sosFeatureActive;

        if (sosFeatureActive) {
            showToast("Shake feature activated. Shake your phone to trigger SOS.");
        } else {
            handler.removeCallbacks(runnable);
        }
    }

    private void showToast(String message) {
        Toast.makeText(context.getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }

    public boolean isSOSFeatureActive() {
        return sosFeatureActive;
    }

    public void registerListener() {
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
    }

    public void unregisterListener() {
        handler.removeCallbacks(runnable);
        sensorManager.unregisterListener(this);
        showToast("Shake feature deactivated.");
    }

    // Implement the onSensorChanged and onAccuracyChanged methods
    @Override
    public void onSensorChanged(SensorEvent event) {
        // Shake detection logic
        float x = event.values[0];
        float y = event.values[1];
        float z = event.values[2];

        float acceleration = (float) Math.sqrt(x * x + y * y + z * z);
        float shakeThreshold = 50.0f;
        long currentTime = System.currentTimeMillis();
        long timeDifference = currentTime - lastShakeTime;

        if (acceleration > shakeThreshold) {
            if (!shakeDetected && timeDifference > 30) {
                shakeDetected = true;
                shakeCount++;
                lastShakeTime = currentTime;
            }
        } else {
            shakeDetected = false;
        }

        // Check if we have three distinct up-and-down movements
        if (shakeCount == 3) {
            showToast("Shake detected. SOS message will be sent.");
            handler.removeCallbacks(runnable);
            handler.postDelayed(runnable, 3000); // Delay for 3 seconds before deactivating shake feature
            shakeCount = 0;
        }


        // For example, if shake is detected, trigger SOS and show a toast message
        handler.removeCallbacks(runnable);
        handler.postDelayed(runnable, 3000); // Delay for 3 seconds before deactivating shake feature
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Not used in this example
    }
    //TODO: rerwite the code with the implementation of sending a message here its an active toast.
}


