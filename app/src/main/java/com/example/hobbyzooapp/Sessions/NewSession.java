package com.example.hobbyzooapp.Sessions;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.example.hobbyzooapp.HomeActivity;
import com.example.hobbyzooapp.R;
import com.example.hobbyzooapp.Activities.NewActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class NewSession extends AppCompatActivity {
    String activityName, activity_id;
    DatePicker datePicker;
    TimePicker timePicker;
    FirebaseAuth firebaseAuth;
    FirebaseUser user;
    Spinner activitySelector;
    ImageView validationButton, returnButton;


    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_session);
        initialisation();
        List<String> activities = setActivities();
        activities.add("");

        ArrayAdapter adapter = new ArrayAdapter(getApplicationContext(), android.R.layout.simple_list_item_1, activities);
        activitySelector.setAdapter(adapter);
        activitySelector.setOnItemSelectedListener(
                new AdapterView.OnItemSelectedListener() {
                    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {

                        Object item = parent.getItemAtPosition(pos);
                        if(parent.getItemAtPosition(pos) == "New Activity"){
                            Intent intent = new Intent().setClass(getApplicationContext(), NewActivity.class);
                            intent.putExtra("origin", 1);
                            startActivity(intent);
                            finish();
                        }
                        final String[] activity_id_select = new String[1];
                        String user_id = user.getUid();
                        DatabaseReference databaseReferenceChild = FirebaseDatabase.getInstance().getReference().child("Activity");
                        String activity_name_selected = (String) activitySelector.getSelectedItem();
                        databaseReferenceChild.orderByChild("user_id").equalTo(user_id).addListenerForSingleValueEvent(new ValueEventListener() {

                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                    String user_id_verify = snapshot.child("user_id").getValue(String.class);
                                    String activity_name = snapshot.child("activity_name").getValue(String.class);
                                    if(user_id.equals(user_id_verify) && activity_name.equals(activity_name_selected)){
                                        String activity_id_verify = snapshot.child("activity_id").getValue(String.class);
                                        activity_id_select[0] = activity_id_verify;
                                    }
                                    activity_id = activity_id_select[0];

                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                                // Gérez l'erreur en cas d'annulation de la requête
                            }
                        });
                        activitySelector.setSelection(pos);

                    }
                    public void onNothingSelected(AdapterView<?> parent) {
                    }
                });//todo penser verif si les espaces fonctionnent dans les noms


        returnButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        validationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                activityName = (String) activitySelector.getSelectedItem();
                LocalDate dateCourante = null;
                int selectedYear = datePicker.getYear();
                int selectedMonth = datePicker.getMonth() + 1;
                int selectedDay = datePicker.getDayOfMonth();
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    dateCourante = LocalDate.now();
                }
                if(activityName.trim().isEmpty() || (timePicker.getHour() == 0 && timePicker.getMinute() == 0) || activitySelector.getSelectedItem() == null){
                    Toast.makeText(getApplicationContext(),"Field can't be empty!",Toast.LENGTH_LONG).show();
                }
                else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    if (dateCourante.isAfter(LocalDate.of(selectedYear, selectedMonth, selectedDay))) {
                        Toast.makeText(getApplicationContext(),"The date can't be earlier!",Toast.LENGTH_LONG).show();
                    }
                    else{
                        addDBSession();
                        Intent intent = new Intent().setClass(getApplicationContext(), HomeActivity.class);
                        startActivity(intent);
                        finish();
                    }
                }
            }

        });

    }

    private void initialisation(){
        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();
        datePicker = findViewById(R.id.datePicker);
        timePicker = findViewById(R.id.timePicker);
        timePicker.setIs24HourView(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            timePicker.setHour(0);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            timePicker.setMinute(0);
        }
        activitySelector = findViewById(R.id.activityName);
        validationButton = findViewById(R.id.validationButton);
        returnButton = findViewById(R.id.returnButton);
    }

    private void addDBSession(){
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        DatabaseReference newChildRef = databaseReference.push();
        String session_id = newChildRef.getKey();
        HashMap<Object, String> hashMap = new HashMap<>();
        int minute_duration = 0;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            minute_duration = timePicker.getMinute() + timePicker.getHour() *60;
        }

        hashMap.put("session_id", session_id);
        hashMap.put("session_day", String.valueOf(datePicker.getDayOfMonth()));
        hashMap.put("session_month", String.valueOf(datePicker.getMonth()+1));
        hashMap.put("session_year", String.valueOf(datePicker.getYear()));
        hashMap.put("session_duration", String.valueOf(minute_duration));
        hashMap.put("session_time", "");
        hashMap.put("session_done", "FALSE");
        hashMap.put("session_picture", "");
        hashMap.put("session_comment", "");
        hashMap.put("activity_id", activity_id);
        hashMap.put("user_id", user.getUid());

        FirebaseDatabase database = FirebaseDatabase.getInstance();

        DatabaseReference reference = database.getReference("Session");
        reference.child(session_id).setValue(hashMap);
    }

    List<String> setActivities(){
        List<String> activities = new ArrayList<>();
        DatabaseReference databaseReferenceChild = FirebaseDatabase.getInstance().getReference().child("Activity");
        databaseReferenceChild.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String user_id = user.getUid();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String user_id_cat = snapshot.child("user_id").getValue(String.class);
                    if(user_id.equals(user_id_cat))
                        activities.add(snapshot.child("activity_name").getValue(String.class).replace(",", " "));
                }
                activities.add("New Activity");
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Gérez l'erreur en cas d'annulation de la requête
            }
        });
        return activities;
    }
}

