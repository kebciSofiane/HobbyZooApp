package com.example.hobbyzooapp.new_activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.hobbyzooapp.Category;
import com.example.hobbyzooapp.ListAnimals;
import com.example.hobbyzooapp.R;

import java.util.ArrayList;
import java.util.List;

public class NewActivity extends AppCompatActivity {

    String name, animalName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_activity);

        Button validationButton = findViewById(R.id.validationButton);
        ImageView animalImage = findViewById(R.id.animalImage);

        animalImage.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Intent intent = new Intent().setClass(getApplicationContext(), ListAnimals.class);
                startActivity(intent);
            }
        });

        Spinner activitySelector = findViewById(R.id.activityName);
        List<String> categories = new ArrayList();
        categories.add("Sport");
        categories.add("Cuisine");
        categories.add("Art");
        ArrayAdapter adapter = new ArrayAdapter(getApplicationContext(), android.R.layout.simple_list_item_1, categories);
        activitySelector.setAdapter(adapter);



        validationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                name = findViewById(R.id.activityName).toString();
                if(name.trim().isEmpty() || animalName.trim().isEmpty()){
                    Toast.makeText(getApplicationContext(),"Le champ nom ne peut pas être vide!",Toast.LENGTH_LONG).show();
                }
                else{

                    finish();
                }
            }
        });
    }
}
