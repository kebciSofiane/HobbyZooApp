package com.example.hobbyzooapp;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ProfileActivity extends AppCompatActivity {

    //todo - just started
    FirebaseAuth firebaseAuth;

    TextView profileTv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Profile");

        firebaseAuth = FirebaseAuth.getInstance();
        profileTv = findViewById(R.id.profileTv);
    }

    private void checkUserStatus(){
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if(user != null){
            profileTv.setText(user.getEmail());
        }
        else{
            startActivity(new Intent(ProfileActivity.this, RegistrationOrConnexion.class));
            finish();
        }
    }

    @Override
    protected void onStart(){
        checkUserStatus();
        super.onStart();
    }

//
//    @Override
//    public boolean onCreateOptionMenu(Menu menu)
}
