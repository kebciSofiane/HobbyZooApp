package com.example.hobbyzooapp.Calendar;
import com.example.hobbyzooapp.R;
import com.example.hobbyzooapp.Sessions.MyDailySessions;
import com.example.hobbyzooapp.Sessions.Session;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import java.time.LocalDate;
import java.util.ArrayList;

import android.util.Log;


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
            if(date.equals(CalendarUtils.selectedDate)){
                GradientDrawable borderDrawable = new GradientDrawable();
                borderDrawable.setShape(GradientDrawable.RECTANGLE);
                borderDrawable.setCornerRadius(10);
                borderDrawable.setStroke(4, Color.BLACK);
                holder.parentView.setBackground(borderDrawable);

                //holder.parentView.setBackgroundColor(Color.LTGRAY);
            }


            getSessions(holder,date);


        }
    }



    @Override
    public int getItemCount() {
        return days.size();
    }

    public interface  OnItemListener {
        void onItemClick(int position, LocalDate date);
    }



    private void getSessions(CalendarViewHolder holder,LocalDate date) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference reference = database.getReference("Session");
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser user = firebaseAuth.getCurrentUser();
        String uid = user.getUid();
        ArrayList<Session> mySessions = new ArrayList<>();
        reference.orderByChild("user_id").equalTo(uid).addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String session_id = snapshot.child("session_id").getValue(String.class);
                    String session_duration = snapshot.child("session_duration").getValue(String.class);
                    String activity_id = snapshot.child("activity_id").getValue(String.class);
                    String session_day = snapshot.child("session_day").getValue(String.class);
                    String session_month = snapshot.child("session_month").getValue(String.class);
                    String session_year = snapshot.child("session_year").getValue(String.class);
                    String session_done = snapshot.child("session_done").getValue(String.class);




                    DatabaseReference referenceActivity = database.getReference("Activity");

                    referenceActivity.child(activity_id).addListenerForSingleValueEvent(new ValueEventListener() {
                        @RequiresApi(api = Build.VERSION_CODES.O)
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()) {
                                String activityName = dataSnapshot.child("activity_name").getValue(String.class);
                                int hourDuration = Integer.parseInt(session_duration) / 60;
                                int minutesDuration = Integer.parseInt(session_duration) % 60;
                                LocalDate sessionDate = LocalDate.of(Integer.parseInt(session_year), Integer.parseInt(session_month), Integer.parseInt(session_day));

                                if (date.getMonth() == sessionDate.getMonth() &&
                                        date.getDayOfMonth() == sessionDate.getDayOfMonth()&&
                                        date.getYear() == sessionDate.getYear()) {
                                    GradientDrawable backgroundDrawable = new GradientDrawable();
                                    backgroundDrawable.setShape(GradientDrawable.RECTANGLE);
                                    backgroundDrawable.setCornerRadius(10); // Définissez ici le rayon de courbure des coins (en pixels)
                                    backgroundDrawable.setColor(Color.RED);
                                    holder.itemView.setBackground(backgroundDrawable);
                                }
                            } else {
                                // L'activité n'existe pas dans la base de données
                            }


                        }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                                // Une erreur s'est produite lors de la récupération des données
                            }
                        });

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w("TAG", "Erreur lors de la récupération des données", databaseError.toException());
            }
        });
    }

}