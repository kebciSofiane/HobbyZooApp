package com.example.hobbyzooapp.AccountManagement;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.hobbyzooapp.Activities.ActivityPage;
import com.example.hobbyzooapp.HomeActivity;
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

public class AccountActivity extends AppCompatActivity {

    private TextView emailTv, myEmail;
    private Button changePasswordBtn, unregisterBtn;
    private ImageButton backButton;
    private FirebaseAuth firebaseAuth;
    private FirebaseStorage storage;
    private StorageReference storageReference;
    private DatabaseReference databaseReference;
    private FirebaseDatabase database;
    private static final int PICK_IMAGE = 1;
    private Uri imageUri;
    private boolean isEditMode = false;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);

        firebaseAuth = FirebaseAuth.getInstance();
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        database = FirebaseDatabase.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference("Users");

        FirebaseUser user = firebaseAuth.getCurrentUser();
        userId = user.getUid();

        emailTv = findViewById(R.id.emailTv);
        changePasswordBtn = findViewById(R.id.changePasswordBtn);
        unregisterBtn = findViewById(R.id.unregisterBtn);
        backButton = findViewById(R.id.backButton3);
        myEmail = findViewById(R.id.myEmail);

        if (user != null) {
            myEmail.setText(getString(R.string.my_email_)); // Utilise la méthode getString() pour récupérer la valeur de la ressource de chaîne
            emailTv.setText(user.getEmail());
        }

        changePasswordBtn.setOnClickListener(v -> {
            if (isEditMode) {
                changePassword();
            } else {
                enterEditMode();
            }
        });

        unregisterBtn.setOnClickListener(v -> showConfirmationDialog());
        backButton.setOnClickListener(v -> finish());
    }
    private void showConfirmationDialog() {
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.custom_dialog_, null);
        TextView dialogTitle = dialogView.findViewById(R.id.dialogTitle);
        TextView dialogText = dialogView.findViewById(R.id.dialogText);
        Button dialogButtonConfirm = dialogView.findViewById(R.id.dialogButtonLeft);
        Button dialogButtonCancel = dialogView.findViewById(R.id.dialogButtonRight);
        dialogTitle.setText("Do you really want to unregister from HobbyZoo ?");
        dialogText.setText("All your data will be definitively deleted !");
        dialogText.setTextColor(Color.RED);
        dialogButtonConfirm.setText("Confirm");
        dialogButtonCancel.setText("Cancel");

        androidx.appcompat.app.AlertDialog.Builder dialogBuilder = new androidx.appcompat.app.AlertDialog.Builder(AccountActivity.this);
        dialogBuilder.setView(dialogView);
        androidx.appcompat.app.AlertDialog dialog = dialogBuilder.create();
        dialog.show();

        dialogButtonConfirm.setOnClickListener(v -> {
            unregisterUser();
            dialog.dismiss();
        });

        dialogButtonCancel.setOnClickListener(v -> dialog.dismiss());
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
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {

                        Toast.makeText(AccountActivity.this, "Password reset email sent successfully", Toast.LENGTH_SHORT).show();
                        exitEditMode();
                    } else {
                        Toast.makeText(AccountActivity.this, "Failed to send password reset email", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void unregisterUser() {
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if (user != null) {
            user.delete()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(AccountActivity.this, "User unregistered successfully", Toast.LENGTH_SHORT).show();
                            deleteDatas();
                        } else {
                            Toast.makeText(AccountActivity.this, "Failed to unregister user", Toast.LENGTH_SHORT).show();
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

    protected  void deleteDatas(){
        DatabaseReference databaseReferenceSession = database.getReference("Session");
        databaseReferenceSession.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    String user_id = dataSnapshot.child("user_id").getValue(String.class);
                    String session_id = dataSnapshot.getKey();
                    if(user_id != null) {
                        if (user_id.equals(userId)) {
                            databaseReferenceSession.child(session_id).removeValue();
                        }
                    }
                }

                DatabaseReference databaseReferenceActivity = database.getReference("Activity");
                databaseReferenceActivity.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            String user_id = dataSnapshot.child("user_id").getValue(String.class);
                            String activity_id = dataSnapshot.getKey();
                            if(user_id != null) {
                                if (user_id.equals(userId)) {
                                    deleteDatasTask(activity_id);
                                    databaseReferenceActivity.child(activity_id).removeValue();
                                }
                            }
                        }

                        DatabaseReference databaseReferenceCategory = database.getReference("Category");
                        databaseReferenceCategory.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot snapshot) {
                                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                    String user_id = dataSnapshot.child("user_id").getValue(String.class);
                                    String category_id = dataSnapshot.getKey();
                                    if(user_id != null) {
                                        if (user_id.equals(userId)) {
                                            databaseReferenceCategory.child(category_id).removeValue();
                                        }
                                    }
                                }
                                DatabaseReference databaseReferenceUsers = database.getReference("Users");
                                databaseReferenceUsers.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot snapshot) {
                                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                            String user_id = dataSnapshot.getKey();
                                            if(user_id != null) {
                                                if (user_id.equals(userId)) {
                                                    databaseReferenceUsers.child(user_id).removeValue();
                                                }
                                            }
                                        }
                                        startActivity(new Intent(AccountActivity.this, RegistrationOrConnexion.class));
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {
                                    }
                                });
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                            }
                        });
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    protected void deleteDatasTask(String activityID){
        DatabaseReference databaseReferenceTasks = database.getReference("Tasks");
        databaseReferenceTasks.orderByChild("activity_id").equalTo(activityID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    String task_id = dataSnapshot.getKey();
                    if(task_id != null) {
                        databaseReferenceTasks.child(task_id).removeValue();
                    }
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }
}