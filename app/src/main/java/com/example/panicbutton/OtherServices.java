package com.example.panicbutton;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.telephony.SmsManager;
import android.text.InputType;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

public class OtherServices extends AppCompatActivity {
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private static final int SOS_INTERVAL = 15000; // 15 seconds

    private FusedLocationProviderClient fusedLocationClient;
    Button btnPolice;
    Button btnHospital;
    Button btnFire;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_other_services);
        btnPolice = findViewById(R.id.btnPolice);
        btnFire = findViewById(R.id.btnfire);
        btnHospital = findViewById(R.id.btnMedical);
        btnPolice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showContactInputDialog("police");
            }
        });

        btnFire.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showContactInputDialog("fire");
            }
        });

        btnHospital.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showContactInputDialog("hospital");
            }
        });
    }

    public void makePhoneCall(String phoneNumber) {
        Intent intent = new Intent(Intent.ACTION_CALL);
        intent.setData(Uri.parse("tel:" + phoneNumber));

        try {
            startActivity(intent);
        } catch (SecurityException e) {
            e.printStackTrace();
            // Handle the exception, e.g., request the CALL_PHONE permission.
        }
    }

    private void showConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Emergency Alert")
                .setMessage("Are you sure you want to call the emergency contact?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
//                        requestPermissionsAndStartPanic();
                    }
                })
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }

    private void requestPermissionsAndStartPanic(String contactNumber) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CALL_PHONE}, LOCATION_PERMISSION_REQUEST_CODE);
        } else {
            startPanic(contactNumber);
        }
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    private void showContactInputDialog(final String contactType) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_PHONE);
        builder.setTitle("Enter " + contactType + " Contact Number")
                .setView(input)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String contactNumber = input.getText().toString();
                        if (!TextUtils.isEmpty(contactNumber)) {
                            saveContactNumber(contactType, contactNumber);
                            requestPermissionsAndStartPanic(contactNumber);
                        } else {
                            showToast("Please enter a valid contact number.");
                        }
                    }
                })
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }
    private void saveContactNumber(String contactType, String contactNumber) {
        // Save the contact number based on the contactType (e.g., "police", "fire", "hospital")
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("emergency_contact_" + contactType, contactNumber);
        editor.apply();
    }

    private void startPanic(String contactNumber) {
        showToast("Calling the emergency contact...");
        makePhoneCall(contactNumber);
    }
}
