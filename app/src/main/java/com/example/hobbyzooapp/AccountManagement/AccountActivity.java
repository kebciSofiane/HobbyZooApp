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

import androidx.appcompat.app.AppCompatActivity;

import com.example.hobbyzooapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
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
}