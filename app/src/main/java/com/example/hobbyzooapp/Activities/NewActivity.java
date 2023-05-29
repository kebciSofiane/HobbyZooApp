package com.example.hobbyzooapp.Activities;

import android.annotation.SuppressLint;
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
import com.example.hobbyzooapp.HomeActivity;
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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NewActivity extends AppCompatActivity {

    String activity_name, animalName, category_id;
    FirebaseAuth firebaseAuth;
    Spinner categorySelector;
    FirebaseUser user;
    TimePicker weeklyGoal;
    ImageView animalImage, validationButton, returnButton;
    List<String> animals, categories;
    int posAnimals;
    String regexPattern = "^[a-zA-Z0-9 ]+$";
    Pattern pattern;
    EditText editActivityName, editPetName;


    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_activity);
        initialisation();
        intentInitialisation();
        Button scrollAnimalsRight = findViewById(R.id.scrollAnimalsRight);
        @SuppressLint({"MissingInflatedId", "LocalSuppress"}) Button scrollAnimalsLeft = findViewById(R.id.scrollAnimalsLeft);
        scrollAnimalsRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(posAnimals < animals.size()-1)
                    posAnimals++;
                else
                    posAnimals = 0;
                int animalId = getResources().getIdentifier(animals.get(posAnimals)+"_whole_neutral", "drawable", getPackageName());
                animalImage.setImageResource(animalId);
                animalImage.invalidate();
            }
        });

        scrollAnimalsLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(posAnimals > 0)
                    posAnimals--;
                else
                    posAnimals = animals.size()-1;
                int animalId = getResources().getIdentifier(animals.get(posAnimals)+"_whole_neutral", "drawable", getPackageName());
                animalImage.setImageResource(animalId);
                animalImage.invalidate();
            }
        });

        categories = setCategories();
        categories.add("");
        ArrayAdapter<String> adapter = new ArrayAdapter<>(NewActivity.this, android.R.layout.simple_list_item_1, categories);
        categorySelector.setAdapter(adapter);
        categorySelector.setOnItemSelectedListener(
                new AdapterView.OnItemSelectedListener() {
                    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {

                        if(parent.getItemAtPosition(pos) == "New Category"){
                            Intent intent = new Intent().setClass(getApplicationContext(), NewCategory.class);
                            intent.putExtra("activity_name", editActivityName.getText());
                            intent.putExtra("animal_name", editPetName.getText());
                            intent.putExtra("pos_animal", posAnimals);
                            startActivity(intent);
                            finish();
                        }
                        final String[] category_id_select = new String[1];
                        String user_id = user.getUid();
                        DatabaseReference databaseReferenceChild = FirebaseDatabase.getInstance().getReference().child("Category");
                        String category_name_selected = (String) categorySelector.getSelectedItem();
                        databaseReferenceChild.orderByChild("user_id").equalTo(user_id).addListenerForSingleValueEvent(new ValueEventListener() {

                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
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

        returnButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        validationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                activity_name = editActivityName.getText().toString();
                animalName = editPetName.getText().toString();
                Matcher matcherActivityName = pattern.matcher(activity_name);
                Matcher matcherAnimalName = pattern.matcher(animalName);

                if(activity_name.trim().isEmpty() || activity_name == null|| animalName.trim().isEmpty() || (weeklyGoal.getMinute() ==0 && weeklyGoal.getHour() == 0 ) || categorySelector.getSelectedItem() == null){
                    Toast.makeText(getApplicationContext(),"Field can't be empty!",Toast.LENGTH_LONG).show();
                }
                else if(!matcherAnimalName.matches() || !matcherActivityName.matches()){
                    Toast.makeText(getApplicationContext(),"Name fields can't have special characters!",Toast.LENGTH_LONG).show();
                }
                else {
                    List<String> activities = new ArrayList<>();

                    DatabaseReference databaseReferenceChild = FirebaseDatabase.getInstance().getReference().child("Activity");
                    databaseReferenceChild.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                String userId = snapshot.child("user_id").getValue(String.class);
                                String nameActivity = snapshot.child("activity_name").getValue(String.class).replace(",", " ");
                                if(userId.equals(user.getUid()) && nameActivity.equals(activity_name))
                                    activities.add(activity_name);
                            }
                            if(activities.size() == 0){
                                addBDActivity();
                                Intent intent = new Intent().setClass(getApplicationContext(), HomeActivity.class);
                                startActivity(intent);
                                finish();
                            }
                            else{
                                Toast.makeText(getApplicationContext(),"This Activity already exists!",Toast.LENGTH_LONG).show();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            // Gérez l'erreur en cas d'annulation de la requête
                        }
                    });


                }
            }
        });
    }

    private void intentInitialisation() {
        Intent intentCategory = getIntent();
        if(intentCategory.hasExtra(activity_name)){
            editActivityName.setText(intentCategory.getStringExtra("activity_name"));
            editPetName.setText(intentCategory.getStringExtra("animal_name"));
            categorySelector.setSelection(categories.size()-2);
            posAnimals = Integer.parseInt(intentCategory.getStringExtra("pos_animal"));
        }

    }


    void initialisation(){
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
        returnButton = findViewById(R.id.returnButton);
        animalImage = findViewById(R.id.animalImage);
        setAnimals();
        pattern = Pattern.compile(regexPattern);
        editActivityName = findViewById(R.id.activityName);
        editPetName = findViewById(R.id.animalName);
        categories = setCategories();
        categories.add("");
    }

    private void addBDActivity(){

            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

            DatabaseReference newChildRef = databaseReference.push();
            String activity_id = newChildRef.getKey();
            HashMap<Object, String> hashMap = new HashMap<>();

            hashMap.put("activity_id", activity_id);
            hashMap.put("activity_name", activity_name.replace(" ", ","));
            hashMap.put("activity_pet_name", animalName.replace(" ", ","));
            hashMap.put("activity_pet", animals.get(posAnimals));
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                hashMap.put("weekly_goal", String.valueOf(weeklyGoal.getHour()*60 + weeklyGoal.getMinute()));
            }
            hashMap.put("spent_time", "0");
            hashMap.put("feeling", "3");
            hashMap.put("category_id", category_id);
            hashMap.put("user_id", user.getUid());

            FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference reference = database.getReference("Activity");
            reference.child(activity_id).setValue(hashMap);
    }

    private void setAnimals() {
        posAnimals = 0;
        animals = new ArrayList<>(Arrays.asList("sheep", "koala", "cat", "dog", "chick", "chicken", "giraffe", "lion", "rabbit", "tiger", "deer", "bear", "beaver", "cow", "monkey", "panda", "pig", "raccoon", "squirrel"));
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
                        categories.add(snapshot.child("category_name").getValue(String.class).replace(",", " "));
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

