package com.example.hobbyzooapp.Activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hobbyzooapp.HomeActivity;
import com.example.hobbyzooapp.R;
import com.example.hobbyzooapp.TodoTask;
import com.example.hobbyzooapp.Sessions.ListSessionsAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ActivityPage extends AppCompatActivity {

    ImageView petPic;
    TextView petName;
    Button editNamePetButton;
    EditText editTextPetName;
    Button validatePetName;
    Button showMoreButton;
    Button showLessButton;
    RecyclerView recyclerView;
    RecyclerView recyclerViewTodoList;
    ImageButton homeButton;
    TextView goalsText;
    List<String> items = new ArrayList<>();
    ListSessionsAdapter adapter;
    private List<TodoTask> todoList = new ArrayList<>();
    Boolean allSessions = false;
    Button addToTodoListButton;
    EditText addToTodoListText;
    Button validateToTodoListButton;
    FirebaseAuth firebaseAuth;
    TextView activityNameDisplay;

    String activityName ;
    String activityPetName ;
    String activityPet ;
    String weeklyGoal;
    String spentTime;
    String category_id;
    LinearLayout header;

    FirebaseDatabase database;
    DatabaseReference referenceActivity;

    public void getActivityData(String activity_id){
         database = FirebaseDatabase.getInstance();
         referenceActivity = database.getReference("Activity");



        referenceActivity.child(activity_id).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // Récupérez les informations de l'activité
                     activityName = dataSnapshot.child("activity_name").getValue(String.class).replace(",", " ");
                     activityPetName = dataSnapshot.child("activity_pet_name").getValue(String.class).replace(",", " ");
                     activityPet = dataSnapshot.child("activity_pet").getValue(String.class);
                     weeklyGoal = dataSnapshot.child("weekly_goal").getValue(String.class);
                     spentTime = dataSnapshot.child("spent_time").getValue(String.class);
                     category_id = dataSnapshot.child("category_id").getValue(String.class);

                    petName.setText(activityPetName);
                    String resourceName = activityPet+"_icon";
                    int resId = ActivityPage.this.getResources().getIdentifier(resourceName,"drawable",ActivityPage.this.getPackageName());
                    petPic.setImageResource(resId);
                    activityNameDisplay.setText(activityName);



                    DatabaseReference referenceCategory = database.getReference("Category");

                    referenceCategory.child(category_id).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()) {
                                String category_color = dataSnapshot.child("category_color").getValue(String.class);
                                int color = Color.parseColor(category_color);
                                header.setBackgroundColor(color);


                            } else {
                                // L'activité n'existe pas dans la base de données
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            // Gérez les erreurs de la récupération des données
                        }
                    });



                } else {
                    // L'activité n'existe pas dans la base de données
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Gérez les erreurs de la récupération des données
            }
        });

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_page);
        Intent intent = getIntent();
        String activity_id = intent.getStringExtra("activity_id");
        petPic =findViewById(R.id.activityPagePetPic);
        petName = findViewById(R.id.activityPagePetName);
        activityNameDisplay =findViewById(R.id.activityPageActivityName);
        header = findViewById(R.id.headerLayout);

        getActivityData(activity_id);

        firebaseAuth = FirebaseAuth.getInstance();

        items.addAll(List.of("5 juin à 13h00 - 15 min","7 juin à 13h00 - 15 min","13 juin à 13h00 - 15 min",
                "5 juin à 13h00 - 15 min","5 juin à 13h00 - 15 min","5 juin à 13h00 - 15 min",
                "5 juin à 13h00 - 15 min","7 juin à 13h00 - 15 min","16 juillet à 13h00 - 15 min",
                "5 juin à 13h00 - 15 min","5 juin à 13h00 - 15 min","5 juin à 13h00 - 15 min"));

        petPic = findViewById(R.id.activityPagePetPic);
        petName = findViewById(R.id.activityPagePetName);
        petName.setText("Coco");
        petPic.setImageResource(R.drawable.koala_icon);
        showMoreButton = findViewById(R.id.activityPageShowMoreButton);
        showLessButton = findViewById(R.id.activityPageShowLessButton);
        editNamePetButton = findViewById(R.id.activityPageEditPetNameButton);
        goalsText = findViewById(R.id.activityPageGoalsText);
        homeButton = findViewById(R.id.homeButton);
        goalsText.setText("Goal: 2h/5h");
        recyclerView = findViewById(R.id.activityPageRecyclerView);
        recyclerViewTodoList = findViewById(R.id.todoRecyclerView);
        GridLayoutManager layoutManager;
        addToTodoListButton = findViewById(R.id.addToTodoListButton);
        validateToTodoListButton = findViewById(R.id.validateToTodoListButton);
        addToTodoListText = findViewById(R.id.addToTodoListText);

        changeManager();

        adapter = new ListSessionsAdapter(items);
        recyclerView.setAdapter(adapter);
        FirebaseUser user = firebaseAuth.getCurrentUser();

        database = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = database.getReference("Tasks");
        String thisActivityId = activity_id;

        HashMap<String, String> tasks = new HashMap<>();
        TodoAdapter adapterTodoList = new TodoAdapter(todoList);
        recyclerViewTodoList.setLayoutManager(new GridLayoutManager(this, 5, GridLayoutManager.HORIZONTAL, false));
        recyclerViewTodoList.setAdapter(adapterTodoList);

        databaseReference.addValueEventListener(new ValueEventListener(){
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                todoList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    String activityId = snapshot.child("activityId").getValue(String.class);
                    String taskName  = snapshot.child("taskName").getValue(String.class);
                    String taskStatus = snapshot.child("taskStatus").getValue(String.class);
                    if (activityId.equals(thisActivityId)){
                        boolean boolTaskStatus = Boolean.parseBoolean(taskStatus);
                        todoList.add(new TodoTask(taskName, boolTaskStatus));
                    }
                }
                adapterTodoList.notifyDataSetChanged();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

        addToTodoListButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                adapter.notifyDataSetChanged();
                addToTodoListButton.setVisibility(View.GONE);
                addToTodoListText.setVisibility(View.VISIBLE);
                validateToTodoListButton.setVisibility(View.VISIBLE);
            }
        });

        validateToTodoListButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String newElement = String.valueOf(addToTodoListText.getText());
                if(newElement.trim().isEmpty()){
                    Toast.makeText(ActivityPage.this, "Nothing written!", Toast.LENGTH_LONG).show();
                }else{
                    todoList.add(new TodoTask(newElement, Boolean.FALSE));
                    DatabaseReference newChildRef = databaseReference.push();
                    String taskId = newChildRef.getKey();
                    tasks.put("taskId", taskId);
                    tasks.put("taskName", newElement);
                    tasks.put("taskStatus", "FALSE");
                    tasks.put("activityId", thisActivityId);

                    FirebaseDatabase database = FirebaseDatabase.getInstance();
                    DatabaseReference reference = database.getReference("Tasks");
                    reference.child(taskId).setValue(tasks);
                    adapterTodoList.notifyDataSetChanged();
                }
                addToTodoListButton.setVisibility(View.VISIBLE);
                addToTodoListText.setText("");
                addToTodoListText.setVisibility(View.GONE);
                validateToTodoListButton.setVisibility(View.GONE);
            }
        });

        editTextPetName = findViewById(R.id.activityPagePetNameEdit);
        InputFilter[] filters = new InputFilter[1];
        filters[0] = new InputFilter.LengthFilter(10);
        editTextPetName.setFilters(filters);

        validatePetName = findViewById(R.id.activityPagecheckPetNameButton);
        //Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.koa);
        /*
        int newWidth = (int) (bitmap.getWidth() * (70 / 100.0));
        int newHeight = (int) (bitmap.getHeight() * (70 / 100.0));
        Bitmap resizedBitmap = Bitmap.createScaledBitmap(bitmap, newWidth, newHeight, false);
        petPic.setScaleType(ImageView.ScaleType.CENTER_CROP);*/

       // petPic.setImageBitmap(bitmap);

        editNamePetButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                editTextPetName.setText(petName.getText());
                editTextPetName.setVisibility(View.VISIBLE);
                editNamePetButton.setVisibility(View.GONE);
                validatePetName.setVisibility(View.VISIBLE);
                petName.setVisibility(View.GONE);
            }
        });

        homeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openMainActivity();

            }
        });

        showMoreButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                allSessions =true;
                changeManager();
                adapter.setExpanded(!adapter.isExpanded());
                adapter.notifyDataSetChanged();
                showLessButton.setVisibility(View.VISIBLE);
                showMoreButton.setVisibility(View.GONE);

            }
        });

        showLessButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                allSessions=false;
                changeManager();

                adapter.setExpanded(!adapter.isExpanded());
                adapter.notifyDataSetChanged();
                showMoreButton.setVisibility(View.VISIBLE);
                showLessButton.setVisibility(View.GONE);
            }
        });

        validatePetName.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                editTextPetName.setVisibility(View.GONE);
                String newPetName = String.valueOf(editTextPetName.getText());;
                petName.setText(newPetName);
                editNamePetButton.setVisibility(View.VISIBLE);
                validatePetName.setVisibility(View.GONE);
                petName.setVisibility(View.VISIBLE);
            }
        });

//        adapterTodoList.setOnCheckedChangeListener(new TodoAdapter.OnCheckedChangeListener() { //todo
//            @Override
//            public void onCheckedChanged(int position, boolean isChecked) {
//                // Le code ici sera exécuté chaque fois que l'utilisateur cochera ou décochera une case
//                // Vous pouvez utiliser la position pour identifier la tâche spécifique dans la liste
//                // et utiliser isChecked pour obtenir l'état de cochage actuel
//
//                // Exemple : afficher l'état de cochage dans la console
//                System.out.println("Tâche à la position " + position + " cochée : " + isChecked);
//            }
//        });
//
    }

    private void changeManager() {
        GridLayoutManager layoutManager;
        if (allSessions)
            layoutManager = new GridLayoutManager(this, 5, GridLayoutManager.HORIZONTAL, false);
        else
            layoutManager = new GridLayoutManager(this, 2, GridLayoutManager.HORIZONTAL, false);

        recyclerView.setLayoutManager(layoutManager);
    }

    public void openMainActivity(){
        Intent intent = new Intent(this, HomeActivity.class);
        startActivity(intent);
        finish();
    }

}