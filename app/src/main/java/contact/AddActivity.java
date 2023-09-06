package contact;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.panicbutton.R;

public class AddActivity extends AppCompatActivity {
    public EditText txtName;
    public EditText txtphone;
    Button btnAdd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);

        txtName = findViewById(R.id.editTextText);
        txtphone = findViewById(R.id.editTextPhone);
        btnAdd = findViewById(R.id.updateBtn);
        DBhelper myDb = new DBhelper(AddActivity.this);
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String nameText = txtName.getText().toString();
                String contactText = txtphone.getText().toString();
                Boolean saveData = myDb.saveuserdata(nameText,contactText);
                if(TextUtils.isEmpty(nameText)||TextUtils.isEmpty(contactText)){
                    Toast.makeText(AddActivity.this, "Add Name & Contact", Toast.LENGTH_SHORT).show();
                }
                else{
                    if(saveData == true){
                        Toast.makeText(AddActivity.this, "Save Contact", Toast.LENGTH_SHORT).show();

                    }
                    Toast.makeText(AddActivity.this, "Exists Contact", Toast.LENGTH_SHORT).show();
                }


            }
        });
    }
}