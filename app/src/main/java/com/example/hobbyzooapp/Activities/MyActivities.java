package com.example.hobbyzooapp.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.hobbyzooapp.Category.Category;
import com.example.hobbyzooapp.HomeActivity;
import com.example.hobbyzooapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MyActivities extends AppCompatActivity {

    private ImageButton homeButton;
    private Button addActivityButton;
    ExpandableListView expandableListView;
    ExpandableListAdapter expandableListAdapter;
    List<String> expandableListTitle;
    HashMap<String, List<Activity>> expandableListDetail;

    private FirebaseAuth firebaseAuth;

    public void addActivities(){
        FirebaseUser user = firebaseAuth.getCurrentUser();
        String email = user.getEmail();
        String uid = user.getUid();
        FirebaseFirestore db = FirebaseFirestore.getInstance();


        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        DatabaseReference newChildRef = databaseReference.push();
        String category_id = newChildRef.getKey();
        String cat = category_id;
        Map<String, String> category = new HashMap<>();
        category.put("category_id", category_id);
        category.put("category_color","#00703c");
        category.put("category_name", "Muscu");
        category.put("user_id", uid);

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference reference= database.getReference("Category");
        reference.child(category_id).setValue(category);



         databaseReference = FirebaseDatabase.getInstance().getReference();
         newChildRef = databaseReference.push();
         category_id = newChildRef.getKey();
         category.clear();

        category.put("category_id", category_id);
        category.put("category_color","#00753c");
        category.put("category_name", "sport");
        category.put("user_id", uid);

        database = FirebaseDatabase.getInstance();
        reference= database.getReference("Category");
        reference.child(category_id).setValue(category);








         databaseReference = FirebaseDatabase.getInstance().getReference();
         newChildRef = databaseReference.push();
        String activity_id = newChildRef.getKey();

        CollectionReference activitiesTable = db.collection("Activities");
        Map<String, String> activity = new HashMap<>();
        activity.put("activity_id", activity_id);
        activity.put("activity_pet_name","Coco");
        activity.put("activity_pet", "koala");
        activity.put("weekly_goal", "2");
        activity.put("spent_time", "1");
        activity.put("activity_name", "fit");
        activity.put("user_id", uid);
        activity.put("category_id", category_id);

         database = FirebaseDatabase.getInstance();
         reference= database.getReference("Activity");
        reference.child(activity_id).setValue(activity);


         databaseReference = FirebaseDatabase.getInstance().getReference();
         newChildRef = databaseReference.push();
         activity_id = newChildRef.getKey();        activity.clear();

        activity.put("activity_id", activity_id);
        activity.put("activity_pet_name","Coco");
        activity.put("activity_pet", "tl");
        activity.put("activity_name", "life");
        activity.put("weekly_goal", "3");
        activity.put("spent_time", "2");
        activity.put("user_id", uid);
        activity.put("category_id", category_id);

         database = FirebaseDatabase.getInstance();
         reference= database.getReference("Activity");
        reference.child(activity_id).setValue(activity);

        databaseReference = FirebaseDatabase.getInstance().getReference();
        newChildRef = databaseReference.push();
        activity_id = newChildRef.getKey();        activity.clear();
        activity.clear();

        activity.put("activity_id", activity_id);
        activity.put("activity_pet_name","Coco");
        activity.put("activity_pet", "rabbit");
        activity.put("weekly_goal", "3");
        activity.put("spent_time", "2");
        activity.put("activity_name", "eat");
        activity.put("user_id", uid);
        activity.put("category_id", cat);

        database = FirebaseDatabase.getInstance();
        reference= database.getReference("Activity");
        reference.child(activity_id).setValue(activity);
    }


    public void getActivities(ArrayList<Activity> activities, ArrayList<Category> categories){
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference reference = database.getReference("Category");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                    String category_id = snapshot.child("category_id").getValue(String.class);
                    String category_color = snapshot.child("category_color").getValue(String.class);
                    String category_name = snapshot.child("category_name").getValue(String.class);
                    String user_id = snapshot.child("user_id").getValue(String.class);


                    FirebaseDatabase database = FirebaseDatabase.getInstance();
                    DatabaseReference reference = database.getReference("Activity");


                    reference.orderByChild("category_id").equalTo(category_id).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                //String activityId = snapshot.getKey();
                                String activity_id = snapshot.child("activity_id").getValue(String.class);
                                String activity_pet_name = snapshot.child("activity_pet_name").getValue(String.class);
                                String activity_pet = snapshot.child("activity_pet").getValue(String.class);
                                String weekly_goal = snapshot.child("weekly_goal").getValue(String.class);
                                String spent_time = snapshot.child("spent_time").getValue(String.class);
                                String activity_name = snapshot.child("activity_name").getValue(String.class);
                                String user_id = snapshot.child("user_id").getValue(String.class);
                                activities.add(new Activity(activity_name, activity_pet_name, activity_pet));

                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            Log.w("TAG", "Erreur lors de la récupération des données", databaseError.toException());
                        }
                    });
                    categories.add(new Category(category_id, category_name, category_color, activities));
                    activities.clear();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w("TAG", "Erreur lors de la récupération des données", databaseError.toException());
            }
        });

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_activities);
        firebaseAuth = FirebaseAuth.getInstance();
        Toast.makeText(getApplicationContext(), "Message temporaire", Toast.LENGTH_SHORT).show();

        //addActivities();

        homeButton = findViewById(R.id.homeButton);
        homeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openMainActivity();
            }
        });

        addActivityButton = findViewById(R.id.add_activity_button);
        addActivityButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                startActivity(new Intent(MyActivities.this, NewActivity.class));
            }
        });

        expandableListView = (ExpandableListView) findViewById(R.id.expandableListView);





        expandableListView = (ExpandableListView) findViewById(R.id.expandableListView);
        expandableListDetail = ExpandableListData.getData();
        expandableListTitle = new ArrayList<String>(expandableListDetail.keySet());
        expandableListAdapter = new ExpandableListAdapter(this, expandableListTitle, expandableListDetail);
        expandableListView.setAdapter(expandableListAdapter);


        expandableListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {


            @Override
            public boolean onChildClick(ExpandableListView parent, View v,
                                        int groupPosition, int childPosition, long id) {
                openActivityPage();
                return true;
            }
        });


        ArrayList<Activity> activities = new ArrayList<>();
        activities.add(new Activity("Dessin", "Biquette", "sheep"));
        activities.add(new Activity("poetry", "Coco", "koala"));
        activities.add(new Activity("poetry", "Coco", "koala"));

        ArrayList<Activity> activities2 = new ArrayList<>();
        activities2.add(new Activity("Muscu", "Biquette", "sheep"));

        ArrayList<Activity> activities3 = new ArrayList<>();
        activities3.add(new Activity("Patisserie", "Biquette", "sheep"));


        List<Category> category = new ArrayList<>();
        category.add(new Category("10", "Art", null, activities));
        category.add(new Category("11", "Sport", null, activities2));
        category.add(new Category("11", "Cuisine", null, activities3));


        //GridView activityListView = findViewById(R.id.category_list_view);
        //CategoryListAdapter adapter = new CategoryListAdapter(this,category);
        //adapter.setOnItemClickListener(new OnItemClickListener() {
    }


    public void openMainActivity(){
        Intent intent = new Intent(this, HomeActivity.class);
        startActivity(intent);
    }

    public void openActivityPage(){
        Intent intent = new Intent(this, ActivityPage.class);
        startActivity(intent);
    }



}