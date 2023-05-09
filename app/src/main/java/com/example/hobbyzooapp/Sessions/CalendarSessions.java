package com.example.hobbyzooapp.Sessions;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.CalendarView;
import android.widget.ImageButton;

import com.example.hobbyzooapp.R;

public class CalendarSessions extends AppCompatActivity {

    CalendarView calendarSes;
    ImageButton todayBut;
    ImageButton homeBut;
    final String TAG = "CalendarSessions";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar_sessions);

        calendarSes = findViewById(R.id.calendarSes);
        todayBut = findViewById(R.id.todayBut);
        homeBut = findViewById(R.id.homeBut);

        todayBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                calendarSes.setDate(calendarSes.getDate());
            }
        });

        /*ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("");
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);*/

        calendarSes.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                String date = dayOfMonth + "/" + (month+1) + "/" + year;
                Log.d(TAG, "onSelectedDayChange : dd/mm/yyyy : " + date);

                Intent intent = new Intent(CalendarSessions.this, MyDailySessions.class);
                intent.putExtra("date",date);
                intent.putExtra("dayOfMonth", dayOfMonth);
                intent.putExtra("month", month+1);
                intent.putExtra("year", year);
                startActivity(intent);
            }
        });

    }

    public boolean onOptionsItemSelected (@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home){
            this.finish();
        }
        return super.onOptionsItemSelected(item);
    }

}