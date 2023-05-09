package com.example.hobbyzooapp.Sessions;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TextView;


import androidx.appcompat.app.AppCompatActivity;

import com.example.hobbyzooapp.Activities.ActivityPage;
import com.example.hobbyzooapp.MainActivity;
import com.example.hobbyzooapp.R;
import com.example.hobbyzooapp.OnItemClickListener;

import java.sql.Time;
import java.util.ArrayList;
import java.util.List;

public class MyDailySessions extends AppCompatActivity {

    private Button homeButton;
    private View sessionButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_daily_sessions);

        homeButton = findViewById(R.id.homeButton);
        homeButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                openMainActivity();
            }
        });

        sessionButton = findViewById(R.id.session);


        List<Session> sessionList = new ArrayList<>();
        sessionList.add(new Session("Dessin", new Time(0, 1, 10), 10, 10,2023));
        sessionList.add(new Session("Bougie",new Time(0,2,0),15,5,2023));

        //date depuis calendrier
        TextView dateSession = findViewById(R.id.dateSession); //mettre TextView à la date
        Intent incomingIntent = getIntent();
        //String date = incomingIntent.getStringExtra("date");
        //dateSession.setText(date);
        int month = incomingIntent.getIntExtra("month", 0);
        int day = incomingIntent.getIntExtra("dayOfMonth", 00);
        int year = incomingIntent.getIntExtra("year",0000);
        String date = day + "," + month + "," + year;
        dateSession.setText(date);


        GridView sessionListView = findViewById(R.id.session_list_view);
        DailySessionAdapter adapter = new DailySessionAdapter(this,sessionList,10,10,2023);
        sessionListView.setAdapter(adapter);
        adapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                openActivityPage();
            }
        });


        }


    public void openMainActivity(){
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);

    }

    public void openActivityPage(){
        Intent intent = new Intent(this, ActivityPage.class);
        startActivity(intent);

    }
}