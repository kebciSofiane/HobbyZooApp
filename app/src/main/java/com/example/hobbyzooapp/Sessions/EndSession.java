package com.example.hobbyzooapp.Sessions;
import com.example.hobbyzooapp.R;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.InputFilter;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.hobbyzooapp.Activities.ActivityPage;
import com.example.hobbyzooapp.RegisterActivity;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
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

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class EndSession extends AppCompatActivity {

    private static final int RETOUR_PRENDRE_PHOTO = 1;
    ImageView petPic;
    Button takeApic;
    ImageView takenImage;
    Button skipButton;
    TextView commentValidated;
    Button validateButton;
    Button validateButton2;
    EditText commentField;
    TextView sessionCount;
    Button modifyPicButton;
    Button modifyCommentButton;
    private String photoPath = "";
    private static final int REQUEST_WRITE_EXTERNAL_STORAGE = 1;
    FirebaseAuth firebaseAuth;
    Intent intent = getIntent();
    String activity_id ;
    String session_id ;
    String activityPet;
    String session_duration;
    String spent_time;
    long totalSessionTime;
    Uri photoUri;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_end_session);

        Intent intent = getIntent();
        activity_id = intent.getStringExtra("activity_id");
        session_id = intent.getStringExtra("session_id");
        totalSessionTime = intent.getLongExtra("spent_time",0);

        firebaseAuth = FirebaseAuth.getInstance();
        petPic = findViewById(R.id.petPicture);
        petPic.setImageResource(R.drawable.koala_icon);
        takeApic = findViewById(R.id.takeAPic);
        takenImage = findViewById(R.id.takenImage);
        commentField = findViewById(R.id.commentText);
        validateButton = findViewById(R.id.validateButton);
        skipButton = findViewById(R.id.skipButton);
        commentValidated = findViewById(R.id.commentValidated);
        validateButton2 = findViewById(R.id.validateButton2);
        sessionCount = findViewById(R.id.sessionCount);
        modifyPicButton = findViewById(R.id.ModifyPicButton);
        modifyCommentButton = findViewById(R.id.ModifyCommentButton);

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
                    String resourceName = activityPet+"_icon";
                    int resId = EndSession.this.getResources().getIdentifier(resourceName,"drawable",EndSession.this.getPackageName());
                    petPic.setImageResource(resId);

                    long newSPentTime = Integer.parseInt(spent_time)+(totalSessionTime/ (1000 * 60));
                    activityRef.child("spent_time").setValue(String.valueOf(newSPentTime), new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                            if (databaseError == null) {
                                System.out.println("Activity modified successfully!");
                            } else {
                                System.err.println("Error when activity modified: " + databaseError.getMessage());
                            }
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
                // Une erreur s'est produite lors de la récupération des données
            }
        });

        InputFilter[] filters = new InputFilter[1];
        filters[0] = new InputFilter.LengthFilter(100);
        commentField.setFilters(filters);

        takeApic.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                try {
                    takePicture();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        modifyCommentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                commentField.setVisibility(View.VISIBLE);
                commentValidated.setVisibility(View.GONE);
                validateButton2.setVisibility(View.GONE);
                validateButton.setVisibility(View.VISIBLE);
                modifyCommentButton.setVisibility(View.GONE);
            }
        });

        modifyPicButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    takePicture();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        validateButton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseReference sessionRef = FirebaseDatabase.getInstance().getReference().child("Session").child(session_id);
                uploadImageToFirebase();
                if (commentValidated.getText().equals("")){
                    sessionRef.child("session_comment").setValue("no comment for this photo");
                } else {
                    sessionRef.child("session_comment").setValue(commentValidated.getText());
                }
                endSession();
            }
        });

        skipButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { endSession(); }
        });

        validateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String comment = String.valueOf(commentField.getText());
                validateButton.setVisibility(View.GONE);
                skipButton.setVisibility(View.GONE);
                commentField.setVisibility(View.GONE);
                commentValidated.setText(comment);
                commentValidated.setVisibility(View.VISIBLE);
                takeApic.setVisibility(View.GONE);
                validateButton2.setVisibility(View.VISIBLE);
                modifyPicButton.setVisibility(View.VISIBLE);
                modifyCommentButton.setVisibility(View.VISIBLE);


            }
        });

    }

    private void takePicture() throws IOException {
        Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        if (intent.resolveActivity(getPackageManager())!=null){
            String date = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
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
        String resourceName = activityPet+"_icon";
        int resId = EndSession.this.getResources().getIdentifier(resourceName,"drawable",EndSession.this.getPackageName());
        petPic.setImageResource(resId);
    }

    @Override
    public void onBackPressed() {
    }

    private void savePhotoToGallery() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    REQUEST_WRITE_EXTERNAL_STORAGE);
        } else {
            if (takenImage.getDrawable() != null) {
                Bitmap bitmap = ((BitmapDrawable) takenImage.getDrawable()).getBitmap();

                ContentValues values = new ContentValues();
                values.put(MediaStore.Images.Media.DISPLAY_NAME, "nom_de_la_photo.jpg");
                values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");

                ContentResolver resolver = getContentResolver();
                Uri uri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

                try {
                    OutputStream outputStream = resolver.openOutputStream(uri);
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
                    outputStream.close();
                    Toast.makeText(this, "La photo a été enregistrée dans la galerie", Toast.LENGTH_SHORT).show();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                Toast.makeText(this, "Sélectionnez une photo avant de l'enregistrer", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public  void updateSessionCount(){
        long hours = TimeUnit.MILLISECONDS.toHours(totalSessionTime);
        long minutes = TimeUnit.MILLISECONDS.toMinutes(totalSessionTime - TimeUnit.HOURS.toMillis(hours));
        int hourDuration = Integer.parseInt(session_duration)/60;
        int minutesDuration = Integer.parseInt(session_duration)%60;
        sessionCount.setText(hours+"h"+minutes+"/"+ hourDuration+"h"+minutesDuration);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_WRITE_EXTERNAL_STORAGE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                savePhotoToGallery();
            } else {
                Toast.makeText(this, "La permission d'enregistrement dans la galerie a été refusée", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void uploadImageToFirebase() {

        FirebaseUser user = firebaseAuth.getCurrentUser();
        if (user != null) {
            FirebaseStorage storage = FirebaseStorage.getInstance();
            StorageReference storageReference = storage.getReference();
            StorageReference profileRef = storageReference.child("sessions/" + session_id + "/picture.jpg");
            profileRef.putFile(photoUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            profileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    //progressDialog.dismiss();
                                    String imageUrl = uri.toString();
                                    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
                                    databaseReference.child("Session").child(session_id).child("session_picture").setValue(imageUrl);
                                    Toast.makeText(EndSession.this, "Image uploaded successfully", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(EndSession.this, "Failed to upload image", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    private void endSession(){
        DatabaseReference sessionRef = FirebaseDatabase.getInstance().getReference().child("Session").child(session_id);
        Date date = new Date();
        String time = new SimpleDateFormat("HHmm").format(date);
        String day = new SimpleDateFormat("dd").format(date);
        String month = new SimpleDateFormat("MM").format(date);
        String year = new SimpleDateFormat("yyyy").format(date);
        sessionRef.child("session_time").setValue(time);
        sessionRef.child("session_done").setValue("TRUE");
        sessionRef.child("session_day").setValue(day);
        sessionRef.child("session_month").setValue(month);
        sessionRef.child("session_year").setValue(year);

        Intent intent = new Intent(EndSession.this, ActivityPage.class);
        intent.putExtra("activity_id", activity_id);
        startActivity(intent);
    }
}