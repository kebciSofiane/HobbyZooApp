package com.example.hobbyzooapp;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class PersonalInformationActivity extends AppCompatActivity {

    private TextView emailTv;
    private LinearLayout passwordLayout;
    private EditText passwordEt;
    private ImageView validateButton;
    private Button changePasswordBtn;
    private Button unregisterBtn;

    private EditText newPasswordEt;
    private EditText currentPasswordEt;

    private FirebaseAuth auth;
    private boolean isEditMode = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_information);

        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();



        emailTv = findViewById(R.id.emailTv);
        passwordLayout = findViewById(R.id.passwordLayout);
        passwordEt = findViewById(R.id.passwordEt);
        validateButton = findViewById(R.id.validateButton);
        changePasswordBtn = findViewById(R.id.changePasswordBtn);
        unregisterBtn = findViewById(R.id.unregisterBtn);
        newPasswordEt = findViewById(R.id.passwordEt);
        currentPasswordEt = findViewById(R.id.passwordEt);
        if (user != null) {
            String email = user.getEmail();
            emailTv.setText(email);
        }


        changePasswordBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isEditMode) {
                    // Save password changes
                    changePassword();
                } else {
                    // Enter edit mode
                    enterEditMode();
                }
            }
        });

        unregisterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Perform unregister action
                unregisterUser();
            }
        });
    }

    private void enterEditMode() {
        isEditMode = true;

        // Show EditText for password
        passwordEt.setVisibility(View.VISIBLE);
        passwordEt.setText("");

        // Show validation button
        validateButton.setVisibility(View.VISIBLE);

        // Change button text
        changePasswordBtn.setText("Save");
    }

    private void changePassword() {
        FirebaseUser user = auth.getCurrentUser();
        if (user != null) {
            String email = user.getEmail();
            String currentPassword = currentPasswordEt.getText().toString().trim();
            String newPassword = newPasswordEt.getText().toString().trim();

            // Vérifier l'authentification de l'utilisateur avant de mettre à jour le mot de passe
            AuthCredential credential = EmailAuthProvider.getCredential(email, currentPassword);
            user.reauthenticate(credential)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            user.updatePassword(newPassword)
                                    .addOnCompleteListener(updateTask -> {
                                        if (updateTask.isSuccessful()) {
                                            Toast.makeText(PersonalInformationActivity.this, "Password updated successfully", Toast.LENGTH_SHORT).show();
                                            currentPasswordEt.setText("");
                                            newPasswordEt.setText("");
                                            exitEditMode();
                                        } else {
                                            Toast.makeText(PersonalInformationActivity.this, "Failed to update password", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        } else {
                            Toast.makeText(PersonalInformationActivity.this, "Authentication failed. Please enter your current password correctly.", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    private void exitEditMode() {
        isEditMode = false;

        // Hide EditText for password
        passwordEt.setVisibility(View.GONE);

        // Hide validation button
        validateButton.setVisibility(View.GONE);

        // Change button text
        changePasswordBtn.setText("Change Password");
    }

    private void unregisterUser() {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();
        if (user != null) {
            user.delete()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(PersonalInformationActivity.this, "User unregistered successfully", Toast.LENGTH_SHORT).show();
                            // Effectuer ici toute action supplémentaire nécessaire après la désinscription de l'utilisateur, par exemple rediriger vers une autre activité.
                        } else {
                            Toast.makeText(PersonalInformationActivity.this, "Failed to unregister user", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }
}
