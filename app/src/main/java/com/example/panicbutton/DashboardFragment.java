package com.example.panicbutton;


import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;

import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;


import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;


import geofence.MapsActivity;

public class DashboardFragment extends Fragment implements SensorEventListener {

    private Button btnwlkthme;
    private Shake shake;
    private Button btnShake;
    private Button btnGeo;
    private Button btnOther;
    private TextView txtAct1;
    private TextView txtAct2;
    private TextView txtAct3;
    private boolean sosFeatureActive = false;
    private int pressButtonCounter = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_dashboard, container, false);


        btnwlkthme = rootView.findViewById(R.id.btnWalkwithme);
        btnGeo = rootView.findViewById(R.id.btnGeo);
        btnShake = rootView.findViewById(R.id.btnShake);
        btnOther = rootView.findViewById(R.id.btnOther);
        txtAct3 = rootView.findViewById(R.id.txtActive3);
        txtAct2 = rootView.findViewById(R.id.txtActive2);
        txtAct1 = rootView.findViewById(R.id.txtActive);

        // Initialize the Shake class with the fragment's context
        shake = new Shake((FragmentActivity) requireContext());

        btnwlkthme.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                txtAct3.setVisibility(View.VISIBLE);
                openMapsActivity();
            }

            public void openMapsActivity() {
               //do something
            }
        });

        btnGeo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(getActivity(), MapsActivity.class);
                    startActivity(intent);
                }


        });
        btnOther.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                openNewActivity();
            }

            private void openNewActivity() {
                Intent intent = new Intent(getActivity(), OtherServices.class);
                startActivity(intent);
            }

        });

        btnShake.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                // Toggle the SOS feature and start/stop shake detection
                shake.toggleSOSFeature();

                if (shake.isSOSFeatureActive()) {
                    shake.registerListener();
                    txtAct2.setVisibility(View.VISIBLE);

                } else {
                    shake.unregisterListener();
                    txtAct2.setVisibility(View.INVISIBLE);
                }
            }
        });

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        // Register the Shake class as a SensorEventListener when the fragment is resumed

        shake.registerListener();
    }

    @Override
    public void onPause() {
        super.onPause();
        // Unregister the Shake class as a SensorEventListener when the fragment is paused
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


}
