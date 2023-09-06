package contact;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.panicbutton.R;

import java.util.ArrayList;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {

    Context context;
    Activity activity;
    ArrayList uid,uname, uphone;
    LayoutInflater layoutInflater;

    public MyAdapter(Activity activity, Context context, ArrayList uid, ArrayList uname, ArrayList uphone){
        this.activity = activity;
        this.context = context;
        this.uid = uid;
        this.uname =uname;
        this.uphone = uphone;
        layoutInflater = LayoutInflater.from(context);

    }



    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = layoutInflater.inflate(R.layout.list_item,parent,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        holder.id.setText(String.valueOf(uid.get(position)));
        holder.names.setText(String.valueOf(uname.get(position)));
        holder.numbers.setText(String.valueOf(uphone.get(position)));
        holder.mainLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context,UpdateActivity.class);
                intent.putExtra("uid",String.valueOf(uid.get(position)));
                intent.putExtra("name",String.valueOf(uname.get(position)));
                intent.putExtra("number",String.valueOf(uphone.get(position)));
                activity.startActivityForResult(intent,1);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return uname.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{
        TextView id,names,numbers;
        LinearLayout mainLayout;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            id = itemView.findViewById(R.id.TxtId);
            names = itemView.findViewById(R.id.txtname);
            numbers = itemView.findViewById(R.id.txtnumber);
            mainLayout = itemView.findViewById(R.id.mainLayout);
        }
    }
}
