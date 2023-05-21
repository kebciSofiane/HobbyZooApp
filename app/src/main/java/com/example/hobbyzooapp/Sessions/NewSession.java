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

import java.sql.Time;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class NewSession extends AppCompatActivity {
    String activityName;
    Date date;
    Time time;
    FirebaseAuth firebaseAuth;
    String activity_id;
    FirebaseUser user;


    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_session);
        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();

        DatePicker datePicker = findViewById(R.id.datePicker);
        TimePicker timePicker= findViewById(R.id.timePicker);
        timePicker.setIs24HourView(true);
        timePicker.setHour(0);
        timePicker.setMinute(0);

        Spinner activitySelector = findViewById(R.id.activityName);
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
                            startActivity(intent);
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
                });



        Button validationButton = findViewById(R.id.validationButton);
        validationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                activityName = (String) activitySelector.getSelectedItem();
                if(activityName.trim().isEmpty() || (timePicker.getHour() == 0 && timePicker.getMinute() == 0) || activitySelector.getSelectedItem() == null){
                    Toast.makeText(getApplicationContext(),"Field can't be empty!",Toast.LENGTH_LONG).show();
                }
                else{
                    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

                    DatabaseReference newChildRef = databaseReference.push();
                    String session_id = newChildRef.getKey();
                    HashMap<Object, String> hashMap = new HashMap<>();
                    int minute_duration = timePicker.getMinute() + timePicker.getHour() *60;

                    hashMap.put("session_id", session_id);
                    hashMap.put("session_duration", String.valueOf(minute_duration));
                    hashMap.put("session_picture", "");
                    hashMap.put("session_comment", "");
                    hashMap.put("activity_id", activity_id);

                    FirebaseDatabase database = FirebaseDatabase.getInstance();

                    DatabaseReference reference = database.getReference("Session");
                    reference.child(session_id).setValue(hashMap);
                    Intent intent = new Intent().setClass(getApplicationContext(), HomeActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        });

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
                        activities.add(snapshot.child("activity_name").getValue(String.class));
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

