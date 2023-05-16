package com.example.hobbyzooapp.Activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
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
    Button validatePetNAme;
    Button showMoreButton;
    Button showLessButton;
    RecyclerView recyclerView;
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
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_page);
        firebaseAuth = FirebaseAuth.getInstance();

        items.addAll(List.of("5 juin à 13h00 - 15 min","7 juin à 13h00 - 15 min","13 juin à 13h00 - 15 min",
                "5 juin à 13h00 - 15 min","5 juin à 13h00 - 15 min","5 juin à 13h00 - 15 min",
                "5 juin à 13h00 - 15 min","7 juin à 13h00 - 15 min","16 juillet à 13h00 - 15 min",
                "5 juin à 13h00 - 15 min","5 juin à 13h00 - 15 min","5 juin à 13h00 - 15 min"));

        petPic = findViewById(R.id.activityPagePetPic);
        petName = findViewById(R.id.activityPagePetName);
        petName.setText("Coco");
        petPic.setImageResource(R.drawable.koa);
        showMoreButton = findViewById(R.id.activityPageShowMoreButton);
        showLessButton = findViewById(R.id.activityPageShowLessButton);
        editNamePetButton = findViewById(R.id.activityPageEditPetNameButton);
        goalsText = findViewById(R.id.activityPageGoalsText);
        homeButton = findViewById(R.id.homeButton);
        goalsText.setText("Goal: 2h/5h");
        recyclerView = findViewById(R.id.activityPageRecyclerView);
        GridLayoutManager layoutManager;
        addToTodoListButton = findViewById(R.id.addToTodoListButton);
        validateToTodoListButton = findViewById(R.id.validateToTodoListButton);
        addToTodoListText = findViewById(R.id.addToTodoListText);

        changeManager();

        adapter = new ListSessionsAdapter(items);
        recyclerView.setAdapter(adapter);
        user = firebaseAuth.getCurrentUser();

        //todo base de donnees
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("Tasks");
        String thisActivityId = "actID"; //todo recuperer id activity de cette activite

        databaseReference.addValueEventListener(new ValueEventListener(){ //todo
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    String activityId = snapshot.child("activityId").getValue(String.class);
                    String taskName  = snapshot.child("taskName").getValue(String.class);
                    String taskStatus = snapshot.child("taskStatus").getValue(String.class);
                    if (activityId.equals(thisActivityId)){
                        if (taskStatus.equals("FALSE")){
                            todoList.add(new TodoTask(taskName, Boolean.FALSE));
                        }else{
                            todoList.add(new TodoTask(taskName, Boolean.TRUE));
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        //todo creation a la main a effacer
        /*todoList.add(new TodoTask("Manger", Boolean.FALSE));
        todoList.add(new TodoTask("Dormir", Boolean.TRUE));
        todoList.add(new TodoTask("Voyager", Boolean.TRUE));*/

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

                    //todo base de donnees
                    HashMap<String, String> tasks = new HashMap<>();
                    String taskId = thisActivityId+newElement; //todo choix arbitraire
                    tasks.put("taskId", taskId);
                    tasks.put("taskName", newElement);
                    tasks.put("taskStatus", "FALSE");
                    tasks.put("activityId", thisActivityId);

                    FirebaseDatabase database = FirebaseDatabase.getInstance();
                    DatabaseReference reference = database.getReference("Tasks");
                    reference.child(taskId).setValue(tasks);
                }
                addToTodoListButton.setVisibility(View.VISIBLE);
                addToTodoListText.setText("");
                addToTodoListText.setVisibility(View.GONE);
                validateToTodoListButton.setVisibility(View.GONE);
            }
        });

        RecyclerView recyclerView = findViewById(R.id.todoRecyclerView);
        TodoAdapter adapterTodoList = new TodoAdapter(todoList);
        recyclerView.setLayoutManager(new GridLayoutManager(this, todoList.size(), GridLayoutManager.HORIZONTAL, false));
        recyclerView.setAdapter(adapterTodoList);

        editTextPetName = findViewById(R.id.activityPagePetNameEdit);
        InputFilter[] filters = new InputFilter[1];
        filters[0] = new InputFilter.LengthFilter(10);
        editTextPetName.setFilters(filters);

        validatePetNAme = findViewById(R.id.activityPagecheckPetNameButton);
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.koa);
        /*
        int newWidth = (int) (bitmap.getWidth() * (70 / 100.0));
        int newHeight = (int) (bitmap.getHeight() * (70 / 100.0));
        Bitmap resizedBitmap = Bitmap.createScaledBitmap(bitmap, newWidth, newHeight, false);
        petPic.setScaleType(ImageView.ScaleType.CENTER_CROP);*/

        petPic.setImageBitmap(bitmap);

        editNamePetButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                editTextPetName.setText(petName.getText());
                editTextPetName.setVisibility(View.VISIBLE);
                editNamePetButton.setVisibility(View.GONE);
                validatePetNAme.setVisibility(View.VISIBLE);
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

        validatePetNAme.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                editTextPetName.setVisibility(View.GONE);
                String newPetName = String.valueOf(editTextPetName.getText());;
                petName.setText(newPetName);
                editNamePetButton.setVisibility(View.VISIBLE);
                validatePetNAme.setVisibility(View.GONE);
                petName.setVisibility(View.VISIBLE);
            }
        });
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