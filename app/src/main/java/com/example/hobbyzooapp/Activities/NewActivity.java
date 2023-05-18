package com.example.hobbyzooapp.Activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.hobbyzooapp.Category.NewCategory;
import com.example.hobbyzooapp.ListAnimals;
import com.example.hobbyzooapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class NewActivity extends AppCompatActivity {

    String name, animalName;
    FirebaseAuth firebaseAuth;
    Spinner categorySelector;
    FirebaseUser user;
    List<String> animals;
    int posAnimals;
    ImageView animalImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_activity);
        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();
        Button validationButton = findViewById(R.id.validationButton);
        animalImage = findViewById(R.id.animalImage);
        Button scrollAnimalsRight = findViewById(R.id.scrollAnimalsRight);
        setAnimals();

        scrollAnimalsRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(posAnimals < animals.size()-1)
                    posAnimals++;
                else
                    posAnimals = 0;
                int animalId = getResources().getIdentifier(animals.get(posAnimals), "drawable", getPackageName());
                animalImage.setImageResource(animalId);
                animalImage.invalidate();

            }
        });

        categorySelector = findViewById(R.id.categoryName);
        List<String> categories = setCategorySelector();
        ArrayAdapter adapter = new ArrayAdapter(getApplicationContext(), android.R.layout.simple_list_item_1, categories);
        categorySelector.setAdapter(adapter);

        categorySelector.setOnItemSelectedListener(
                new AdapterView.OnItemSelectedListener() {
                    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {

                        Object item = parent.getItemAtPosition(pos);
                        if(parent.getItemAtPosition(pos) == "New Category"){
                            Intent intent = new Intent().setClass(getApplicationContext(), NewCategory.class);
                            startActivity(intent);
                        }

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

                if(name.trim().isEmpty() || animalName.trim().isEmpty()){
                    Toast.makeText(getApplicationContext(),"Le champ nom ne peut pas être vide!",Toast.LENGTH_LONG).show();
                }
                else {
                    final String[] category_id = new String[1];

                    DatabaseReference databaseReferenceChild = FirebaseDatabase.getInstance().getReference().child("Category");

                    databaseReferenceChild.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                String user_id = snapshot.child("user_id").getValue(String.class);
                                String category_name = snapshot.child("category_name").getValue(String.class);
                                if(user_id == user.getUid() && category_name == (String) categorySelector.getSelectedItem())
                                    category_id[0] = snapshot.child("category_id").getValue(String.class);
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            // Gérez l'erreur en cas d'annulation de la requête
                        }
                    });


                    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

                    DatabaseReference newChildRef = databaseReference.push();
                    String activity_id = newChildRef.getKey();
                    HashMap<Object, String> hashMap = new HashMap<>();

                    hashMap.put("activity_id", activity_id);
                    hashMap.put("activity_name",name);
                    hashMap.put("activity_pet_name", animalName);
                    hashMap.put("activity_pet", "@drawable/sheep"); //todo faire input
                    hashMap.put("weekly_goal", String.valueOf(weekly_goal.getText()));
                    hashMap.put("spent_time", "0");
                    hashMap.put("category_id", category_id[0]);
                    hashMap.put("user_id", user.getUid());

                    FirebaseDatabase database = FirebaseDatabase.getInstance();

                    DatabaseReference reference = database.getReference("Activity");
                    reference.child(activity_id).setValue(hashMap);
                }
            }
        });
    }

    private void setAnimals() {
        posAnimals = 0;
        animals = new ArrayList<>(Arrays.asList("sheep", "cat", "chick", "giraffe", "cow", "koa", "lion", "rabbit", "tiger", "tl"));

    }

    List<String> setCategorySelector(){
        List<String> categories = new ArrayList<>();
        DatabaseReference databaseReferenceChild = FirebaseDatabase.getInstance().getReference().child("Category");

        databaseReferenceChild.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String user_id = user.getUid();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String user_id_cat = snapshot.child("user_id").getValue(String.class);
                    if(user_id.equals(user_id_cat))
                       categories.add(snapshot.child("category_name").getValue(String.class));
                }
                categories.add("New Category");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Gérez l'erreur en cas d'annulation de la requête
            }
        });
        return categories;
    }



}
