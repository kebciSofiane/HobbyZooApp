package com.example.hobbyzooapp.AccountManagement;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Patterns;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.hobbyzooapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {

    EditText emailEt, passwordEt, pseudoET;
    Button registerBtn;
    TextView haveAccountTv;
    ImageView profileIv;
    ProgressDialog progressDialog;
    private FirebaseAuth auth;
    private FirebaseStorage storage;
    private StorageReference storageReference;
    private DatabaseReference databaseReference;
    private Uri imageUri;

    private static final int PICK_IMAGE = 1;
    private static final int USERNAME_MAX_LENGTH = 15;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        emailEt = findViewById(R.id.emailEt);
        passwordEt = findViewById(R.id.passwordEt);
        registerBtn = findViewById(R.id.register_btn);
        haveAccountTv = findViewById(R.id.have_accountTv);
        pseudoET = findViewById(R.id.pseudoET);
        profileIv = findViewById(R.id.photoIV);

        auth = FirebaseAuth.getInstance();
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        databaseReference = FirebaseDatabase.getInstance().getReference();
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Registering User...");

        profileIv.setOnClickListener(v -> {
            Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
            galleryIntent.setType("image/*");
            startActivityForResult(galleryIntent, PICK_IMAGE);
        });

        registerBtn.setOnClickListener(v -> {
            String email = emailEt.getText().toString().trim();
            String password = passwordEt.getText().toString().trim();
            String pseudo = pseudoET.getText().toString().trim();

            if (pseudo.length() > USERNAME_MAX_LENGTH) {
                pseudoET.setError("Username is too long. Maximum length is " + USERNAME_MAX_LENGTH + " characters.");
                pseudoET.requestFocus();
                return;
            }

            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                emailEt.setError("Invalid email address");
                emailEt.setFocusable(true);
            } else if (password.length() < 8) {
                passwordEt.setError("password must have at least 8 characters");
                passwordEt.setFocusable(true);
            } else {
                registerUser(email, password, pseudo, imageUri);
            }
        });

        haveAccountTv.setOnClickListener(v -> {
            startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
            finish();
        });
    }

    private void registerUser(String email, String password, String pseudo, Uri imageUri) {
        progressDialog.setMessage("Registering User...");
        progressDialog.show();

        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = auth.getCurrentUser();
                        if (user != null) {
                            user.sendEmailVerification().addOnCompleteListener(emailVerificationTask -> {
                                progressDialog.dismiss();

                                if (emailVerificationTask.isSuccessful()) {
                                    LocalDate currentDate = null;
                                    LocalDate nextMonday = null;
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                        currentDate = LocalDate.now();
                                        nextMonday = currentDate.with(DayOfWeek.MONDAY);
                                        if (currentDate.compareTo(nextMonday) > 0) {
                                            nextMonday = nextMonday.plusWeeks(1);
                                        }
                                    }
                                    String email1 = user.getEmail();
                                    String uid = user.getUid();

                                    String pseudo1 = pseudoET.getText().toString().trim();

                                    if (imageUri == null) {
                                        HashMap<String, Object> userMap = new HashMap<>();
                                        userMap.put("email", email1);
                                        userMap.put("uid", uid);
                                        userMap.put("pseudo", pseudo1);
                                        userMap.put("emailVerified", false);
                                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                            userMap.put("connectNextMondayDay", nextMonday.getDayOfMonth());
                                            userMap.put("connectNextMondayMonth",  Integer.parseInt(String.valueOf(nextMonday.getMonth().getValue())));
                                            userMap.put("connectNextMondayYear", nextMonday.getYear());
                                        }

                                        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
                                        reference.child(uid).setValue(userMap).addOnCompleteListener(task1 -> {
                                            if (task1.isSuccessful()) {
                                                Toast.makeText(RegisterActivity.this, "Registered...\nA verification email has been sent to " + email1, Toast.LENGTH_LONG).show();
                                                FirebaseAuth.getInstance().signOut();
                                                startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                                                finish();
                                            } else {
                                                Toast.makeText(RegisterActivity.this, "Failed to register. Please try again.", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                    } else {
                                        StorageReference storageRef = FirebaseStorage.getInstance().getReference().child("users/" + uid + "/profile.jpg");

                                        LocalDate finalNextMonday = nextMonday;
                                        storageRef.putFile(imageUri)
                                                .addOnSuccessListener(taskSnapshot -> storageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                                                    String imageUrl = uri.toString();
                                                    HashMap<String, Object> userMap = new HashMap<>();
                                                    userMap.put("email", email1);
                                                    userMap.put("uid", uid);
                                                    userMap.put("pseudo", pseudo1);
                                                    userMap.put("image", imageUrl);
                                                    userMap.put("emailVerified", false);
                                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                                        userMap.put("connectNextMondayDay", finalNextMonday.getDayOfMonth());
                                                        userMap.put("connectNextMondayMonth", Integer.parseInt(String.valueOf(finalNextMonday.getMonth().getValue())));
                                                        userMap.put("connectNextMondayYear", finalNextMonday.getYear());
                                                    }
                                                    DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
                                                    reference.child(uid).setValue(userMap).addOnCompleteListener(task1 -> {
                                                        if (task1.isSuccessful()) {
                                                            Toast.makeText(RegisterActivity.this, "Registered...\nA verification email has been sent to " + email1, Toast.LENGTH_LONG).show();
                                                            FirebaseAuth.getInstance().signOut();
                                                            startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                                                            finish();
                                                        } else {
                                                            Toast.makeText(RegisterActivity.this, "Failed to register. Please try again.", Toast.LENGTH_SHORT).show();
                                                        }
                                                    });
                                                }))
                                                .addOnFailureListener(e -> {
                                                    progressDialog.dismiss();
                                                    Toast.makeText(RegisterActivity.this, "Failed to upload image", Toast.LENGTH_SHORT).show();
                                                });
                                    }
                                } else {
                                    Toast.makeText(RegisterActivity.this, "Failed to send verification email. Please try again.", Toast.LENGTH_SHORT).show();
                                }
                            });
                        } else {
                            progressDialog.dismiss();
                            Toast.makeText(RegisterActivity.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        progressDialog.dismiss();
                        Toast.makeText(RegisterActivity.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(e -> {
                    progressDialog.dismiss();
                    Toast.makeText(RegisterActivity.this, "Registration failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            profileIv.setImageURI(imageUri);
            uploadImageToFirebase();
        }
    }

    private void uploadImageToFirebase() {
        FirebaseUser user = auth.getCurrentUser();
        if (user != null) {
            StorageReference profileRef = storageReference.child("users/" + user.getUid() + "/profile.jpg");
            profileRef.putFile(imageUri)
                    .addOnSuccessListener(taskSnapshot -> profileRef.getDownloadUrl().addOnSuccessListener(uri -> {
                        String imageUrl = uri.toString();
                        databaseReference.child("Users").child(user.getUid()).child("image").setValue(imageUrl);
                        Toast.makeText(RegisterActivity.this, "Image uploaded successfully", Toast.LENGTH_SHORT).show();
                    }))
                    .addOnFailureListener(e -> {
                        progressDialog.dismiss();
                        Toast.makeText(RegisterActivity.this, "Failed to upload image", Toast.LENGTH_SHORT).show();
                    });
        }
    }

    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            this.finish();
        }
        return super.onOptionsItemSelected(item);
    }

}