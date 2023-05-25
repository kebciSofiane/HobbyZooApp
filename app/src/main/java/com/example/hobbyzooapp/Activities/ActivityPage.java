package com.example.hobbyzooapp.Activities;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.InputFilter;
import android.util.Log;
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
import com.example.hobbyzooapp.Sessions.OnSessionListRetrievedListener;
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
    EditText editTextActivityName;
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
    private List<String> mysessions = new ArrayList<>();
    private List<String> lastSessionData = new ArrayList<>();

    Boolean allSessions = false;
    Button addToTodoListButton;
    EditText addToTodoListText;
    Button validateToTodoListButton;
    FirebaseAuth firebaseAuth;
    TextView activityNameDisplay, sessionCommentDisplay;

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
                    int weeklyHours = Integer.parseInt(weeklyGoal) / 60;
                    int weeklyMinutes = Integer.parseInt(weeklyGoal) % 60;

                    int weeklySpentHours = Integer.parseInt(spentTime) / 60;
                    int weeklySpentMinutes = Integer.parseInt(spentTime) % 60;
                    goalsText.setText("Weekly Goal: "+weeklySpentHours+"h"+weeklySpentMinutes+"/"+weeklyHours+"h"+weeklyMinutes);


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

                    sessionCommentDisplay.setText("??");//todo

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

    private ArrayList<String> getSessionList(String activity_id, OnSessionListRetrievedListener listener){
        DatabaseReference reference = database.getReference("Session");
        ArrayList<String> mySessions = new ArrayList<>();
        reference.orderByChild("activity_id").equalTo(activity_id).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String session_id = snapshot.child("session_id").getValue(String.class);
                    String session_duration = snapshot.child("session_duration").getValue(String.class);
                    String activity_id = snapshot.child("activity_id").getValue(String.class);
                    String session_day = snapshot.child("session_day").getValue(String.class);
                    String session_month = snapshot.child("session_month").getValue(String.class);
                    String session_year = snapshot.child("session_year").getValue(String.class);
                    int hourDuration = Integer.parseInt(session_duration)/60;
                    int minutesDuration = Integer.parseInt(session_duration)%60;
                    String session = session_day+"/"+session_month+"/"+session_year+" for "+hourDuration+"h:"+minutesDuration;
                    mySessions.add(session);
                }
                listener.onSessionListRetrieved(mySessions);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w("TAG", "Erreur lors de la récupération des données", databaseError.toException());
            }
        });

        return  mySessions;
    }

    private ArrayList<String> getLastSessionPicCom(String activity_id, OnSessionListRetrievedListener listener){
        DatabaseReference reference = database.getReference("Session");
        ArrayList<String> lastSessionData = new ArrayList<>();
        reference.orderByChild("activity_id").equalTo(activity_id).orderByChild("session_done").equalTo("TRUE").addListenerForSingleValueEvent(new ValueEventListener() {
            int lastDate = 0;
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String session_day = snapshot.child("session_day").getValue(String.class);
                    String session_month = snapshot.child("session_month").getValue(String.class);
                    String session_year = snapshot.child("session_year").getValue(String.class);
                    String session_time = snapshot.child("session_time").getValue(String.class);
                    int date = Integer.parseInt(session_year + session_month + session_day + session_time);

                    String session_picture = snapshot.child("session_picture").getValue(String.class);
                    String session_comment = snapshot.child("session_comment").getValue(String.class);

                    if (lastDate < date) {
                        lastSessionData.set(0, session_picture);
                        lastSessionData.set(1, session_comment);
                        lastDate = date;
                    }
                }
                listener.onSessionListRetrieved(lastSessionData);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w("TAG", "Erreur lors de la récupération des données", databaseError.toException());
            }
        });
        return  lastSessionData;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_page);
        Intent intent = getIntent();
        String activity_id = intent.getStringExtra("activity_id");
        petPic = findViewById(R.id.activityPagePetPic);
        petName = findViewById(R.id.activityPagePetName);
        activityNameDisplay = findViewById(R.id.activityPageActivityName);
        header = findViewById(R.id.headerLayout);
        goalsText = findViewById(R.id.activityPageGoalsText);
        editNamePetButton = findViewById(R.id.activityPageEditPetNameButton);
        editTextActivityName = findViewById(R.id.activityPageActivityNameEdit);

        sessionCommentDisplay = findViewById(R.id.activityPageCommentText);

        getActivityData(activity_id);

        firebaseAuth = FirebaseAuth.getInstance();
        getSessionList(activity_id, new OnSessionListRetrievedListener() {
            @Override
            public void onSessionListRetrieved(ArrayList<String> sessionList) {
                mysessions = sessionList;
                adapter = new ListSessionsAdapter(sessionList);
                recyclerView.setAdapter(adapter);
                changeManager();

            }
        });

        /*getLastSessionPicCom(activity_id, new OnSessionListRetrievedListener() { //todo
            @Override
            public void onSessionListRetrieved(ArrayList<String> sessionPicCom) {
                lastSessionData = sessionPicCom;
                sessionCommentDisplay.setText(lastSessionData.get(1));

                adapter = new ListSessionsAdapter(sessionPicCom);
                recyclerView.setAdapter(adapter);
                changeManager();

            }
        });*/

        petPic = findViewById(R.id.activityPagePetPic);
        petName = findViewById(R.id.activityPagePetName);
        petPic.setImageResource(R.drawable.koala_icon);
        showMoreButton = findViewById(R.id.activityPageShowMoreButton);
        showLessButton = findViewById(R.id.activityPageShowLessButton);
        homeButton = findViewById(R.id.homeButton);
        recyclerView = findViewById(R.id.activityPageRecyclerView);
        recyclerViewTodoList = findViewById(R.id.todoRecyclerView);
        addToTodoListButton = findViewById(R.id.addToTodoListButton);
        validateToTodoListButton = findViewById(R.id.validateToTodoListButton);
        addToTodoListText = findViewById(R.id.addToTodoListText);

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
                editTextActivityName.setText(activityNameDisplay.getText());
                editTextPetName.setVisibility(View.VISIBLE);
                editTextActivityName.setVisibility(View.VISIBLE);
                editNamePetButton.setVisibility(View.GONE);
                validatePetName.setVisibility(View.VISIBLE);
                petName.setVisibility(View.GONE);
                activityNameDisplay.setVisibility(View.GONE);
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
                editTextActivityName.setVisibility(View.GONE);
                String newPetName = String.valueOf(editTextPetName.getText());;
                String newActivityName = String.valueOf(editTextActivityName.getText());;
                petName.setText(newPetName);
                activityNameDisplay.setText(newActivityName);
                editNamePetButton.setVisibility(View.VISIBLE);
                validatePetName.setVisibility(View.GONE);
                petName.setVisibility(View.VISIBLE);
                activityNameDisplay.setVisibility(View.VISIBLE);

                DatabaseReference activitiesRef = FirebaseDatabase.getInstance().getReference("Activity");
                DatabaseReference activityRef = activitiesRef.child(activity_id);
                activityRef.child("activity_pet_name").setValue(newPetName, new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                        if (databaseError == null) {
                            System.out.println("Activité modifiée avec succès !");
                        } else {
                            System.err.println("Erreur lors de la modification de l'activité : " + databaseError.getMessage());
                        }
                    }
                });

                activityRef.child("activity_name").setValue(newActivityName, new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                        if (databaseError == null) {
                            System.out.println("Activité modifiée avec succès !");
                        } else {
                            System.err.println("Erreur lors de la modification de l'activité : " + databaseError.getMessage());
                        }
                    }
                });
            }
        });

    }

    private void changeManager() {
        GridLayoutManager layoutManager;
         if (allSessions)
             if (mysessions.size()<=5)
                 layoutManager = new GridLayoutManager(this, mysessions.size() , GridLayoutManager.HORIZONTAL, false);
             else
                layoutManager = new GridLayoutManager(this, 5, GridLayoutManager.HORIZONTAL, false);
        else
                layoutManager = new GridLayoutManager(this, 3, GridLayoutManager.HORIZONTAL, false);

        recyclerView.setLayoutManager(layoutManager);
    }

    public void openMainActivity(){
        Intent intent = new Intent(this, HomeActivity.class);
        startActivity(intent);
        finish();
    }

}