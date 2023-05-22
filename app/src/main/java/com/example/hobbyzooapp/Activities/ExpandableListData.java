package com.example.hobbyzooapp.Activities;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.example.hobbyzooapp.Category.Category;
import com.example.hobbyzooapp.HomeActivity;
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



public class ExpandableListData {


    public static HashMap<String, Category>  getActivities(ActivitiesCallBack callback) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference reference = database.getReference("Category");
        HashMap<String, Category> expandableListDetail = new HashMap<>();
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser user = firebaseAuth.getCurrentUser();
        String uid = user.getUid();
        reference.orderByChild("user_id").equalTo(uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String category_id = snapshot.child("category_id").getValue(String.class);
                    String category_color = snapshot.child("category_color").getValue(String.class);
                    String category_name = snapshot.child("category_name").getValue(String.class);

                    FirebaseDatabase database = FirebaseDatabase.getInstance();
                    DatabaseReference reference = database.getReference("Activity");

                    reference.orderByChild("category_id").equalTo(category_id).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            ArrayList<Activity> activities = new ArrayList<>();
                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                //String activityId = snapshot.getKey();
                                String activity_id = snapshot.child("activity_id").getValue(String.class);
                                String activity_pet_name = snapshot.child("activity_pet_name").getValue(String.class);
                                String activity_pet = snapshot.child("activity_pet").getValue(String.class);
                                String activity_name = snapshot.child("activity_name").getValue(String.class);
                                activities.add(new Activity(activity_id,activity_name, activity_pet_name, activity_pet));

                                expandableListDetail.put(category_name, new Category(category_id,category_name,category_color, activities));
                                callback.onActivitiesLoaded(expandableListDetail);


                            }

                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            Log.w("TAG", "Erreur lors de la récupération des données", databaseError.toException());
                        }
                    });
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w("TAG", "Erreur lors de la récupération des données", databaseError.toException());
            }
        });

        return expandableListDetail;
    }
}