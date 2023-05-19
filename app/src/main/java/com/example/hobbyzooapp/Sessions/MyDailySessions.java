package com.example.hobbyzooapp.Sessions;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.TextView;


import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.hobbyzooapp.Calendar.CalendarActivity;
import com.example.hobbyzooapp.Calendar.CalendarUtils;
import com.example.hobbyzooapp.HomeActivity;
import com.example.hobbyzooapp.R;
import com.example.hobbyzooapp.OnItemClickListener;
import com.google.firebase.auth.FirebaseAuth;

import java.sql.Time;
import java.time.LocalDate;
import java.util.ArrayList;

public class MyDailySessions extends AppCompatActivity {

    private ImageButton homeButton;
    private Button addSessionButton;
    private ImageButton calendarButton;
    private View sessionButton;
    FirebaseAuth firebaseAuth;

    public ArrayList<Session> sessionList;


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_daily_sessions);
        firebaseAuth = FirebaseAuth.getInstance();

        homeButton = findViewById(R.id.home_button);
        homeButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                openMainActivity();
                finish();
            }
        });

        addSessionButton = findViewById(R.id.add_session_button);
        addSessionButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                startActivity(new Intent(MyDailySessions.this, NewSession.class));
            }
        });

        calendarButton = findViewById(R.id.calendar_button);
        calendarButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                openCalendar();
            }
        });

        sessionButton = findViewById(R.id.session);
        sessionList = new ArrayList<>();

        sessionList.add(new Session("Dessin", new Time(0, 1, 10), 10, 10,2023));
        sessionList.add(new Session("Bougie",new Time(0,2,0),15,5,2023));


        GridView sessionListView = findViewById(R.id.session_list_view);
        LocalDate localDate = CalendarUtils.selectedDate;
        MyDailySessionsAdapter adapter = new MyDailySessionsAdapter(this,sessionList,localDate);

        TextView dateSession = findViewById(R.id.dateSession);
        String date = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            date = localDate.getDayOfMonth() +"/"+ localDate.getMonth().getValue() +"/"+localDate.getYear();
        }
        dateSession.setText(date);


        adapter.setOnItemClickListener(new OnItemClickListener(){
            @Override
            public void onItemClick(int position) {
                new AlertDialog.Builder(MyDailySessions.this)
                        .setTitle("Vous avez selectionné une session de "+adapter.getItem(position).getName()+" de "+adapter.getItem(position).getTime())
                        .setMessage("Voulez-vous commencer cette session ?")
                        .setNegativeButton(android.R.string.no, null)
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface arg0, int arg1) {
                                openRunSession();
                            }
                        }).create().show();
            }
        });
        sessionListView.setAdapter(adapter);

        }


    public void openMainActivity(){
        Intent intent = new Intent(this, HomeActivity.class);
        startActivity(intent);

    }

    public void openCalendar(){
        Intent intent = new Intent(this, CalendarActivity.class);
        startActivity(intent);
    }

    public void openRunSession(){
        Intent intent = new Intent(this, RunSession.class);
        startActivity(intent);

    }



}