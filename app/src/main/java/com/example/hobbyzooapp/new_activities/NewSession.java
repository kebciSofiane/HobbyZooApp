package com.example.hobbyzooapp.new_activities;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.example.hobbyzooapp.Category;
import com.example.hobbyzooapp.R;

import java.sql.Array;
import java.sql.Time;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class NewSession extends AppCompatActivity {
    String activityName;
    Time time;
    Date date;


    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_session);

        DatePicker datePicker = findViewById(R.id.datePicker);
        TimePicker timePicker=(TimePicker)findViewById(R.id.timePicker);
        timePicker.setIs24HourView(true);
        timePicker.setHour(0);
        timePicker.setMinute(0);

        Date now = new Date();
        DateFormat dateformatter = DateFormat.getDateInstance(DateFormat.SHORT);
        String formattedDate = dateformatter.format(now);
        //datePicker.setMinDate(Long.parseLong(formattedDate));

        Spinner activitySelector = findViewById(R.id.activityName);
        List<String> activities = new ArrayList();
        activities.add("Bougie");
        activities.add("Cuisine");
        activities.add("Dessin");
        ArrayAdapter adapter = new ArrayAdapter(getApplicationContext(), android.R.layout.simple_list_item_1, activities);
        activitySelector.setAdapter(adapter);
        //activitySelector.setOnItemClickListener(AdapterView.OnItemClickListener);

        Button validationButton = findViewById(R.id.validationButton);
        validationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                activityName = (String) activitySelector.getSelectedItem();
                if(activityName.trim().isEmpty()){
                    Toast.makeText(getApplicationContext(),"Le champ nom ne peut pas être vide!",Toast.LENGTH_LONG).show();
                }
                else if(timePicker.getHour() == 0 && timePicker.getMinute() == 0){
                    Toast.makeText(getApplicationContext(),"Le champ Durée ne peut pas être à 0!",Toast.LENGTH_LONG).show();
                }
                else{
                    Toast.makeText(getApplicationContext(),"name:"+activityName+", time:"+timePicker.getHour()+ ", date:"+datePicker.getDayOfMonth(),Toast.LENGTH_LONG).show();;
                }
            }
        });

    }
}

