package com.example.hobbyzooapp;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class PersonalInformationActivity extends AppCompatActivity {

    private TextView emailTv;
    private TextView passwordTv;
    private EditText passwordEt;
    private ImageView validateButton;
    private Button changePasswordBtn;
    private Button unregisterBtn;



    private FirebaseAuth auth;
    private boolean isEditMode = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_information);

         auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();



        emailTv = findViewById(R.id.emailTv);

        passwordTv = findViewById(R.id.passwordTv);
        validateButton = findViewById(R.id.validateButton);
        changePasswordBtn = findViewById(R.id.changePasswordBtn);
        unregisterBtn = findViewById(R.id.unregisterBtn);

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

        passwordTv.setVisibility(View.GONE);
        // Show EditText for password



        // Show validation button
        validateButton.setVisibility(View.VISIBLE);

        // Change button text
        changePasswordBtn.setText("Send");
    }

    private void changePassword() {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();
        if (user != null) {
            String email = user.getEmail();
            String currentPassword = passwordTv.getText().toString().trim();

            sendPasswordResetEmail(user);
        }
    }

    private void sendPasswordResetEmail(FirebaseUser user) {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        String email = user.getEmail();
        auth.sendPasswordResetEmail(email)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(PersonalInformationActivity.this, "Password reset email sent successfully", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(PersonalInformationActivity.this, "Failed to send password reset email", Toast.LENGTH_SHORT).show();
                    }
                });
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
