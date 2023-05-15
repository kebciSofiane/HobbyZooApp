package com.example.hobbyzooapp.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.hobbyzooapp.Category.NewCategory;
import com.example.hobbyzooapp.ListAnimals;
import com.example.hobbyzooapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class NewActivity extends AppCompatActivity {

    String name, animalName;
    FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_activity);
        firebaseAuth = FirebaseAuth.getInstance();

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
        categories.add("Nouvelle Catégorie");
        ArrayAdapter adapter = new ArrayAdapter(getApplicationContext(), android.R.layout.simple_list_item_1, categories);
        categorySelector.setAdapter(adapter);
        categorySelector.setOnItemSelectedListener(
                new AdapterView.OnItemSelectedListener() {
                    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {

                        Object item = parent.getItemAtPosition(pos);
                        if(parent.getItemAtPosition(pos) == "Nouvelle Catégorie"){
                            Intent intent = new Intent().setClass(getApplicationContext(), NewCategory.class);
                            startActivity(intent);
                        }
                        System.out.println(item.toString());

                    }
                    public void onNothingSelected(AdapterView<?> parent) {
                    }
                });



        validationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText text = findViewById(R.id.activityName);
                EditText weekly_goal = findViewById(R.id.weeklyGoal);
                name = text.getText().toString();
                EditText textAnimal = findViewById(R.id.animalName);
                animalName = textAnimal.getText().toString();
                FirebaseUser user = firebaseAuth.getCurrentUser();

                if(name.trim().isEmpty() || animalName.trim().isEmpty()){
                    Toast.makeText(getApplicationContext(),"Le champ nom ne peut pas être vide!",Toast.LENGTH_LONG).show();
                }
                else {

                    DatabaseReference databaseReference = FirebaseAuth.getInstance().getReference();

                    DatabaseReference newChildRef = databaseReference.push();
                    String activity_id = newChildRef.getKey();
                    HashMap<Object, String> hashMap = new HashMap<>();

                    hashMap.put("activity_id", activity_id);
                    hashMap.put("activity_name",name);
                    hashMap.put("activity_pet_name", animalName);
                    hashMap.put("activity_pet", "@drawable/sheep"); //todo faire input
                    hashMap.put("weekly_goal", String.valueOf(weekly_goal.getText()));
                    hashMap.put("spent_time", "0");
                    hashMap.put("category_id", (String) categorySelector.getSelectedItem()); //todo recup id
                    hashMap.put("user_id", user.getUid());

                    FirebaseDatabase database = FirebaseDatabase.getInstance();

                    DatabaseReference reference = database.getReference("Activity");
                    reference.child(activity_id).setValue(hashMap);
                }
            }
        });
    }
}
