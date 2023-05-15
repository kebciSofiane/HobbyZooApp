package com.example.hobbyzooapp.Sessions;

import android.content.Intent;
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

import com.example.hobbyzooapp.R;
import com.example.hobbyzooapp.Activities.NewActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.sql.Time;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class NewSession extends AppCompatActivity {
    String activityName;
    Time time;
    Date date;
    FirebaseAuth firebaseAuth;


    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_session);
        firebaseAuth = FirebaseAuth.getInstance();

        DatePicker datePicker = findViewById(R.id.datePicker);
        TimePicker timePicker=(TimePicker)findViewById(R.id.timePicker);
        timePicker.setIs24HourView(true);
        timePicker.setHour(0);
        timePicker.setMinute(0);

        Spinner activitySelector = findViewById(R.id.activityName);
        List<String> activities = new ArrayList();
        activities.add("Bougie");
        activities.add("Cuisine");
        activities.add("Dessin");
        activities.add("Nouvelle Activité");
        ArrayAdapter adapter = new ArrayAdapter(getApplicationContext(), android.R.layout.simple_list_item_1, activities);
        activitySelector.setAdapter(adapter);
        activitySelector.setOnItemSelectedListener(
                new AdapterView.OnItemSelectedListener() {
                    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {

                        Object item = parent.getItemAtPosition(pos);
                        if(parent.getItemAtPosition(pos) == "Nouvelle Activité"){
                            Intent intent = new Intent().setClass(getApplicationContext(), NewActivity.class);
                            startActivity(intent);
                        }
                        System.out.println(item.toString());

                    }
                    public void onNothingSelected(AdapterView<?> parent) {
                    }
                });

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
                    DatabaseReference databaseReference = FirebaseAuth.getInstance().getReference();

                    DatabaseReference newChildRef = databaseReference.push();
                    String session_id = newChildRef.getKey();
                    HashMap<Object, String> hashMap = new HashMap<>();
                    int minute_duration = timePicker.getMinute() + timePicker.getHour() *60;

                    hashMap.put("session_id", session_id);
                    hashMap.put("session_duration", String.valueOf(minute_duration));
                    hashMap.put("session_picture", "");
                    hashMap.put("session_comment", "");
                    hashMap.put("activity_id", (String) activitySelector.getSelectedItem()); //todo recup id

                    FirebaseDatabase database = FirebaseDatabase.getInstance();

                    DatabaseReference reference = database.getReference("Session");
                    reference.child(session_id).setValue(hashMap);
                }
            }
        });

    }
}

