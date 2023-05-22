package com.example.hobbyzooapp.Calendar;
import com.example.hobbyzooapp.OnItemClickListener;
import com.example.hobbyzooapp.R;
import com.example.hobbyzooapp.Sessions.MyDailySessions;
import com.example.hobbyzooapp.Sessions.Session;
import com.example.hobbyzooapp.Sessions.MyDailySessionsAdapter;
import com.example.hobbyzooapp.Sessions.SessionsCallback;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.GridView;
import android.widget.TextView;

import java.sql.Time;
import java.time.LocalDate;
import java.util.ArrayList;

import static com.example.hobbyzooapp.Calendar.CalendarUtils.daysInWeekArray;
import static com.example.hobbyzooapp.Calendar.CalendarUtils.monthYearFromDate;

public class WeekViewActivity extends AppCompatActivity implements CalendarAdapter.OnItemListener
{
    private TextView monthYearText;
    private RecyclerView calendarRecyclerView;
    private GridView eventListView;
    FirebaseAuth firebaseAuth;
    MyDailySessionsAdapter adapter;
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_week_view);
        firebaseAuth = FirebaseAuth.getInstance();
        initWidgets();
        setWeekView();


        GridView eventListView = findViewById(R.id.eventListView);
        LocalDate localDate = CalendarUtils.selectedDate;


        ArrayList<Session> sessions;

        SessionsCallback callback = new SessionsCallback() {
            @Override
            public void onSessionsLoaded(ArrayList<Session> mySessions) {
                adapter = new MyDailySessionsAdapter(WeekViewActivity.this, mySessions, localDate);
                eventListView.setAdapter(adapter);
            }
        };
        sessions= getSessions(callback);



    }

    private void initWidgets()
    {
        calendarRecyclerView = findViewById(R.id.calendarRecyclerView);
        monthYearText = findViewById(R.id.monthYearTV);
        eventListView = findViewById(R.id.eventListView);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void setWeekView()
    {
        monthYearText.setText(monthYearFromDate(CalendarUtils.selectedDate));
        ArrayList<LocalDate> days = daysInWeekArray(CalendarUtils.selectedDate);

        CalendarAdapter calendarAdapter = new CalendarAdapter(days, this);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getApplicationContext(), 7);
        calendarRecyclerView.setLayoutManager(layoutManager);
        calendarRecyclerView.setAdapter(calendarAdapter);
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    public void previousWeekAction(View view)
    {
        CalendarUtils.selectedDate = CalendarUtils.selectedDate.minusWeeks(1);
        setWeekView();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void nextWeekAction(View view)
    {
        CalendarUtils.selectedDate = CalendarUtils.selectedDate.plusWeeks(1);
        setWeekView();
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onItemClick(int position, LocalDate date)
    {
        CalendarUtils.selectedDate = date;
        setWeekView();

    }
    private ArrayList<Session> getSessions(SessionsCallback callback) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference reference = database.getReference("Session");
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser user = firebaseAuth.getCurrentUser();
        String uid = user.getUid();
        ArrayList<Session> mySessions = new ArrayList<>();
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String session_id = snapshot.child("session_id").getValue(String.class);
                    String session_duration = snapshot.child("session_duration").getValue(String.class);
                    String activity_id = snapshot.child("activity_id").getValue(String.class);

                    DatabaseReference referenceActivity = database.getReference("Activity");

                    referenceActivity.child(activity_id).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()) {
                                String activityName = dataSnapshot.child("activity_name").getValue(String.class);
                                int hourDuration = Integer.parseInt(session_duration)/60;
                                int minutesDuration = Integer.parseInt(session_duration)%60;

                                mySessions.add(new Session(session_id,activity_id,activityName,new Time(hourDuration,minutesDuration,0),22,5,2023));
                                callback.onSessionsLoaded(mySessions);
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
        return mySessions;
    }

}