package com.example.panicbutton;

import android.app.Service;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.os.IBinder;

import androidx.annotation.Nullable;

public class MyBackgroundServices extends Service implements SensorEventListener {
    private Shake shake;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // Initialize the Shake class with the service's context
        shake = new Shake(this);

        // Start shake detection
        shake.registerListener();

        // Return START_STICKY to indicate that the service should be restarted if it is killed by the system
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        // Stop shake detection
        shake.unregisterListener();
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        // Your shake detection logic here
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Not used in this example
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}