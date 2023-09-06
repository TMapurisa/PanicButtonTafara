package contact;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.example.panicbutton.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class contact extends Fragment {
    RecyclerView recyclerView;
    FloatingActionButton fltnbtn;
    DBhelper dBhelper;
    ArrayList<String> contact_id,contact_name,contact_num;
    MyAdapter myAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.contact, container, false);

        recyclerView = rootView.findViewById(R.id.recyclerView);
        dBhelper = new DBhelper(getActivity());
        fltnbtn = rootView.findViewById(R.id.flotingbtn);

        fltnbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(),AddActivity.class);
                startActivity(intent);

            }
        });
        contact_id = new ArrayList<>();
        contact_name = new ArrayList<>();
        contact_num = new ArrayList<>();

        myAdapter = new MyAdapter(getActivity(),getContext(), contact_id, contact_name, contact_num);
        recyclerView.setAdapter(myAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        displayData();

        return rootView;
    }

    private void displayData() {
        Cursor cursor = dBhelper.getdata();
        if(cursor.getCount() == -1){
            Toast.makeText(getActivity(), "No Data To display!", Toast.LENGTH_SHORT).show();
            return;
        }
        else{
            while(cursor.moveToNext()){
                contact_id.add(cursor.getString(0));
                contact_name.add(cursor.getString(1));
                contact_num.add(cursor.getString(2));
            }
        }
        myAdapter.notifyDataSetChanged();
    }
}
