package com.example.hobbyzooapp.new_activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

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

        Spinner categorySelector = findViewById(R.id.categoryName);
        List<String> categories = new ArrayList();
        categories.add("Sport");
        categories.add("Cuisine");
        categories.add("Art");
        ArrayAdapter adapter = new ArrayAdapter(getApplicationContext(), android.R.layout.simple_list_item_1, categories);
        categorySelector.setAdapter(adapter);



        validationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText text = findViewById(R.id.activityName);
                name = text.getText().toString();
                EditText textAnimal = findViewById(R.id.animalName);
                animalName = textAnimal.getText().toString();
                if(name.trim().isEmpty() || animalName.trim().isEmpty()){
                    Toast.makeText(getApplicationContext(),"Le champ nom ne peut pas être vide!",Toast.LENGTH_LONG).show();
                }
                else{

                    Toast.makeText(getApplicationContext(),"name: "+name+", c: "+categorySelector.getSelectedItem()+", nAni: "+animalName,Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}
