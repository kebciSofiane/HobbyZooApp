package com.example.hobbyzooapp;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hobbyzooapp.Calendar.CalendarAdapter;
import com.example.hobbyzooapp.Calendar.CalendarUtils;
import com.example.hobbyzooapp.Calendar.CalendarViewHolder;
import com.example.hobbyzooapp.Sessions.MyDailySessions;
import com.example.hobbyzooapp.Sessions.MyDailySessionsAdapter;
import com.example.hobbyzooapp.Sessions.RunSession;
import com.example.hobbyzooapp.Sessions.Session;
import com.example.hobbyzooapp.Sessions.SessionsCallback;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.sql.Time;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;

public class CalendarEvolutionAdapter extends RecyclerView.Adapter<CalendarViewHolder> {

        private final ArrayList<LocalDate> days;
        private  int evenDayColor;

    private final com.example.hobbyzooapp.Calendar.CalendarAdapter.OnItemListener onItemListener;

        public CalendarEvolutionAdapter(ArrayList<LocalDate> days,
                                        com.example.hobbyzooapp.Calendar.CalendarAdapter.OnItemListener onItemListener,
        int evenDayColor)
        {
            this.evenDayColor =evenDayColor;
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
        public void onBindViewHolder(@NonNull CalendarViewHolder holder, int position)
        {
            final LocalDate date = days.get(position);
            if(date == null)
                holder.dayOfMonth.setText("");

            else
            {
                holder.dayOfMonth.setText(String.valueOf(date.getDayOfMonth()));
                if(date.equals(CalendarUtils.selectedDate))
                    holder.parentView.setBackgroundColor(Color.LTGRAY);

                getSessions(holder,date);


//                if (date.getDayOfMonth() % 2 == 0) {
//                    holder.itemView.setBackgroundColor(evenDayColor);
//                } else {
//                    holder.itemView.setBackgroundColor(Color.TRANSPARENT);
//                }
            }


        }

        @Override
        public int getItemCount()
        {
            return days.size();
        }

        public interface  OnItemListener
        {
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


                    if (session_done.equals("TRUE")) {

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
                                    holder.itemView.setBackgroundColor(evenDayColor);
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
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w("TAG", "Erreur lors de la récupération des données", databaseError.toException());
            }
        });
    }

}
