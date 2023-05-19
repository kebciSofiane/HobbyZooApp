package com.example.hobbyzooapp.Activities;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.example.hobbyzooapp.Category.NewCategory;
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

public class NewActivity extends AppCompatActivity {

    String name, animalName;
    FirebaseAuth firebaseAuth;
    Spinner categorySelector;
    FirebaseUser user;
    String firstCategory = null;
    TimePicker weeklyGoal;
    String category_id;
    Button validationButton;
    ImageView animalImage;
    List<String> animals;
    int posAnimals;


    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_activity);
        initialision();
        Button scrollAnimalsRight = findViewById(R.id.scrollAnimalsRight);
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

        List<String> categories = setCategories();
        categories.add(firstCategory);
        ArrayAdapter adapter = new ArrayAdapter(NewActivity.this, android.R.layout.simple_list_item_1, categories);
        categorySelector.setAdapter(adapter);
        categorySelector.setOnItemSelectedListener(
                new AdapterView.OnItemSelectedListener() {
                    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {

                        if(parent.getItemAtPosition(pos) == "New Category"){
                            Intent intent = new Intent().setClass(getApplicationContext(), NewCategory.class);
                            startActivity(intent);
                        }
                        final String[] category_id_select = new String[1];
                        String user_id = user.getUid();
                        DatabaseReference databaseReferenceChild = FirebaseDatabase.getInstance().getReference().child("Category");
                        String category_name_selected = (String) categorySelector.getSelectedItem();
                        databaseReferenceChild.orderByChild("user_id").equalTo(user_id).addListenerForSingleValueEvent(new ValueEventListener() {

                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                    String user_id_verify = snapshot.child("user_id").getValue(String.class);
                                    String category_name = snapshot.child("category_name").getValue(String.class);
                                    if(user_id.equals(user_id_verify) && category_name.equals(category_name_selected)){
                                        String category_id_verify = snapshot.child("category_id").getValue(String.class);
                                        category_id_select[0] = category_id_verify;
                                    }
                                    category_id = category_id_select[0];

                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                                // Gérez l'erreur en cas d'annulation de la requête
                            }
                        });
                        categorySelector.setSelection(pos);

                    }
                    public void onNothingSelected(AdapterView<?> parent) {
                    }
                });



        validationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText text = findViewById(R.id.activityName);
                EditText textAnimal = findViewById(R.id.animalName);
                name = text.getText().toString();
                animalName = textAnimal.getText().toString();

                if(name.trim().isEmpty() || animalName.trim().isEmpty() || (weeklyGoal.getMinute() ==0 && weeklyGoal.getHour() == 0 ) || categorySelector.getSelectedItem() == null){
                    Toast.makeText(getApplicationContext(),"Field can't be empty!",Toast.LENGTH_LONG).show();
                }
                else {
                    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

                    DatabaseReference newChildRef = databaseReference.push();
                    String activity_id = newChildRef.getKey();
                    HashMap<Object, String> hashMap = new HashMap<>();

                    hashMap.put("activity_id", activity_id);
                    hashMap.put("activity_name",name);
                    hashMap.put("activity_pet_name", animalName);
                    hashMap.put("activity_pet", animals.get(posAnimals));
                    hashMap.put("weekly_goal", String.valueOf(weeklyGoal.getHour()*60 + weeklyGoal.getMinute()));
                    hashMap.put("spent_time", "0");
                    hashMap.put("category_id", category_id);
                    hashMap.put("user_id", user.getUid());

                    FirebaseDatabase database = FirebaseDatabase.getInstance();

                    DatabaseReference reference = database.getReference("Activity");
                    reference.child(activity_id).setValue(hashMap);
                }
            }
        });
    }



    void initialision(){
        firebaseAuth = FirebaseAuth.getInstance();
        categorySelector = findViewById(R.id.categoryName);
        weeklyGoal = findViewById(R.id.weeklyGoal);
        weeklyGoal.setIs24HourView(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            weeklyGoal.setHour(0);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            weeklyGoal.setMinute(0);
        }
        user = firebaseAuth.getCurrentUser();
        validationButton = findViewById(R.id.validationButton);
        animalImage = findViewById(R.id.animalImage);
        setAnimals();
    }


    private void setAnimals() {
        posAnimals = 0;
        animals = new ArrayList<>(Arrays.asList("sheep", "cat", "chick", "giraffe", "cow", "koa", "lion", "rabbit", "tiger", "tl"));

    }

    List<String> setCategories(){
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

