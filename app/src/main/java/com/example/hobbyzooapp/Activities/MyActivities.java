package com.example.hobbyzooapp.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.ImageButton;

import com.example.hobbyzooapp.Category.Category;
import com.example.hobbyzooapp.HomeActivity;
import com.example.hobbyzooapp.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MyActivities extends AppCompatActivity {

    private ImageButton homeButton;
    private Button addActivityButton;
    ExpandableListView expandableListView;
    ExpandableListAdapter expandableListAdapter;
    List<String> expandableListTitle;
    HashMap<String, List<Activity>> expandableListDetail;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_activities);

        homeButton = findViewById(R.id.homeButton);
        homeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openMainActivity();
            }
        });

        addActivityButton = findViewById(R.id.add_activity_button);
        addActivityButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                startActivity(new Intent(MyActivities.this, NewActivity.class));
            }
        });

        expandableListView = (ExpandableListView) findViewById(R.id.expandableListView);





        expandableListView = (ExpandableListView) findViewById(R.id.expandableListView);
        expandableListDetail = ExpandableListData.getData();
        expandableListTitle = new ArrayList<String>(expandableListDetail.keySet());
        expandableListAdapter = new ExpandableListAdapter(this, expandableListTitle, expandableListDetail);
        expandableListView.setAdapter(expandableListAdapter);


        expandableListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {


            @Override
            public boolean onChildClick(ExpandableListView parent, View v,
                                        int groupPosition, int childPosition, long id) {
                openActivityPage();
                return true;
            }
        });


        ArrayList<Activity> activities = new ArrayList<>();
        activities.add(new Activity("Dessin", "Biquette", 10, "sheep"));
        activities.add(new Activity("poetry", "Coco", 10, "koala"));
        activities.add(new Activity("poetry", "Coco", 10, "koala"));

        ArrayList<Activity> activities2 = new ArrayList<>();
        activities2.add(new Activity("Muscu", "Biquette", 10, "sheep"));

        ArrayList<Activity> activities3 = new ArrayList<>();
        activities3.add(new Activity("Patisserie", "Biquette", 10, "sheep"));


        List<Category> category = new ArrayList<>();
        category.add(new Category(10, "Art", null, activities));
        category.add(new Category(11, "Sport", null, activities2));
        category.add(new Category(11, "Cuisine", null, activities3));


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