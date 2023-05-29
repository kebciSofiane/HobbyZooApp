package com.example.hobbyzooapp.Sessions;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hobbyzooapp.Activities.ActivityPage;
import com.example.hobbyzooapp.Calendar.CalendarUtils;
import com.example.hobbyzooapp.HomeActivity;
import com.example.hobbyzooapp.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

public class ListSessionsAdapter extends RecyclerView.Adapter<ListSessionsAdapter.ViewHolder> {
    private List<Session> items;
    private int displayedItemCount = 3;
    private boolean isExpanded = false;
    ViewGroup v;


    public void setDisplayedItemCount(int displayedItemCount) {
        this.displayedItemCount = displayedItemCount;
    }

    public ListSessionsAdapter(List<Session> items) {
        this.items = items;
    }

    public void setItems(List<Session> items) {
        this.items = items;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        v= parent;
        // Créez et retournez une instance de ViewHolder qui contient la vue de chaque élément
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_session_list_activity, parent, false);
        return new ViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        if (items.isEmpty()) {
            holder.textView.setText("No session planned for this activity"); // Message affiché lorsque la liste est vide
        } else {
            Session item = items.get(position);
            holder.textView.setText(item.getDay()+"-"+item.getMonth()+"-"+item.getYear()+" for "+item.getTime());

            holder.textView.setOnClickListener(new View.OnClickListener() {

                @RequiresApi(api = Build.VERSION_CODES.O)
                @Override
                public void onClick(View v) {
                    CalendarUtils.selectedDate = LocalDate.of(item.getYear(),item.getMonth(),item.getDay());
                    MyDailySessions.localDate=LocalDate.of(item.getYear(),item.getMonth(),item.getDay());
                    Intent intent=new Intent(v.getContext(), MyDailySessions.class);
                    System.out.println("hahahaah");

                }
            });





        }

    }

    public boolean isExpanded() {
        return isExpanded;
    }

    public void setExpanded(boolean expanded) {
        isExpanded = expanded;
    }


    @Override
    public int getItemCount() {
        if (items.isEmpty()) {
            return 1; // Retourne 1 pour afficher le message si la liste est vide
        } else if (isExpanded) {
            return items.size();
        } else {
            return Math.min(items.size(), displayedItemCount);
        }
    }



    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView textView;

        public ViewHolder(View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.itemTitleTextView);


        }
    }
}
