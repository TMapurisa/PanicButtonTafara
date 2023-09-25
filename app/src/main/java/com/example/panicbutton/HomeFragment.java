package com.example.panicbutton;

import SafetyTips.TipsNotifications;
import contact.*;
import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.hardware.Sensor;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.telephony.SmsManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.Timer;

public class HomeFragment extends Fragment {


    //new{
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private static final int SOS_INTERVAL = 15000; // 15 seconds
    private FusedLocationProviderClient fusedLocationClient;
    private Timer sosTimer;
    private boolean isPanicActive = false;
    private Handler handler = new Handler(Looper.getMainLooper());
    public DBhelper dbh ;

    //new
    private TipsNotifications tipsNotifications = new TipsNotifications();




    // }
    private Button panicbtn;
    private Button Deact;
    private TextView panictext;
    private int btn_press = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_home, container, false);

        tipsNotifications.startUse(getContext());
        // Start the service
        panicbtn = rootView.findViewById(R.id.show_text_button);
        panictext = rootView.findViewById(R.id.hidden_text);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(getActivity());
        Deact = rootView.findViewById(R.id.BtnNotification);
        Deact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tipsNotifications.stop();
                Deact.setVisibility(View.INVISIBLE);

            }
        });



        panicbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (btn_press % 2 == 0) {
                    panicbtn.setText("Stop");
                    if (!isPanicActive) {
                        showConfirmationDialog();
                        panictext.setText("SOS MESSAGE SENT");
                        panictext.setVisibility(View.VISIBLE);
                    } else {
                        stopPanic();
                    }

                    btn_press++;

                } else {
                    panictext.setVisibility(View.INVISIBLE);

                    panicbtn.setText("PANIC");
                    btn_press++;
                    stopPanic();
                }

            }


        });

        return rootView;
    }
    private void showConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Emergency Alert")
                .setMessage("Are you sure you want to send an emergency alert to your contacts?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        requestPermissionsAndStartPanic();
                    }
                })
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }
    public void requestPermissionsAndStartPanic() {
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.SEND_SMS}, LOCATION_PERMISSION_REQUEST_CODE);
        } else {
            startPanic();
        }
    }
    private void startPanic() {
        panictext.setText("SOS has been sent!");
        isPanicActive = true;
        showToast("SMS will be sent");
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (isPanicActive) {
                    sendSOSMessage();
                    handler.postDelayed(this, SOS_INTERVAL);
                }
            }
        }, 0); // Start immediately
    }

    private void sendSOSMessage() {
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        Task<Location> locationTask = fusedLocationClient.getLastLocation();
        locationTask.addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null) {
                    double latitude = location.getLatitude();
                    double longitude = location.getLongitude();
                    String liveLocationUrl = "https://www.google.com/maps?q=" + latitude + "," + longitude;
                    String distressMessage = "SOS: I need help! My live location: " + liveLocationUrl;
                    sendSmsToEmergencyContact(distressMessage);
                    showToast("SOS message sent to emergency contact");
                } else {
                    showToast("Location not available");
                }
            }
        });
    }

    private void sendSmsToEmergencyContact(String message) {
        DBhelper dbh = new DBhelper(getContext());
        Cursor cursor = dbh.getdata();

        // Check if there are any contacts
        if (cursor != null && cursor.moveToFirst()) {
            do {
                String phoneNumber = cursor.getString(cursor.getColumnIndex("contact"));

                SmsManager smsManager = SmsManager.getDefault();
                smsManager.sendTextMessage(phoneNumber, null, message, null, null);

            } while (cursor.moveToNext());

            // Close the cursor when done
            cursor.close();
        }

        // Close the database connection
        dbh.close();
    }





//    private void sendSmsToEmergencyContact(String message) {
//
//
//        dbh = new DBhelper(getContext());
//        Cursor cursor = dbh.getdata();
//
//        // Check if there are any contacts
//        if (cursor != null && cursor.moveToFirst()) {
//            do {
//               // String contactName = cursor.getString(cursor.getColumnIndex("name"));
//                String phoneNumber = cursor.getString(cursor.getColumnIndex("phone_number"));
//
//                SmsManager smsManager = SmsManager.getDefault();
//                smsManager.sendTextMessage(phoneNumber, null, message, null, null);
//
//
//            } while (cursor.moveToNext());
//
//            // Close the cursor when done
//            cursor.close();
//        }
//        // Close the database connection
//        dbh.close();
//
//        // end of new code
////            SmsManager smsManager = SmsManager.getDefault();
////            smsManager.sendTextMessage(emergencyContactNumber, null, message, null, null);
//    }






    private void stopPanic() {
        isPanicActive = false;
        handler.removeCallbacksAndMessages(null);
        showToast("Panic stopped");
    }
    private void showToast(String message) {
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
    }





}