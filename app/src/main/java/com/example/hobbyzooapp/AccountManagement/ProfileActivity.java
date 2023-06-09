package com.example.hobbyzooapp.AccountManagement;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.hobbyzooapp.Activities.MyActivities;
import com.example.hobbyzooapp.CalendarEvolution.MyEvolutionActivity;
import com.example.hobbyzooapp.R;
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
    private ImageView profileImageView, addPhoto;
    private Button editProfileButton, personalInfoButton, myActivitiesButton, followMyProgressButton, settingsButton, validate;
    private ImageButton backButton;
    private EditText usernameEdit;

    private FirebaseAuth firebaseAuth;
    private FirebaseStorage storage;
    private StorageReference storageReference;
    private DatabaseReference databaseReference;
    private String userId;
    private static final int PICK_IMAGE = 1;
    private Uri imageUri;
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
        editProfileButton = findViewById(R.id.edit_profile);
        personalInfoButton = findViewById(R.id.account);
        myActivitiesButton = findViewById(R.id.my_activities);
        followMyProgressButton = findViewById(R.id.follow_my_progress);
        backButton = findViewById(R.id.backButton);
        settingsButton = findViewById(R.id.settings_button);
        usernameEdit = findViewById(R.id.username_edit);
        addPhoto = findViewById(R.id.add_photo);
        validate = findViewById(R.id.validateButton);

        databaseReference = FirebaseDatabase.getInstance().getReference("Users");
        userId = user.getUid();

        if (user != null) {
            DatabaseReference reference = databaseReference.child(userId);
            reference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        String pseudo = snapshot.child("pseudo").getValue(String.class);
                        if (pseudo != null) {
                            usernameTextView.setText(pseudo);
                            usernameEdit.setText(pseudo);
                        }

                        String image = snapshot.child("image").getValue(String.class);
                        if (image != null) {
                            Glide.with(ProfileActivity.this)
                                    .load(image)
                                    .into(profileImageView);
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.w("TAG", "Data reading error", error.toException());
                }
            });
        }

        myActivitiesButton.setOnClickListener(v -> startActivity(new Intent(ProfileActivity.this, MyActivities.class)));
        backButton.setOnClickListener(v -> finish());
        settingsButton.setOnClickListener(v -> startActivity(new Intent(ProfileActivity.this, SettingsActivity.class)));
        personalInfoButton.setOnClickListener(v -> startActivity(new Intent(ProfileActivity.this, AccountActivity.class)));
        followMyProgressButton.setOnClickListener(v -> startActivity(new Intent(ProfileActivity.this, MyEvolutionActivity.class)));
        editProfileButton.setOnClickListener(v -> {
            usernameEdit.setVisibility(View.VISIBLE);
            addPhoto.setVisibility(View.VISIBLE);
            validate.setVisibility(View.VISIBLE);
            usernameTextView.setVisibility(View.GONE);
            editProfileButton.setVisibility(View.GONE);
            backButton.setVisibility(View.GONE);

            String currentUsername = usernameTextView.getText().toString().trim();
            usernameEdit.setText(currentUsername);

            imageUri = null;
        });

        addPhoto.setOnClickListener(v -> {
            Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
            galleryIntent.setType("image/*");
            startActivityForResult(galleryIntent, PICK_IMAGE);
        });

        validate.setOnClickListener(v -> {
            String newUsername = usernameEdit.getText().toString().trim();

            boolean isUsernameChanged = !newUsername.isEmpty() && !newUsername.equals(usernameTextView.getText().toString().trim());

            if (isUsernameChanged && imageUri == null) {
                // Si seul le nom d'utilisateur a été modifié, mais aucune nouvelle image n'a été sélectionnée
                updateUserProfile(newUsername, null);
            } else if (!isUsernameChanged && imageUri != null) {
                // Si seule la nouvelle image a été sélectionnée, mais le nom d'utilisateur n'a pas été modifié
                updateUserProfile(null, imageUri);
            } else if (isUsernameChanged && imageUri != null) {
                // Si à la fois le nom d'utilisateur et la nouvelle image ont été modifiés
                updateUserProfile(newUsername, imageUri);
            } else {
                // Si ni le nom d'utilisateur ni la nouvelle image n'ont été modifiés
                // Cacher les éléments d'édition du profil sans effectuer de mise à jour
                usernameEdit.setVisibility(View.GONE);
                addPhoto.setVisibility(View.GONE);
                validate.setVisibility(View.GONE);
                usernameTextView.setVisibility(View.VISIBLE);
                editProfileButton.setVisibility(View.VISIBLE);
                backButton.setVisibility(View.VISIBLE);
            }
        });
    }

    private void updateUserProfile(String newUsername, Uri imageUri) {
        DatabaseReference userRef = databaseReference.child(userId);

        if (newUsername != null && !newUsername.isEmpty()) {
            if (newUsername.length() > 15) {
                usernameEdit.setError("Username is too long. Maximum length is " + 15 + " characters.");
                usernameEdit.requestFocus();
                return;
            }
            userRef.child("pseudo").setValue(newUsername);
            usernameTextView.setText(newUsername); // Mettre à jour le TextView usernameTextView immédiatement
        }

        // Mettre à jour l'image de profil si une nouvelle URI est fournie
        if (imageUri != null) {
            // Enregistrer la nouvelle image de profil dans Firebase Storage
            String imageFileName = userId + ".jpg";
            StorageReference imageRef = storageReference.child("profile_images").child(imageFileName);

            ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setMessage("Uploading image...");
            progressDialog.setCancelable(false);
            progressDialog.show();

            imageRef.putFile(imageUri)
                    .addOnSuccessListener(taskSnapshot -> {
                        imageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                            userRef.child("image").setValue(uri.toString());

                            progressDialog.dismiss();
                            Toast.makeText(ProfileActivity.this, "Profile updated successfully", Toast.LENGTH_SHORT).show();

                            Glide.with(ProfileActivity.this)
                                    .load(uri)
                                    .into(profileImageView);

                            usernameEdit.setVisibility(View.GONE);
                            addPhoto.setVisibility(View.GONE);
                            validate.setVisibility(View.GONE);
                            usernameTextView.setVisibility(View.VISIBLE);
                            editProfileButton.setVisibility(View.VISIBLE);
                        });
                    })
                    .addOnFailureListener(e -> {
                        progressDialog.dismiss();
                        Toast.makeText(ProfileActivity.this, "Failed to upload image", Toast.LENGTH_SHORT).show();
                    });
        } else {
            usernameEdit.setVisibility(View.GONE);
            addPhoto.setVisibility(View.GONE);
            validate.setVisibility(View.GONE);
            usernameTextView.setVisibility(View.VISIBLE);
            editProfileButton.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            profileImageView.setImageURI(imageUri);
        }
    }
}