package contact;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.panicbutton.R;

public class UpdateActivity extends AppCompatActivity {

    EditText newName,newPhone;
    Button update,delete;
    String id,name,phone;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update);

        newName = findViewById(R.id.updateTextText);
        newPhone = findViewById(R.id.updateTextPhone);
        DBhelper dBhelper = new DBhelper(UpdateActivity.this);
        update = findViewById(R.id.updateBtn);
        delete = findViewById(R.id.deleteBtn);
        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                name =  newName.getText().toString();
                phone =  newPhone.getText().toString();
                Boolean savedata = dBhelper.updatedata(id,name,phone);
                if(savedata == true){
                    Toast.makeText(UpdateActivity.this, "Save Contact", Toast.LENGTH_SHORT).show();

                }else

                    Toast.makeText(UpdateActivity.this, "Exists Contact", Toast.LENGTH_SHORT).show();
            }
        });
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DBhelper dbh = new DBhelper(UpdateActivity.this);
                Boolean isDeleted = dbh.deleteOnerow(id);
                if (isDeleted == true){
                    Toast.makeText(UpdateActivity.this, "SuccessFully Deleted", Toast.LENGTH_SHORT).show();
                    finish();
                }
                else{
                    Toast.makeText(UpdateActivity.this, "Operation Failed", Toast.LENGTH_SHORT).show();
                }

            }
        });
        getIntentData();


    }
    public void getIntentData(){
        if(getIntent().hasExtra("uid")&&getIntent().hasExtra("name")&& getIntent().hasExtra("number")){
            //getting data from intent
            id = getIntent().getStringExtra("uid");
            name = getIntent().getStringExtra("name");
            phone= getIntent().getStringExtra("number");

            //Setting intent data
            newName.setText(name);
            newPhone.setText(phone);
        }
        else{
            Toast.makeText(this, "No Data", Toast.LENGTH_SHORT).show();
        }

    }

}
//package com.example.contacts;
//
//import androidx.appcompat.app.AppCompatActivity;
//
//import android.os.Bundle;
//import android.text.TextUtils;
//import android.view.View;
//import android.widget.Button;
//import android.widget.EditText;
//import android.widget.Toast;
//
//public class UpdateActivity extends AppCompatActivity {
//    EditText newName,newPhone;
//    Button update;
//    String name,phone;
//
//
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_update);
//
//    }
//}
