package com.example.hobbyzooapp.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.GridView;

import com.example.hobbyzooapp.OnItemClickListener;
import com.example.hobbyzooapp.R;

import java.util.ArrayList;
import java.util.List;

public class MyActivities extends AppCompatActivity {

    private Button homeButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_activity);

        homeButton = findViewById(R.id.homeButton);
        homeButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                openMainActivity();
            }
        });

        List<Activity> activityList = new ArrayList<>();
        activityList.add(new Activity("Dessin","Biquette",null,10,"sheep"));
        activityList.add(new Activity("Bougie","Coco",null,10,"koala"));

        GridView activityListView = findViewById(R.id.activity_list_view);
        ActivityListAdapter adapter = new ActivityListAdapter(this,activityList);
        adapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                openMainActivity();
            }
        });
        activityListView.setAdapter(adapter);




        //activityListView.setAdapter(new ActivityAdapter(this,activityList));
    }
    public void openMainActivity(){
        Intent intent = new Intent(this, ActivityPage.class);
        startActivity(intent);
        finish();
    }

    //SELECT * from activities a, activities b where categorie.a=categorie.b


}