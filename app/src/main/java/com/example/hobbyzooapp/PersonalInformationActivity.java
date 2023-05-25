package com.example.hobbyzooapp;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class PersonalInformationActivity extends AppCompatActivity {

    private TextView emailTv;
    private TextView usernameLabel;
    private EditText usernameEdit;
    private ImageView profileImageView;
    private Button changeUsernameBtn;
    private Button changePhotoBtn;
    private Button changePasswordBtn;
    private Button unregisterBtn;
    private Button savePhotoBtn;
    private ImageButton backButton;

    private FirebaseAuth firebaseAuth;
    private FirebaseStorage storage;
    private StorageReference storageReference;
    private DatabaseReference databaseReference;
    private static final int PICK_IMAGE = 1;

    private Uri imageUri;
    private boolean isEditMode = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_information);

        firebaseAuth = FirebaseAuth.getInstance();
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        databaseReference = FirebaseDatabase.getInstance().getReference("Users");

        FirebaseUser user = firebaseAuth.getCurrentUser();

        emailTv = findViewById(R.id.emailTv);
        usernameLabel = findViewById(R.id.usernameLabel);
        usernameEdit = findViewById(R.id.usernameEdit);
        profileImageView = findViewById(R.id.profileImageView);
        changeUsernameBtn = findViewById(R.id.changeUsernameBtn);
        changePhotoBtn = findViewById(R.id.changePhotoBtn);
        changePasswordBtn = findViewById(R.id.changePasswordBtn);
        unregisterBtn = findViewById(R.id.unregisterBtn);
        backButton = findViewById(R.id.backButton3);


        if (user != null) {
            emailTv.setText(user.getEmail());

            DatabaseReference reference = databaseReference.child(user.getUid());
            reference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        String username = snapshot.child("pseudo").getValue(String.class);
                        if (username != null) {
                            usernameLabel.setText(username);
                            usernameEdit.setText(username);
                        }

                        String image = snapshot.child("image").getValue(String.class);
                        if (image != null) {
                            Glide.with(PersonalInformationActivity.this)
                                    .load(image)
                                    .into(profileImageView);
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    // Handle data reading errors
                }
            });
        }

        changeUsernameBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isEditMode) {
                    saveUsernameChanges();
                } else {
                    enterEditMode();
                }
            }
        });

        changePhotoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent, PICK_IMAGE);
            }
        });

        changePasswordBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isEditMode) {
                    changePassword();
                } else {
                    enterEditMode();
                }
            }
        });

        unregisterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showConfirmationDialog();
            }
        });
        savePhotoBtn = findViewById(R.id.savePhotoBtn);
        savePhotoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (imageUri != null) {
                    updateProfileImage(imageUri);
                }
            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(PersonalInformationActivity.this, ProfileActivity.class));

            }
        });


    }
    private void showConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Confirm");
        builder.setMessage("Do you really want to unregister from HobbyZoo ?");
        builder.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                unregisterUser();
            }
        });
        builder.setNegativeButton("Cancel", null);

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void enterEditMode() {
        isEditMode = true;
        changeUsernameBtn.setText("Save");
        usernameLabel.setVisibility(View.GONE);
        usernameEdit.setVisibility(View.VISIBLE);
        usernameEdit.requestFocus();
    }

    private void exitEditMode() {
        isEditMode = false;
        changeUsernameBtn.setText("Edit");
        usernameLabel.setVisibility(View.VISIBLE);
        usernameEdit.setVisibility(View.GONE);

    }

    private void changePassword() {
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if (user != null) {
            String email = user.getEmail();
            sendPasswordResetEmail(user);
        }
    }

    private void sendPasswordResetEmail(FirebaseUser user) {
        String email = user.getEmail();
        firebaseAuth.sendPasswordResetEmail(email)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(PersonalInformationActivity.this, "Password reset email sent successfully", Toast.LENGTH_SHORT).show();
                            exitEditMode();
                        } else {
                            Toast.makeText(PersonalInformationActivity.this, "Failed to send password reset email", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }



    private void unregisterUser() {
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if (user != null) {
            user.delete()
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(PersonalInformationActivity.this, "User unregistered successfully", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(PersonalInformationActivity.this, "Failed to unregister user", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }

    private void saveUsernameChanges() {
        String newUsername = usernameEdit.getText().toString().trim();
        FirebaseUser user = firebaseAuth.getCurrentUser();

        if (user != null) {
            DatabaseReference reference = databaseReference.child(user.getUid());
            reference.child("pseudo").setValue(newUsername)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            exitEditMode();
                            Toast.makeText(PersonalInformationActivity.this, "Username updated successfully", Toast.LENGTH_SHORT).show();

                            // Mettre à jour le TextView du nom d'utilisateur (usernameLabel)
                            usernameLabel.setText(newUsername);

                            // Pass the updated username back to ProfileActivity
                            Intent intent = new Intent();
                            intent.putExtra("newUsername", newUsername);
                            setResult(RESULT_OK, intent);
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(PersonalInformationActivity.this, "Failed to update username", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
        Intent intent = new Intent();
        intent.putExtra("newUsername", newUsername);
        intent.putExtra("newImageUri", imageUri != null ? imageUri.toString() : null);
        setResult(RESULT_OK, intent);

        finish();
    }

    private void updateProfileImage(Uri imageUri) {
        FirebaseUser user = firebaseAuth.getCurrentUser();

        if (user != null) {
            StorageReference imageRef = storageReference.child("profile_images").child(user.getUid() + ".jpg");

            ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setMessage("Uploading image...");
            progressDialog.setCancelable(false);
            progressDialog.show();

            imageRef.putFile(imageUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            imageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    DatabaseReference reference = databaseReference.child(user.getUid());
                                    reference.child("image").setValue(uri.toString())
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    progressDialog.dismiss();
                                                    Toast.makeText(PersonalInformationActivity.this, "Profile image updated successfully", Toast.LENGTH_SHORT).show();

                                                    // Pass the updated image URI back to ProfileActivity
                                                    Intent intent = new Intent();
                                                    intent.putExtra("newImageUri", uri.toString());
                                                    setResult(RESULT_OK, intent);

                                                    savePhotoBtn.setVisibility(View.GONE); // Masquer le bouton "Enregistrer"
                                                }
                                            })
                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    progressDialog.dismiss();
                                                    Toast.makeText(PersonalInformationActivity.this, "Failed to update profile image", Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                }
                            });
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(PersonalInformationActivity.this, "Failed to upload image", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            profileImageView.setImageURI(imageUri);
            savePhotoBtn.setVisibility(View.VISIBLE); // Afficher le bouton "Enregistrer"
        }
    }
    @Override
    public void onBackPressed() {
        String newUsername = usernameEdit.getText().toString().trim();

        if (isEditMode) {
            if (!newUsername.isEmpty() && !newUsername.equals(usernameLabel.getText().toString())) {
                exitEditMode();
                usernameLabel.setText(newUsername);

                // Passer les données modifiées en tant qu'extra à travers l'intent de retour
                Intent intent = new Intent();
                intent.putExtra("newUsername", newUsername);
                intent.putExtra("newImageUri", imageUri != null ? imageUri.toString() : null);
                setResult(RESULT_OK, intent);
            } else {
                setResult(RESULT_CANCELED);
            }
        } else {
            setResult(RESULT_CANCELED);
        }

        super.onBackPressed();
    }


}

