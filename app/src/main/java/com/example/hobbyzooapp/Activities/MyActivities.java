package com.example.hobbyzooapp.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.hobbyzooapp.Category.Category;
import com.example.hobbyzooapp.HomeActivity;
import com.example.hobbyzooapp.ProfileActivity;
import com.example.hobbyzooapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MyActivities extends AppCompatActivity {

    private ImageButton homeButton, addActivityButton, backButton;
    ExpandableListView expandableListView;
    ExpandableListAdapter expandableListAdapter;
    List<String> expandableListTitle;
    HashMap<String, Category> expandableListDetail;
    String clickedActivityID;
    Button editCategoryButton;

    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_activities);
        firebaseAuth = FirebaseAuth.getInstance();

        //addActivities();

        homeButton = findViewById(R.id.homeButton);
        homeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openMainActivity();
                finish();
            }
        });

        addActivityButton = findViewById(R.id.add_activity_button);
        editCategoryButton= findViewById(R.id.editCategory);

        addActivityButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                startActivity(new Intent(MyActivities.this, NewActivity.class));

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