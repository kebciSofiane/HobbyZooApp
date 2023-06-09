package com.example.hobbyzooapp.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.ImageButton;

import com.example.hobbyzooapp.Category.Category;
import com.example.hobbyzooapp.HomeActivity;
import com.example.hobbyzooapp.R;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MyActivities extends AppCompatActivity {

    ImageButton homeButton, addActivityButton, backButton;
    ExpandableListView expandableListView;
    ExpandableListAdapter expandableListAdapter;
    List<String> expandableListTitle;
    HashMap<String, Category> expandableListDetail;
    String clickedActivityID;
    Button editCategoryButton;

    private FirebaseAuth firebaseAuth;


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_activities);
        firebaseAuth = FirebaseAuth.getInstance();

        editCategoryButton= findViewById(R.id.editCategory);
        homeButton = findViewById(R.id.homeButton);
        homeButton.setOnClickListener(v -> {
            finishAffinity();
            openMainActivity();
            finish();
        });

        addActivityButton = findViewById(R.id.add_activity_button);
        addActivityButton.setOnClickListener(v -> {
            Intent intent = new Intent(MyActivities.this, NewActivity.class);
            intent.putExtra("previousActivity", 0);
            startActivity(intent);

        });

        ActivitiesCallBack callback = expandableListDetail -> {
            expandableListTitle = new ArrayList<String>(expandableListDetail.keySet());
            expandableListAdapter = new ExpandableListAdapter(MyActivities.this, expandableListTitle, expandableListDetail);
            expandableListView.setAdapter(expandableListAdapter);

        };

        expandableListView = (ExpandableListView) findViewById(R.id.expandableListView);
        expandableListDetail = ExpandableListData.getActivities(callback);

        expandableListView.setOnChildClickListener((parent, v, groupPosition, childPosition, id) -> {
            Category category = expandableListDetail.get(expandableListTitle.get(groupPosition));
            clickedActivityID = category.getActivities().get(childPosition).getActivity_id();


            Intent intent = new Intent(MyActivities.this, ActivityPage.class);

            intent.putExtra("activity_id", clickedActivityID);
            intent.putExtra("previousActivity", 1);
            startActivity(intent);
            finish();
            return true;
        });

        backButton = findViewById(R.id.backButtonMyActivities);
        backButton.setOnClickListener(v -> finish());
    }

    public void openMainActivity(){
        Intent intent = new Intent(this, HomeActivity.class);
        startActivity(intent);
    }
}