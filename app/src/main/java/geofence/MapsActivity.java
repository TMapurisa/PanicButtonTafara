package geofence;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.app.PendingIntent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.panicbutton.R;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMapLongClickListener, GoogleMap.OnMarkerClickListener {

    private static final String TAG = "MapsActivity";

    private GoogleMap mMap;
    private GeofencingClient geofencingClient;
    private GeofenceHelper geofenceHelper;

    private float GEOFENCE_RADIUS = 50;
    private String GEOFENCE_ID = "SOME_GEOFENCE_ID";
    private SharedPreferences geofencePreferences;

    private int FINE_LOCATION_ACCESS_REQUEST_CODE = 10001;
    private int BACKGROUND_LOCATION_ACCESS_REQUEST_CODE = 10002;
    private Set<LatLng> predefinedMarkerPositions = new HashSet<>(); // Store predefined marker positions


    private boolean predefinedGeofenceActive = false;
    private boolean addedGeofenceActive = false;

    private Button btnActivatePredefined;
    private Button btnAddGeofence;




    private NotificationHelper notificationHelper; // Add this line





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        notificationHelper = new NotificationHelper(this);

        // Initialize buttons
        btnActivatePredefined = findViewById(R.id.btnActivatePredefined);
        btnAddGeofence = findViewById(R.id.btnAddGeofence);

        // Set button click listeners
        // Set button click listeners
        btnActivatePredefined.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                togglePredefinedGeofence();
            }
        });

        btnAddGeofence.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggleAddedGeofence();
            }
        });
    }

    // Method to toggle the predefined geofence activation
    private void togglePredefinedGeofence() {
        if (predefinedGeofenceActive) {
            // Deactivate predefined geofence
            // Implement the logic to deactivate the geofence here
            // ...

            predefinedGeofenceActive = false;
            btnActivatePredefined.setText("Activate Predefined Geofence");
        } else {
            // Activate predefined geofence
            // Implement the logic to activate the geofence here
            // ...

            predefinedGeofenceActive = true;
            btnActivatePredefined.setText("Deactivate Predefined Geofence");
        }
    }

    // Method to toggle the added geofence activation
    private void toggleAddedGeofence() {
        if (addedGeofenceActive) {
            // Deactivate added geofence
            // Implement the logic to deactivate the geofence here
            // ...

            addedGeofenceActive = false;
            btnAddGeofence.setText("Activate Added Geofence");
        } else {
            // Activate added geofence
            // Implement the logic to activate the geofence here
            // ...

            addedGeofenceActive = true;
            btnAddGeofence.setText("Deactivate Added Geofence");
        }




















        // Initialize buttons
        btnActivatePredefined = findViewById(R.id.btnActivatePredefined);
        btnAddGeofence = findViewById(R.id.btnAddGeofence);

        geofencePreferences = getSharedPreferences("Geofences", MODE_PRIVATE);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        geofencingClient = LocationServices.getGeofencingClient(this);
        geofenceHelper = new GeofenceHelper(this);
    }












    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        mMap.setOnMarkerClickListener(this); // Set up marker click listener

        enableUserLocation();

        mMap.setOnMapLongClickListener(this);

        mMap.clear(); // Clear the map
        addPredefinedGeofences(); // Add predefined geofences
        loadSavedGeofences(); // Load saved geofences
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        Log.d(TAG, "Clicked marker at position: " + marker.getPosition());

        if (!isPredefinedMarker(marker)) {
            Log.d(TAG, "Removing geofence for marker at position: " + marker.getPosition());
            removeGeofence(marker.getPosition());
            return true; // Consume the event for custom markers
        } else {
            return false; // Do not consume the event for predefined markers
        }
    }


    private void removeGeofence(LatLng latLng) {
        String keyToRemove = null;
        Map<String, ?> allEntries = geofencePreferences.getAll();
        for (Map.Entry<String, ?> entry : allEntries.entrySet()) {
            String[] values = entry.getValue().toString().split(",");
            double latitude = Double.parseDouble(values[0]);
            double longitude = Double.parseDouble(values[1]);

            if (latitude == latLng.latitude && longitude == latLng.longitude) {
                keyToRemove = entry.getKey();
                break;
            }
        }

        if (keyToRemove != null) {
            // Remove the geofence from SharedPreferences
            SharedPreferences.Editor editor = geofencePreferences.edit();
            editor.remove(keyToRemove);
            editor.apply();

            // Remove the geofence from the GeofencingClient
            geofencingClient.removeGeofences(Collections.singletonList(keyToRemove));

            // Clear the map and reload saved geofences (excluding predefined)
            mMap.clear(); // Clear the map
            loadSavedGeofences(); // Load saved geofences (excluding predefined)
        }
    }

    private void loadSavedGeofences() {
        Map<String, ?> allEntries = geofencePreferences.getAll();
        int count = 0;
        for (Map.Entry<String, ?> entry : allEntries.entrySet()) {
            String[] values = entry.getValue().toString().split(",");
            double latitude = Double.parseDouble(values[0]);
            double longitude = Double.parseDouble(values[1]);
            float radius = Float.parseFloat(values[2]);

            LatLng latLng = new LatLng(latitude, longitude);

            // Skip predefined geofences
            if (!isPredefinedGeofence(latitude, longitude)) {
                addMarker(latLng);
                addCircle(latLng, radius);
                addGeofence(latLng, radius);
                count++;
            }
        }
    }





    private boolean isPredefinedMarker(Marker marker) {
        LatLng predefined1 = new LatLng(-15.39484, 28.30669);
        LatLng predefined2 = new LatLng(-15.38631, 28.35340);

        LatLng markerPosition = marker.getPosition();
        return isLatLngEqual(markerPosition, predefined1) || isLatLngEqual(markerPosition, predefined2);
    }

    private boolean isLatLngEqual(LatLng latLng1, LatLng latLng2) {
        double tolerance = 0.00001; // Set a small tolerance for comparison
        return Math.abs(latLng1.latitude - latLng2.latitude) < tolerance &&
                Math.abs(latLng1.longitude - latLng2.longitude) < tolerance;
    }

    private boolean isPredefinedGeofence(double latitude, double longitude) {
        LatLng predefined1 = new LatLng(-15.39484, 28.30669);
        LatLng predefined2 = new LatLng(-15.38631, 28.35340);

        LatLng targetLatLng = new LatLng(latitude, longitude);
        return isLatLngEqual(targetLatLng, predefined1) || isLatLngEqual(targetLatLng, predefined2);
    }



    private void addPredefinedMarker(LatLng latLng) {
        MarkerOptions markerOptions = new MarkerOptions().position(latLng);
        mMap.addMarker(markerOptions);
    }




    private void addPredefinedGeofences() {
        LatLng predefined1 = new LatLng(-15.39484, 28.30669);
        LatLng predefined2 = new LatLng(-15.38631, 28.35340);

        // Add marker and circle for predefined geofence 1
        addPredefinedMarker(predefined1);
        addCircle(predefined1, GEOFENCE_RADIUS);

        // Add marker and circle for predefined geofence 2
        addPredefinedMarker(predefined2);
        addCircle(predefined2, GEOFENCE_RADIUS);
    }

















    private void enableUserLocation() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
        } else {
            //Ask for permission
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                //We need to show user a dialog for displaying why the permission is needed and then ask for the permission...
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, FINE_LOCATION_ACCESS_REQUEST_CODE);
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, FINE_LOCATION_ACCESS_REQUEST_CODE);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == FINE_LOCATION_ACCESS_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //We have the permission
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }
                mMap.setMyLocationEnabled(true);
            } else {
                //We do not have the permission..

            }
        }

        if (requestCode == BACKGROUND_LOCATION_ACCESS_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //We have the permission
                Toast.makeText(this, "You can add geofences...", Toast.LENGTH_SHORT).show();
            } else {
                //We do not have the permission..
                Toast.makeText(this, "Background location access is neccessary for geofences to trigger...", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onMapLongClick(LatLng latLng) {
        if (Build.VERSION.SDK_INT >= 29) {
            //We need background permission
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_BACKGROUND_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                handleMapLongClick(latLng);
            } else {
                if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_BACKGROUND_LOCATION)) {
                    //We show a dialog and ask for permission
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_BACKGROUND_LOCATION}, BACKGROUND_LOCATION_ACCESS_REQUEST_CODE);
                } else {
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_BACKGROUND_LOCATION}, BACKGROUND_LOCATION_ACCESS_REQUEST_CODE);
                }
            }

        } else {
            handleMapLongClick(latLng);
        }

    }














    private void handleMapLongClick(LatLng latLng) {
        if (geofencePreferences.getAll().size() >= 2) {
            Toast.makeText(this, "Maximum of two geofences reached", Toast.LENGTH_SHORT).show();
            return;
        }

        // No need to clear the map here
        addMarker(latLng);
        addCircle(latLng, GEOFENCE_RADIUS);
        addGeofence(latLng, GEOFENCE_RADIUS);

        // Save geofence details to SharedPreferences
        saveGeofence(latLng.latitude, latLng.longitude, GEOFENCE_RADIUS);
    }



    private void saveGeofence(double latitude, double longitude, float radius) {
        SharedPreferences.Editor editor = geofencePreferences.edit();
        String key = String.valueOf(System.currentTimeMillis()); // Generate a unique key
        String value = latitude + "," + longitude + "," + radius;
        editor.putString(key, value);
        editor.apply();
    }














    private void addGeofence(LatLng latLng, float radius) {

        Geofence geofence = geofenceHelper.getGeofence(GEOFENCE_ID, latLng, radius, Geofence.GEOFENCE_TRANSITION_ENTER | Geofence.GEOFENCE_TRANSITION_DWELL | Geofence.GEOFENCE_TRANSITION_EXIT);
        GeofencingRequest geofencingRequest = geofenceHelper.getGeofencingRequest(geofence);
        PendingIntent pendingIntent = geofenceHelper.getPendingIntent();

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        geofencingClient.addGeofences(geofencingRequest, pendingIntent)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "onSuccess: Geofence Added...");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        String errorMessage = geofenceHelper.getErrorString(e);
                        Log.d(TAG, "onFailure: " + errorMessage);
                    }
                });
    }

    private void addMarker(LatLng latLng) {
        MarkerOptions markerOptions = new MarkerOptions().position(latLng);
        mMap.addMarker(markerOptions);
    }

    private void addCircle(LatLng latLng, float radius) {
        CircleOptions circleOptions = new CircleOptions();
        circleOptions.center(latLng);
        circleOptions.radius(radius);
        circleOptions.strokeColor(Color.argb(255, 255, 0,0));
        circleOptions.fillColor(Color.argb(64, 255, 0,0));
        circleOptions.strokeWidth(4);
        mMap.addCircle(circleOptions);
    }
}
