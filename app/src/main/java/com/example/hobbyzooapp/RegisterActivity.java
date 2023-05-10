package com.example.hobbyzooapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Patterns;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;


public class RegisterActivity extends AppCompatActivity  {

    EditText emailEt, passwordEt, pseudoET;
    Button registerBtn;
    TextView haveAccountTv;
    ImageView photoV;

    ProgressDialog progressDialog;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Create account");

        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);



        emailEt = findViewById(R.id.emailEt);
        passwordEt = findViewById(R.id.passwordEt);
        pseudoET = findViewById(R.id.pseudoET);
        registerBtn = findViewById(R.id.register_btn);
        haveAccountTv = findViewById(R.id.have_accountTv);
        photoV = findViewById(R.id.photoIV);

        auth =FirebaseAuth.getInstance();

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Registering User...");

        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = emailEt.getText().toString().trim();
                String password = passwordEt.getText().toString().trim();
                String pseudo = pseudoET.getText().toString().trim();

                if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                    emailEt.setError("Invalid email address");
                    emailEt.setFocusable(true);
                }
                else if(password.length()<8){
                    passwordEt.setError("password must have at least 8 characters");
                    passwordEt.setFocusable(true);
                }
                else{
                   registerUser(email, password,pseudo,photoV);
                }
            }
        });

        haveAccountTv.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                finish();
            }
        });
    }



    private void registerUser(String email, String password, String pseudo, Uri imageUri) {
        progressDialog.show();
        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, dismiss dialog and start register activity
                            progressDialog.dismiss();
                            FirebaseUser user = auth.getCurrentUser();

                            //get user uid and email from auth
                            String email = user.getEmail();
                            String uid = user.getUid();
                            String pseudo = user.getDisplayName();

                            //when user is registered, store user info in firebase realtime database
                            HashMap<String, Object> hashMap = new HashMap<>();

                            hashMap.put("email", email);
                            hashMap.put("uid", uid);
                            hashMap.put("pseudo", pseudo);

                            // upload profile image to Firebase Storage
                            if (imageUri != null) {
                                StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("users/" + uid + "/profile.jpg");

                                storageReference.putFile(imageUri)
                                        .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                            @Override
                                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                                // get download url of uploaded image
                                                Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                                                while (!uriTask.isSuccessful()) ;
                                                Uri downloadUri = uriTask.getResult();

                                                if (downloadUri != null) {
                                                    // update user profile with profile image url
                                                    UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                                            .setPhotoUri(downloadUri)
                                                            .build();
                                                    user.updateProfile(profileUpdates);
                                                    hashMap.put("image", downloadUri.toString());
                                                }
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                // handle image upload failure
                                            }
                                        });
                            }

                            FirebaseDatabase database = FirebaseDatabase.getInstance();

                            DatabaseReference reference = database.getReference("Users");
                            reference.child(uid).setValue(hashMap);

                            Toast.makeText(RegisterActivity.this, "Registered...\n" + user.getEmail(), Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(RegisterActivity.this, HomeActivity.class ));
                            finish();
                        } else {
                            // If sign in fails, display a message to the user.
                            progressDialog.dismiss();
                            Toast.makeText(RegisterActivity.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                        }

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        Toast.makeText(RegisterActivity.this,""+e.getMessage(),Toast.LENGTH_SHORT).show();
                    }
                });
    }




    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
            int id = item.getItemId();
            if(id == android.R.id.home){
                this.finish();
            }

        return super.onOptionsItemSelected(item);
    }
}