package com.example.hobbyzooapp;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.load.model.LazyHeaders;
import com.bumptech.glide.request.RequestOptions;
import com.example.hobbyzooapp.Activities.MyActivities;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;


public class ProfileActivity extends AppCompatActivity {
    private TextView usernameTextView;
    private ImageView profileImageView;
    private TextView editProfileTextView;
    private Button personalInfoButton;
    private Button myActivitiesButton;
    private Button followMyProgressButton;
    private ImageButton backButton;
    private Button settingsButton;
    private EditText usernameEdit;
    private ImageView addPhoto;
    private Button validate;

    private FirebaseAuth firebaseAuth;
    private FirebaseStorage storage;
    private StorageReference storageReference;
    private DatabaseReference databaseReference;
    private String pseudo;
    private Uri imageUri;
    private String userId;
    private static final int PICK_IMAGE = 1;


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
                        pseudo = snapshot.child("pseudo").getValue(String.class);
                        if (pseudo != null) {
                            usernameTextView.setText(pseudo);
                            usernameEdit.setText(pseudo); // Afficher le pseudo dans le champ d'édition
                        }

                        String image = snapshot.child("image").getValue(String.class);
                        if (image != null) {
                            Glide.with(ProfileActivity.this)
                                    .load(image)
                                    .into(profileImageView);
                        } else {
                            Glide.with(ProfileActivity.this)
                                    .load(R.drawable.ic_profile)
                                    .into(profileImageView);
                        }

                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    // Handle data reading errors
                }

            });
            Query query = databaseReference.orderByChild("email").equalTo(user.getEmail());
            reference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@androidx.annotation.NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        String pseudo = snapshot.child("pseudo").getValue(String.class);
                        if (pseudo != null) {
                            usernameTextView.setText(pseudo);
                        }

                        String image = snapshot.child("image").getValue(String.class);
                        if (image != null) {
                            Glide.with(ProfileActivity.this)
                                    .load(image)
                                    .into(profileImageView);
                        } else {
                            Glide.with(ProfileActivity.this)
                                    .load(R.drawable.ic_profile)
                                    .into(profileImageView);
                        }

                    }

                }

                @Override
                public void onCancelled(@androidx.annotation.NonNull DatabaseError error) {

                }
            });        }


        myActivitiesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ProfileActivity.this, MyActivities.class));
            }
        });


        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ProfileActivity.this, HomeActivity.class));
                finish();

            }
        });

        settingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ProfileActivity.this, SettingsActivity.class));

            }
        });


        editProfileTextView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                usernameEdit.setVisibility(View.VISIBLE);
                addPhoto.setVisibility(View.VISIBLE);
                validate.setVisibility(View.VISIBLE);
                usernameTextView.setVisibility(View.GONE);
                editProfileTextView.setVisibility(View.GONE);

            }
        });
//photo
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        databaseReference = FirebaseDatabase.getInstance().getReference();




        addPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent, PICK_IMAGE);
            }
        });



        validate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String newUsername = usernameEdit.getText().toString().trim();
                if (imageUri == null) {
                    Toast.makeText(ProfileActivity.this, "Please select an image", Toast.LENGTH_SHORT).show();
                } else {
                    validate(newUsername, imageUri);
                }
            }
        });


//
    }

    private void validate(String newUsername, Uri imageUri) {
        DatabaseReference userRef = databaseReference.child(userId);

        // Mettre à jour le nom d'utilisateur
        userRef.child("pseudo").setValue(newUsername);

        // Enregistrer la nouvelle image de profil dans Firebase Storage
        String imageFileName = userId + ".jpg";
        StorageReference imageRef = storageReference.child("profile_images").child(imageFileName);

        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Uploading image...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        imageRef.putFile(imageUri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        // Obtenir l'URL de téléchargement de l'image
                        imageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                // Mettre à jour l'URL de l'image de profil
                                userRef.child("image").setValue(uri.toString());

                                progressDialog.dismiss();
                                Toast.makeText(ProfileActivity.this, "Profile updated successfully", Toast.LENGTH_SHORT).show();

                                // Recharger l'image de profil mise à jour
                                Glide.with(ProfileActivity.this)
                                        .load(uri)
                                        .into(profileImageView);

                                // Cacher les éléments d'édition du profil après validation
                                usernameEdit.setVisibility(View.GONE);
                                addPhoto.setVisibility(View.GONE);
                                validate.setVisibility(View.GONE);
                                usernameTextView.setVisibility(View.VISIBLE);
                                editProfileTextView.setVisibility(View.VISIBLE);
                            }
                        });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        Toast.makeText(ProfileActivity.this, "Failed to upload image", Toast.LENGTH_SHORT).show();
                    }
                });
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            // Afficher l'image sélectionnée dans ImageView
            profileImageView.setImageURI(imageUri);
        }
    }




}
