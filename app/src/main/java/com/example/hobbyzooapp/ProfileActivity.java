package com.example.hobbyzooapp;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

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
import com.squareup.picasso.Picasso;


public class ProfileActivity extends AppCompatActivity {
    private TextView usernameTextView;
    private ImageView profileImageView;
    private TextView editProfileTextView;
    private Button personalInfoButton;
    private Button myActivitiesButton;
    private Button followMyProgressButton;
    private ImageButton homeButton;
    private ImageButton settingsButton;

    private FirebaseAuth firebaseAuth;
    private FirebaseStorage storage;
    private StorageReference storageReference;
    private DatabaseReference databaseReference;

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
        databaseReference = FirebaseDatabase.getInstance().getReference("Users");

        if (user != null) {
            DatabaseReference reference = databaseReference.child(user.getUid());
            reference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        String pseudo = snapshot.child("pseudo").getValue(String.class);
                        if (pseudo != null) {
                            usernameTextView.setText(pseudo);
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
                            Picasso.get().load(image).into(profileImageView);
                        } else {
                            Picasso.get().load(R.drawable.ic_profile).into(profileImageView);
                        }
                    }

                }

                @Override
                public void onCancelled(@androidx.annotation.NonNull DatabaseError error) {

                }
            });        }


//////////////////////////////////////////////////////////////// image//
//
//// Ajoutez cette ligne après avoir récupéré la référence de l'utilisateur
//        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("Users").child(user.getUid());
//
//// Écoutez les données une seule fois pour récupérer le nom du fichier image
//        userRef.child("image").addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                if (snapshot.exists()) {
//                    String imageFileName = snapshot.getValue(String.class);
//
//                    // Utilisez le nom du fichier image pour charger l'image depuis Firebase Storage
//                    StorageReference imageRef = storageReference.child(imageFileName);
//
//                    // Continuer avec le code pour charger l'image avec Glide
//                    imageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
//                        @Override
//                        public void onSuccess(Uri uri) {
//                            String imageUrl = uri.toString();
//
//                            Glide.with(ProfileActivity.this)
//                                    .load(imageUrl)
//                                    .placeholder(R.drawable.ic_profile) // Image temporaire de substitution
//                                    .error(R.drawable.ic_error) // Image à afficher en cas d'échec de chargement
//                                    .into(profileImageView);
//                        }
//                    }).addOnFailureListener(new OnFailureListener() {
//                        @Override
//                        public void onFailure(@NonNull Exception e) {
//                            // Gérez les échecs de téléchargement d'image
//                        }
//                    });
//
//                } else {
//                    // Le nom du fichier image n'existe pas dans la base de données
//                    // Gérez cette situation en conséquence
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//                // Gérez les erreurs de lecture de données
//            }
//        });


        // autre methode pour ajouter l'image


//
//
//
//
//        String userId = user.getUid();
//        StorageReference profileRef = storageReference.child("users/" + userId + "profile.jpg");
//
//        Glide.with(ProfileActivity.this)
//                .load(profileRef)
//                .placeholder(R.drawable.ic_profile) // Image de substitution en cas de chargement ou d'erreur
//                .error(R.drawable.ic_error) // Image à afficher en cas d'erreur de chargement
//                .into(profileImageView);







///////////////////////////////////////////////////////////////////////////


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

        settingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ProfileActivity.this, SettingsActivity.class));

            }
        });
    }


}
