package com.example.hobbyzooapp.Calendar;
import com.example.hobbyzooapp.HomeActivity;
import com.example.hobbyzooapp.R;
import com.example.hobbyzooapp.Sessions.MyDailySessions;
import com.google.firebase.auth.FirebaseAuth;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import java.time.LocalDate;
import java.util.ArrayList;

import static com.example.hobbyzooapp.Calendar.CalendarUtils.daysInMonthArray;
import static com.example.hobbyzooapp.Calendar.CalendarUtils.monthYearFromDate;

public class CalendarActivity extends AppCompatActivity implements CalendarAdapter.OnItemListener
{
    private TextView monthYearText;
    private RecyclerView calendarRecyclerView;
    private Button todaySessionButton;
    private Button todayMonthButton;
    private ImageButton homeButton;
    FirebaseAuth firebaseAuth;


    @SuppressLint("MissingInflatedId")
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);
        firebaseAuth = FirebaseAuth.getInstance();
        initWidgets();
        CalendarUtils.selectedDate = LocalDate.now();
        setMonthView();



        todaySessionButton = findViewById(R.id.today_session);
        todaySessionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CalendarUtils.selectedDate = LocalDate.now();
                openTodaySessions();
            }
        });

        todayMonthButton = findViewById(R.id.today_month);
        todayMonthButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CalendarUtils.selectedDate = LocalDate.now();
                setMonthView();
            }
        });

        homeButton = findViewById(R.id.home_button);
        homeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openMainActivity();
                finish();
            }
        });

    }

    private void initWidgets() {
        calendarRecyclerView = findViewById(R.id.calendarRecyclerView);
        monthYearText = findViewById(R.id.monthYearTV);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void setMonthView() {
        monthYearText.setText(monthYearFromDate(CalendarUtils.selectedDate));
        ArrayList<LocalDate> daysInMonth = daysInMonthArray(CalendarUtils.selectedDate);

        CalendarAdapter calendarAdapter = new CalendarAdapter(daysInMonth, this);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getApplicationContext(), 7);
        calendarRecyclerView.setLayoutManager(layoutManager);
        calendarRecyclerView.setAdapter(calendarAdapter);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void previousMonthAction(View view)
    {
        CalendarUtils.selectedDate = CalendarUtils.selectedDate.minusMonths(1);
        setMonthView();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void nextMonthAction(View view)
    {
        CalendarUtils.selectedDate = CalendarUtils.selectedDate.plusMonths(1);
        setMonthView();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onItemClick(int position, LocalDate date)
    {
        if(date != null)
        {
            CalendarUtils.selectedDate = date;
            MyDailySessions.localDate=date;
            openTodaySessions();
        }
    }


    public void openTodaySessions(){
        Intent intent = new Intent(this, MyDailySessions.class);
        startActivity(intent);}

    public void openMainActivity(){
        Intent intent = new Intent(this, HomeActivity.class);
        startActivity(intent);
        finish();}


}







