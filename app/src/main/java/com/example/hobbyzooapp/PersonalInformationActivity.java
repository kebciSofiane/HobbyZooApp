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

import java.text.BreakIterator;

public class PersonalInformationActivity extends AppCompatActivity {

    private TextView emailTv;

    private Button changePasswordBtn;
    private Button unregisterBtn;
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
        changePasswordBtn = findViewById(R.id.changePasswordBtn);
        unregisterBtn = findViewById(R.id.unregisterBtn);
        backButton = findViewById(R.id.backButton3);


        if (user != null) {
            emailTv.setText(user.getEmail());
        }


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

    }

    private void exitEditMode() {
        isEditMode = false;


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

//    private void saveUsernameChanges() {
//        String newUsername = usernameEdit.getText().toString().trim();
//        FirebaseUser user = firebaseAuth.getCurrentUser();
//
//        if (user != null) {
//            DatabaseReference reference = databaseReference.child(user.getUid());
//            reference.child("pseudo").setValue(newUsername)
//                    .addOnSuccessListener(new OnSuccessListener<Void>() {
//                        @Override
//                        public void onSuccess(Void aVoid) {
//                            exitEditMode();
//                            Toast.makeText(PersonalInformationActivity.this, "Username updated successfully", Toast.LENGTH_SHORT).show();
//
//                            // Mettre à jour le TextView du nom d'utilisateur (usernameLabel)
//                            usernameLabel.setText(newUsername);
//
//                            // Pass the updated username back to ProfileActivity
//                            Intent intent = new Intent();
//                            intent.putExtra("newUsername", newUsername);
//                            setResult(RESULT_OK, intent);
//                        }
//                    })
//                    .addOnFailureListener(new OnFailureListener() {
//                        @Override
//                        public void onFailure(@NonNull Exception e) {
//                            Toast.makeText(PersonalInformationActivity.this, "Failed to update username", Toast.LENGTH_SHORT).show();
//                        }
//                    });
//        }
//        Intent intent = new Intent();
//        intent.putExtra("newUsername", newUsername);
//        intent.putExtra("newImageUri", imageUri != null ? imageUri.toString() : null);
//        setResult(RESULT_OK, intent);
//    }

//    private void updateProfileImage(Uri imageUri) {
//        FirebaseUser user = firebaseAuth.getCurrentUser();
//
//        if (user != null) {
//            StorageReference imageRef = storageReference.child("profile_images").child(user.getUid() + ".jpg");
//
//            ProgressDialog progressDialog = new ProgressDialog(this);
//            progressDialog.setMessage("Uploading image...");
//            progressDialog.setCancelable(false);
//            progressDialog.show();
//
//            imageRef.putFile(imageUri)
//                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
//                        @Override
//                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
//                            imageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
//                                @Override
//                                public void onSuccess(Uri uri) {
//                                    DatabaseReference reference = databaseReference.child(user.getUid());
//                                    reference.child("image").setValue(uri.toString())
//                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
//                                                @Override
//                                                public void onSuccess(Void aVoid) {
//                                                    progressDialog.dismiss();
//                                                    Toast.makeText(PersonalInformationActivity.this, "Profile image updated successfully", Toast.LENGTH_SHORT).show();
//
//                                                    // Pass the updated image URI back to ProfileActivity
//                                                    Intent intent = new Intent();
//                                                    intent.putExtra("newImageUri", uri.toString());
//                                                    setResult(RESULT_OK, intent);
//
//                                                    savePhotoBtn.setVisibility(View.GONE); // Masquer le bouton "Enregistrer"
//                                                }
//                                            })
//                                            .addOnFailureListener(new OnFailureListener() {
//                                                @Override
//                                                public void onFailure(@NonNull Exception e) {
//                                                    progressDialog.dismiss();
//                                                    Toast.makeText(PersonalInformationActivity.this, "Failed to update profile image", Toast.LENGTH_SHORT).show();
//                                                }
//                                            });
//                                }
//                            });
//                        }
//                    })
//                    .addOnFailureListener(new OnFailureListener() {
//                        @Override
//                        public void onFailure(@NonNull Exception e) {
//                            progressDialog.dismiss();
//                            Toast.makeText(PersonalInformationActivity.this, "Failed to upload image", Toast.LENGTH_SHORT).show();
//                        }
//                    });
//        }
//    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
        }
    }



}

