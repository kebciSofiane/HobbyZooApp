package com.example.hobbyzooapp.AccountManagement;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.hobbyzooapp.R;
import com.example.hobbyzooapp.WeeklyEvent;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.HashMap;

public class LoginActivity extends AppCompatActivity {

    private static final int RC_SIGN_IN = 100;
    GoogleSignInClient googleSignInClient;
    EditText emailET, passwordEt;
    TextView notHaveAccountTv, recoverPassTv;
    Button loginBtn;
    SignInButton googleLoginBtn;
    private FirebaseAuth firebaseAuth;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        googleSignInClient = GoogleSignIn.getClient(this, gso);

        firebaseAuth = FirebaseAuth.getInstance();

        emailET = findViewById(R.id.emailEt);
        passwordEt = findViewById(R.id.passwordEt);
        notHaveAccountTv = findViewById(R.id.notHaveAccountTv);
        recoverPassTv = findViewById(R.id.recoverPassTv);
        loginBtn = findViewById(R.id.login_btn);
        googleLoginBtn = findViewById(R.id.googleLoginBtn);

        loginBtn.setOnClickListener(v -> {
            String email = emailET.getText().toString();
            String password = passwordEt.getText().toString();
            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                emailET.setError("Invalid email address");
                emailET.setFocusable(true);
            } else if (passwordEt.getText().toString().trim().isEmpty()){
                passwordEt.setError("Password can't be empty");
                passwordEt.setFocusable(true);
            }
            else{
                loginUser(email, password);
            }
        });

        notHaveAccountTv.setOnClickListener(v -> {
            startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
            finish();
        });

        recoverPassTv.setOnClickListener(v -> showRecoverPasswordDialog());

        googleLoginBtn.setOnClickListener(v -> {
            Intent signInIntent = googleSignInClient.getSignInIntent();
            startActivityForResult(signInIntent, RC_SIGN_IN);
        });

        progressDialog = new ProgressDialog(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if (user != null && user.isEmailVerified()) {
            startActivity(new Intent(LoginActivity.this, WeeklyEvent.class));
            finish();
        }
    }

    private void showRecoverPasswordDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Recover Password");

        LinearLayout linearLayout = new LinearLayout(this);
        EditText emailEt = new EditText(this);
        emailEt.setHint("Email");
        emailEt.setMinEms(16);

        linearLayout.addView(emailEt);
        linearLayout.setPadding(10, 10, 10, 10);

        builder.setView(linearLayout);

        builder.setPositiveButton("Recover", (dialog, which) -> {
            String email = emailEt.getText().toString().trim();
            beginRecovery(email);
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());

        builder.create().show();
    }

    private void beginRecovery(String email) {
        progressDialog.setMessage("Sending email ...");
        progressDialog.show();

        firebaseAuth.sendPasswordResetEmail(email).addOnCompleteListener(task -> {
            progressDialog.dismiss();
            if (task.isSuccessful()) {
                Toast.makeText(LoginActivity.this, "Email sent", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(LoginActivity.this, "Failed", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(e -> {
            progressDialog.dismiss();
            Toast.makeText(LoginActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
        });
    }

    private void loginUser(String email, String pswrd) {
        progressDialog.setMessage("Logging In ...");
        progressDialog.show();
        firebaseAuth.signInWithEmailAndPassword(email, pswrd)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = firebaseAuth.getCurrentUser();
                        if (user != null && user.isEmailVerified()) {
                            progressDialog.dismiss();
                            startActivity(new Intent(LoginActivity.this, WeeklyEvent.class));
                            finish();
                        } else {
                            progressDialog.dismiss();
                            Toast.makeText(LoginActivity.this, "Please verify your email before logging in.", Toast.LENGTH_SHORT).show();
                            firebaseAuth.signOut();
                        }
                    } else {
                        progressDialog.dismiss();
                        Toast.makeText(LoginActivity.this, "Authentication failed", Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(e -> {
                    progressDialog.dismiss();
                    Toast.makeText(LoginActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Sign in success, update UI with the signed-in user's information
                        FirebaseUser user = firebaseAuth.getCurrentUser();
                        LocalDate currentDate = null;
                        LocalDate nextMonday = null;
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            currentDate = LocalDate.now();
                            nextMonday = currentDate.with(DayOfWeek.MONDAY);
                            if (currentDate.compareTo(nextMonday) > 0) {
                                nextMonday = nextMonday.plusWeeks(1);
                            }
                        }

                        String email = user.getEmail();
                        String uid = user.getUid();

                        // When user is registered, store user info in Firebase Realtime Database
                        HashMap<Object, Object> hashMap = new HashMap<>();
                        hashMap.put("email", email);
                        hashMap.put("uid", uid);
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            hashMap.put("connectNextMondayDay", nextMonday.getDayOfMonth());
                            hashMap.put("connectNextMondayMonth",  Integer.parseInt(String.valueOf(nextMonday.getMonth().getValue())));
                            hashMap.put("connectNextMondayYear", nextMonday.getYear());
                        }
                        FirebaseDatabase database = FirebaseDatabase.getInstance();
                        DatabaseReference reference = database.getReference("Users");
                        reference.child(uid).setValue(hashMap);

                        Toast.makeText(LoginActivity.this, "" + user.getEmail(), Toast.LENGTH_SHORT).show();

                        startActivity(new Intent(LoginActivity.this, WeeklyEvent.class));
                        finish();
                    } else {
                        Toast.makeText(LoginActivity.this, "Login Failed...", Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(e -> Toast.makeText(LoginActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                Toast.makeText(this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }

}
