package com.example.hobbyzooapp.Sessions;
import com.example.hobbyzooapp.Calendar.CalendarUtils;
import com.example.hobbyzooapp.HomeActivity;
import com.example.hobbyzooapp.R;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.InputFilter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class EndSession extends AppCompatActivity {

    private static final int RETOUR_PRENDRE_PHOTO = 1;
    ImageView petPic, takenImage;
    TextView commentValidated,sessionCount;
    Button takeApic ;
    ImageButton validateButton, validateButton2, skipButton, modifyCommentButton, cancelButton;
    EditText commentField;
    private RelativeLayout windowsPet;
    private String photoPath = "";
    private static final int REQUEST_WRITE_EXTERNAL_STORAGE = 1;
    FirebaseAuth firebaseAuth;
    String activity_id, session_id, activityPet, session_duration, spent_time;
    long totalSessionTime;
    Uri photoUri;


    @RequiresApi(api = Build.VERSION_CODES.O)
    @SuppressLint({"MissingInflatedId", "SetTextI18n"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_end_session);

        Intent intent = getIntent();
        activity_id = intent.getStringExtra("activity_id");
        session_id = intent.getStringExtra("session_id");
        totalSessionTime = intent.getLongExtra("spent_time",0);
        windowsPet = findViewById(R.id.windows_pet);
        firebaseAuth = FirebaseAuth.getInstance();
        petPic = findViewById(R.id.petPicture);
        petPic.setImageResource(R.drawable.koala_icon_neutral);

        takeApic = findViewById(R.id.takeAPic);
        takenImage = findViewById(R.id.takenImage);
        commentField = findViewById(R.id.commentText);
        validateButton = findViewById(R.id.validateButton);
        skipButton = findViewById(R.id.skipButton);
        commentValidated = findViewById(R.id.commentValidated);
        validateButton2 = findViewById(R.id.validateButton2);
        sessionCount = findViewById(R.id.sessionCount);
        modifyCommentButton = findViewById(R.id.ModifyCommentButton);
        cancelButton = findViewById(R.id.cancelButton);

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference activitiesRef = FirebaseDatabase.getInstance().getReference("Activity");
        DatabaseReference activityRef = activitiesRef.child(activity_id);

        DatabaseReference referenceActivity = database.getReference("Activity");
        referenceActivity.child(activity_id).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    activityPet = dataSnapshot.child("activity_pet").getValue(String.class);
                    spent_time = dataSnapshot.child("spent_time").getValue(String.class);
                    String resourceName = activityPet+"_icon_neutral";
                    int resId = EndSession.this.getResources().getIdentifier(resourceName,"drawable",EndSession.this.getPackageName());
                    petPic.setImageResource(resId);

                    long newSPentTime = Integer.parseInt(spent_time)+(totalSessionTime/ (1000 * 60));
                    activityRef.child("spent_time").setValue(String.valueOf(newSPentTime), (databaseError, databaseReference) -> {
                        if (databaseError == null) {
                            System.out.println("Activity successfully modified !");
                        } else {
                            System.err.println("Error while modifying activity : " + databaseError.getMessage());
                        }
                    });
                } else {
                    // L'activité n'existe pas dans la base de données
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Une erreur s'est produite lors de la récupération des données
            }
        });

        DatabaseReference referenceSession = database.getReference("Session");
        referenceSession.child(session_id).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    session_duration = dataSnapshot.child("session_duration").getValue(String.class);
                    updateSessionCount();
                } else {
                    // L'activité n'existe pas dans la base de données
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w("TAG", "Data recovery error", databaseError.toException());
            }
        });

        InputFilter[] filters = new InputFilter[1];
        filters[0] = new InputFilter.LengthFilter(100);
        commentField.setFilters(filters);

        takeApic.setOnClickListener(v -> {
            try {
                takePicture();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

        modifyCommentButton.setOnClickListener(v -> {
            commentField.setVisibility(View.VISIBLE);
            validateButton.setVisibility(View.VISIBLE);
            commentValidated.setVisibility(View.GONE);
            validateButton2.setVisibility(View.GONE);
            modifyCommentButton.setVisibility(View.GONE);
            cancelButton.setVisibility(View.GONE);
        });


        validateButton2.setOnClickListener(v -> {
            DatabaseReference sessionRef = FirebaseDatabase.getInstance().getReference().child("Session").child(session_id);
            if (commentValidated.getText().equals("") && !photoPath.equals("")){
                sessionRef.child("session_comment").setValue("No comment for this photo");
                uploadImageToFirebase();
                endSession();
            } else if (!commentValidated.getText().equals("")){
                sessionRef.child("session_comment").setValue(commentValidated.getText());
                uploadImageToFirebase();
                endSession();
            } else if (commentValidated.getText().equals("") && photoPath.equals("")){
                LayoutInflater inflater = getLayoutInflater();
                View dialogView = inflater.inflate(R.layout.custom_dialog_, null);

                TextView dialogTitle = dialogView.findViewById(R.id.dialogTitle);
                TextView dialogText = dialogView.findViewById(R.id.dialogText);
                Button dialogButtonLeft = dialogView.findViewById(R.id.dialogButtonLeft);
                Button dialogButtonRight = dialogView.findViewById(R.id.dialogButtonRight);

                dialogTitle.setText("No comment and no picture");
                dialogText.setText("Do you want to continue ?");
                dialogButtonLeft.setText("Edit");
                dialogButtonLeft.setTextColor(Color.GREEN);
                dialogButtonRight.setText("Skip");
                dialogButtonRight.setTextColor(Color.RED);

                AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(EndSession.this);
                dialogBuilder.setView(dialogView);
                AlertDialog dialog = dialogBuilder.create();
                dialog.show();
                dialogButtonLeft.setOnClickListener(v1 -> dialog.dismiss());
                dialogButtonRight.setOnClickListener(v12 -> {
                    endSession();
                    dialog.dismiss();
                });
            }
        });


        cancelButton.setOnClickListener(v -> {
            LayoutInflater inflater = getLayoutInflater();
            View dialogView = inflater.inflate(R.layout.custom_dialog_, null);

            TextView dialogTitle = dialogView.findViewById(R.id.dialogTitle);
            Button dialogButtonLeft = dialogView.findViewById(R.id.dialogButtonLeft);
            Button dialogButtonRight = dialogView.findViewById(R.id.dialogButtonRight);

            dialogTitle.setText("You're about to cancel your feedback");
            dialogButtonLeft.setText("Edit");
            dialogButtonLeft.setTextColor(Color.GREEN);
            dialogButtonRight.setText("Cancel");
            dialogButtonRight.setTextColor(Color.RED);

            AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(EndSession.this);
            dialogBuilder.setView(dialogView);
            AlertDialog dialog = dialogBuilder.create();
            dialog.show();
            dialogButtonLeft.setOnClickListener(v13 -> dialog.dismiss());
            dialogButtonRight.setOnClickListener(v14 -> {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    endSession();
                }
                dialog.dismiss();
            });

        });

        skipButton.setOnClickListener(v -> endSession());

        validateButton.setOnClickListener(v -> {
            String comment = String.valueOf(commentField.getText());
            validateButton.setVisibility(View.GONE);
            skipButton.setVisibility(View.GONE);
            commentField.setVisibility(View.GONE);
            commentValidated.setText(comment);
            commentValidated.setVisibility(View.VISIBLE);
            takeApic.setVisibility(View.VISIBLE);
            validateButton2.setVisibility(View.VISIBLE);
            modifyCommentButton.setVisibility(View.VISIBLE);
            cancelButton.setVisibility(View.VISIBLE);

        });

    }

    @SuppressLint("QueryPermissionsNeeded")
    private void takePicture() throws IOException {
        Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        if (intent.resolveActivity(getPackageManager())!=null){
            @SuppressLint("SimpleDateFormat") String date = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            File photoDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
            File photoFile = File.createTempFile(date,".jpg", photoDir);
            photoPath = photoFile.getAbsolutePath();

            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
                photoUri = FileProvider.getUriForFile(EndSession.this,
                        EndSession.this.getApplicationContext().getOpPackageName()+".provider",
                        photoFile);
            }
            intent.putExtra(MediaStore.EXTRA_OUTPUT,photoUri);
            startActivityForResult(intent, RETOUR_PRENDRE_PHOTO);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Bitmap image = BitmapFactory.decodeFile(photoPath);
        takenImage.setImageBitmap(image);
        takenImage.setVisibility(View.VISIBLE);
        String resourceName = activityPet+"_icon_neutral";
        int resId = EndSession.this.getResources().getIdentifier(resourceName,"drawable",EndSession.this.getPackageName());
        petPic.setImageResource(resId);
        windowsPet.setVisibility(View.GONE);
    }

    @Override
    public void onBackPressed() {}


    @SuppressLint("SetTextI18n")
    public  void updateSessionCount(){
        long hours = TimeUnit.MILLISECONDS.toHours(totalSessionTime);
        long minutes = TimeUnit.MILLISECONDS.toMinutes(totalSessionTime - TimeUnit.HOURS.toMillis(hours));
        int hourDuration = Integer.parseInt(session_duration)/60;
        int minutesDuration = Integer.parseInt(session_duration)%60;
        sessionCount.setText(hours+"h"+minutes+"min / "+ hourDuration+"h"+minutesDuration+"min");
    }

    private void uploadImageToFirebase() {
        if (!photoPath.equals("")){
            FirebaseUser user = firebaseAuth.getCurrentUser();
            if (user != null) {
                FirebaseStorage storage = FirebaseStorage.getInstance();
                StorageReference storageReference = storage.getReference();
                StorageReference profileRef = storageReference.child("sessions/" + session_id + "/picture.jpg");
                profileRef.putFile(photoUri)
                        .addOnSuccessListener(taskSnapshot -> profileRef.getDownloadUrl().addOnSuccessListener(uri -> {
                            //progressDialog.dismiss();
                            String imageUrl = uri.toString();
                            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
                            databaseReference.child("Session").child(session_id).child("session_picture").setValue(imageUrl);
                            Toast.makeText(EndSession.this, "Image uploaded successfully", Toast.LENGTH_SHORT).show();
                        }))
                        .addOnFailureListener(e -> Toast.makeText(EndSession.this, "Failed to upload image", Toast.LENGTH_SHORT).show());
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void endSession(){
        DatabaseReference sessionRef = FirebaseDatabase.getInstance().getReference().child("Session").child(session_id);
        Date date = new Date();
        CalendarUtils.selectedDate= LocalDate.now();
        @SuppressLint("SimpleDateFormat") String time = new SimpleDateFormat("HHmm").format(date);
        @SuppressLint("SimpleDateFormat") String day = new SimpleDateFormat("dd").format(date);
        @SuppressLint("SimpleDateFormat") String month = new SimpleDateFormat("MM").format(date);
        @SuppressLint("SimpleDateFormat") String year = new SimpleDateFormat("yyyy").format(date);
        sessionRef.child("session_time").setValue(time);
        sessionRef.child("session_done").setValue("TRUE");
        sessionRef.child("session_day").setValue(day);
        sessionRef.child("session_month").setValue(month);
        sessionRef.child("session_year").setValue(year);

        finishAffinity();
        Intent intent = new Intent(EndSession.this, HomeActivity.class);
        intent.putExtra("activity_id", activity_id);
        startActivity(intent);
        finish();
    }
}