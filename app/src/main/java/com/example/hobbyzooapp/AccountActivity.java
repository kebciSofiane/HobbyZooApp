package com.example.hobbyzooapp;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class AccountActivity extends AppCompatActivity {

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
        setContentView(R.layout.activity_account);

        firebaseAuth = FirebaseAuth.getInstance();
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        databaseReference = FirebaseDatabase.getInstance().getReference("Users");

        FirebaseUser user = firebaseAuth.getCurrentUser();

        emailTv = findViewById(R.id.emailTv);
        changePasswordBtn = findViewById(R.id.changePasswordBtn);
        unregisterBtn = findViewById(R.id.unregisterBtn);
        backButton = findViewById(R.id.backButton3);

       // emailTv.setText(R.string.myEmail);
        if (user != null) {

            emailTv.setText(user.getEmail() );
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
                startActivity(new Intent(AccountActivity.this, ProfileActivity.class));

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
                            Toast.makeText(AccountActivity.this, "Password reset email sent successfully", Toast.LENGTH_SHORT).show();
                            exitEditMode();
                        } else {
                            Toast.makeText(AccountActivity.this, "Failed to send password reset email", Toast.LENGTH_SHORT).show();
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
                                Toast.makeText(AccountActivity.this, "User unregistered successfully", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(AccountActivity.this, "Failed to unregister user", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
        }
    }
}