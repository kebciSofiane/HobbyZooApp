package com.example.hobbyzooapp.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.ImageButton;

import com.example.hobbyzooapp.Category.Category;
import com.example.hobbyzooapp.HomeActivity;
import com.example.hobbyzooapp.AccountManagement.ProfileActivity;
import com.example.hobbyzooapp.R;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MyActivities extends AppCompatActivity {

    ImageButton homeButton, addActivityButton, profileButton, backButton;
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

        //addActivities();

        editCategoryButton= findViewById(R.id.editCategory);
        homeButton = findViewById(R.id.homeButton);
        homeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openMainActivity();
                finish();
            }
        });

        addActivityButton = findViewById(R.id.add_activity_button);
        addActivityButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                startActivity(new Intent(MyActivities.this, NewActivity.class));

            }
        });


        profileButton= findViewById(R.id.profile_btn);
        profileButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                startActivity(new Intent(MyActivities.this, ProfileActivity.class));

            }
        });
        ActivitiesCallBack callback = new ActivitiesCallBack() {
            @Override
            public void onActivitiesLoaded(HashMap<String, Category> expandableListDetail) {
                expandableListTitle = new ArrayList<String>(expandableListDetail.keySet());
                expandableListAdapter = new ExpandableListAdapter(MyActivities.this, expandableListTitle, expandableListDetail);
                expandableListView.setAdapter(expandableListAdapter);

            }
        };

        expandableListView = (ExpandableListView) findViewById(R.id.expandableListView);
        expandableListDetail = ExpandableListData.getActivities(callback);

        expandableListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {

            @Override
            public boolean onChildClick(ExpandableListView parent, View v,
                                        int groupPosition, int childPosition, long id) {
                Category category = expandableListDetail.get(expandableListTitle.get(groupPosition));
                clickedActivityID = category.getActivities().get(childPosition).getActivity_id();


                Intent intent = new Intent(MyActivities.this, ActivityPage.class);
                //Toast.makeText(getApplicationContext(), clickedActivityID, Toast.LENGTH_SHORT).show();

                intent.putExtra("activity_id", clickedActivityID);
                startActivity(intent);
                return true;
            }

        });

        backButton = findViewById(R.id.backButtonMyActivities);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MyActivities.this, ProfileActivity.class));
            }
        });

        //GridView activityListView = findViewById(R.id.category_list_view);
        //CategoryListAdapter adapter = new CategoryListAdapter(this,category);
        //adapter.setOnItemClickListener(new OnItemClickListener() {
    }

    public void openMainActivity(){
        Intent intent = new Intent(this, HomeActivity.class);
        startActivity(intent);
    }

    public void openActivityPage(){
        Intent intent = new Intent(this, ActivityPage.class);
        startActivity(intent);
    }
}