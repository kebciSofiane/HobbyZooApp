package com.example.hobbyzooapp.Calendar;
import com.example.hobbyzooapp.R;
import com.example.hobbyzooapp.Sessions.MyDailySessions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;

import android.graphics.Color;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicMarkableReference;

class CalendarAdapter extends RecyclerView.Adapter<CalendarViewHolder>
{
    private final ArrayList<LocalDate> days;
    private final OnItemListener onItemListener;

    DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference("Session");

    public CalendarAdapter(ArrayList<LocalDate> days, OnItemListener onItemListener) {
        this.days = days;
        this.onItemListener = onItemListener;
    }

    @NonNull
    @Override
    public CalendarViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.calendar_cell, parent, false);
        ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
        if(days.size() > 15) //month view
            layoutParams.height = (int) (parent.getHeight() * 0.166666666);
        else // week view
            layoutParams.height = (int) parent.getHeight();

        return new CalendarViewHolder(view, onItemListener, days);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onBindViewHolder(@NonNull CalendarViewHolder holder, int position) {
        final LocalDate date = days.get(position);
        if(date == null)
            holder.dayOfMonth.setText("");
        else
        {
            holder.dayOfMonth.setText(String.valueOf(date.getDayOfMonth()));
            if(date.equals(CalendarUtils.selectedDate))
                holder.parentView.setBackgroundColor(Color.LTGRAY);
            FirebaseDatabase database = FirebaseDatabase.getInstance();


///////////////////////////////////////////////
           searchSession(date.getDayOfMonth(), date.getMonthValue(), date.getYear(), new SessionExistsCallback() {
                @Override
                public void onSessionExists(boolean sessionExists) {
                    // Utilisez la valeur de sessionExists ici
                    if (sessionExists) {
                        holder.parentView.setBackgroundColor(Color.RED);
                    } else {

                    }

                }

           });
        }
    }


    public void searchSession(int jour, int mois, int annee, SessionExistsCallback callback) {
        Query dayQuery = databaseRef.orderByChild("session_day").equalTo(jour);
        Query monthQuery = databaseRef.orderByChild("session_month").equalTo(mois);
        Query yearQuery = databaseRef.orderByChild("session_year").equalTo(annee);

        dayQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    monthQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()) {
                                yearQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        boolean sessionExists = dataSnapshot.exists();
                                        callback.onSessionExists(sessionExists);
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {
                                        callback.onSessionExists(false);
                                    }
                                });
                            } else {
                                callback.onSessionExists(false);
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            callback.onSessionExists(false);
                        }
                    });
                } else {
                    callback.onSessionExists(false);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                callback.onSessionExists(false);
            }
        });
    }



    /////////////////////////

    @Override
    public int getItemCount() {
        return days.size();
    }

    public interface  OnItemListener {
        void onItemClick(int position, LocalDate date);
    }
}