package com.example.hobbyzooapp;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.hobbyzooapp.Activities.MyActivities;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;


public class ProfileActivity extends AppCompatActivity {
    private TextView usernameTextView;
    private ImageView profileImageView;
    private TextView editProfileTextView;
    private Button personalInfoButton;
    private Button myActivitiesButton;
    private Button followMyProgressButton;
    private ImageButton homeButton;
    private ImageButton settingsButton;

    FirebaseAuth firebaseAuth;
    private FirebaseStorage storage;
    private StorageReference storageReference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        firebaseAuth = FirebaseAuth.getInstance();
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        FirebaseUser user = firebaseAuth.getCurrentUser();

        usernameTextView = findViewById(R.id.profile_username);
        profileImageView = findViewById(R.id.profile_image);
        editProfileTextView = findViewById(R.id.edit_profile);
        personalInfoButton = findViewById(R.id.personal_info);
        myActivitiesButton = findViewById(R.id.my_activities);
        followMyProgressButton = findViewById(R.id.follow_my_progress);
        homeButton = findViewById(R.id.home_button);
        settingsButton = findViewById(R.id.settings_button);

        if (user != null) {
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(user.getUid());
            reference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        String pseudo = snapshot.child("pseudo").getValue(String.class);
                        if (pseudo != null) {
                            TextView pseudoTextView = findViewById(R.id.profile_username);
                            pseudoTextView.setText(pseudo);
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    // Gérer les erreurs de lecture des données
                }
            });
        }

        String imageUrl = "https://firebasestorage.googleapis.com/v0/b/hobbyzoo-de1e5.appspot.com/o/users%2F0itwJp7fxERvueCFileao9an7D62%2Fprofile.jpg?alt=media&token=8ea4e6de-ff82-402f-bbce-7b8f3e0cd548";

        ImageView profileImageView = findViewById(R.id.profile_image);
        Glide.with(this)
                .load(imageUrl)
                .placeholder(R.drawable.ic_profile) // Image de remplacement temporaire
                .error(R.drawable.ic_error) // Image d'erreur en cas de chargement échoué
                .into(profileImageView);

        myActivitiesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ProfileActivity.this, MyActivities.class));

            }
        });

        homeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ProfileActivity.this, HomeActivity.class));
                finish();

            }
        });

//        settingsButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                startActivity(new Intent(ProfileActivity.this, SettingsActivity.class));
//
//            }
//        });
    }


}