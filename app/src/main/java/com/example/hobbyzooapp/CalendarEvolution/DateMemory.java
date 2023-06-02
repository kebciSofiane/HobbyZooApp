package com.example.hobbyzooapp.CalendarEvolution;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.provider.MediaStore;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.example.hobbyzooapp.ProfileActivity;
import com.example.hobbyzooapp.Calendar.CalendarUtils;
import com.example.hobbyzooapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.io.OutputStream;
import java.time.LocalDate;
import java.util.ArrayList;

public class DateMemory extends AppCompatActivity {
    TextView dateView;
    LocalDate date;
    private static final int REQUEST_WRITE_EXTERNAL_STORAGE = 1;
    ImageView memoryImage;
    String activity_name;
    TextView memoryComment;
    ImageButton backButton;
    Button share, rightArrow, leftArrow, download;
    int memoriesIndex =0;
    ArrayList<String> myMemoriesPictures;
    ArrayList<String> myMemoriesComments;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_date_memory);

        Intent intent = getIntent();
        int day = intent.getIntExtra("day",0);
        int month = intent.getIntExtra("month",0);
        int year = intent.getIntExtra("year",0);
        activity_name  = intent.getStringExtra("activity_name");


        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
             date = LocalDate.of(year,
                    month,
                    day);
        }

        dateView = findViewById(R.id.memoryDate);
        memoryImage = findViewById(R.id.memoryImage);
        share = findViewById(R.id.shareButton);
        leftArrow = findViewById(R.id.scrollMemoriesLeft);
        rightArrow = findViewById(R.id.scrollMemoriesRight);
        download = findViewById(R.id.downloadButton);
        memoryComment=findViewById(R.id.memoryComment);
        backButton=findViewById(R.id.memoryBackButton);

        dateView.setText(date.toString());

        showMemories();


        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DateMemory.this, MyEvolutionActivity.class);
                CalendarUtils.selectedDate = date;
                startActivity(intent);
            }
        });

        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                partagerSurTwitter();
            }
        });

    }

    private void partagerSurTwitter() {
        String cheminImage = "/path/to/your/image.jpg";
        Uri imageUri = Uri.parse(cheminImage);

        Intent partagerIntent = new Intent(Intent.ACTION_SEND);
        partagerIntent.setType("image/*");
        partagerIntent.putExtra(Intent.EXTRA_STREAM, imageUri);
        partagerIntent.setPackage("com.twitter.android"); // L'identifiant du package de l'application Twitter

        PackageManager packageManager = getPackageManager();
        if (packageManager.resolveActivity(partagerIntent, 0) != null) {
            startActivity(partagerIntent);
        } else {
            Toast.makeText(this, "L'application Twitter n'est pas installée.", Toast.LENGTH_SHORT).show();
        }
    }

    private void savePhotoToGallery() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    REQUEST_WRITE_EXTERNAL_STORAGE);
        } else {
            if (memoryImage.getDrawable() != null) {
                Bitmap bitmap = ((BitmapDrawable) memoryImage.getDrawable()).getBitmap();

                ContentValues values = new ContentValues();
                values.put(MediaStore.Images.Media.DISPLAY_NAME, date+".jpg");
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

    private void showMemories(){

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference reference = database.getReference("Session");
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser user = firebaseAuth.getCurrentUser();
        String uid = user.getUid();
        myMemoriesPictures = new ArrayList<>();
        myMemoriesComments = new ArrayList<>();


        reference.orderByChild("user_id").equalTo(uid).addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String session_id = snapshot.child("session_id").getValue(String.class);
                    String session_duration = snapshot.child("session_duration").getValue(String.class);
                    String activity_id = snapshot.child("activity_id").getValue(String.class);
                    String session_day = snapshot.child("session_day").getValue(String.class);
                    String session_month = snapshot.child("session_month").getValue(String.class);
                    String session_year = snapshot.child("session_year").getValue(String.class);
                    String session_done = snapshot.child("session_done").getValue(String.class);
                    String session_image = snapshot.child("session_picture").getValue(String.class);
                    String session_comment = snapshot.child("session_comment").getValue(String.class);

                    LocalDate sessionDate = null; //todo vérfiier le null
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                        assert session_year != null;
                        assert session_month != null;
                        assert session_day != null;
                        sessionDate = LocalDate.of(Integer.parseInt(session_year), Integer.parseInt(session_month), Integer.parseInt(session_day));
                    }

                    assert session_done != null;
                    DatabaseReference refActivity = database.getReference("Activity");
                    LocalDate finalSessionDate = sessionDate;
                    refActivity.orderByChild("activity_name").equalTo(activity_name).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                                String activityId = childSnapshot.getKey();
                                if (session_done.equals("TRUE")) {
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                        if (date.getMonth() == finalSessionDate.getMonth() &&
                                                date.getDayOfMonth() == finalSessionDate.getDayOfMonth() &&
                                                date.getYear() == finalSessionDate.getYear()) {
                                            assert activityId != null;
                                            if (activityId.equals(activity_id)) {
                                                assert activityId != null;
                                                if (activityId.equals(activity_id)) {
                                                    myMemoriesPictures.add(session_image);
                                                    myMemoriesComments.add(session_comment);

                                                    buttons();
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            // Gérer les erreurs de lecture de la base de données ici
                        }
                    });

                }




            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w("TAG", "Erreur lors de la récupération des données", databaseError.toException());
            }
        });

    }

    public void buttons(){
        int cornerRadius = 40; // en pixels

        leftArrow.setVisibility(View.VISIBLE);
        rightArrow.setVisibility(View.VISIBLE);

        RequestOptions requestOptions = new RequestOptions()
                .transform(new RoundedCorners(cornerRadius));

        Glide.with(DateMemory.this)
                .load(myMemoriesPictures.get(memoriesIndex))
                .apply(requestOptions)
                .into(memoryImage);

        memoryComment.setText(myMemoriesComments.get(memoriesIndex));

        if (myMemoriesPictures.size()==1){
            leftArrow.setVisibility(View.GONE);
            rightArrow.setVisibility(View.GONE);
        }

        rightArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (memoriesIndex== myMemoriesPictures.size()-1)
                    memoriesIndex=0;
                else memoriesIndex++;

                Glide.with(DateMemory.this)
                        .load(myMemoriesPictures.get(memoriesIndex))
                        .apply(requestOptions)
                        .into(memoryImage);
                memoryComment.setText(myMemoriesComments.get(memoriesIndex));


            }
        });

        leftArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (memoriesIndex==0)
                    memoriesIndex= myMemoriesPictures.size()-1;
                else memoriesIndex--;

                Glide.with(DateMemory.this)
                        .load(myMemoriesPictures.get(memoriesIndex))
                        .apply(requestOptions)
                        .into(memoryImage);
                memoryComment.setText(myMemoriesComments.get(memoriesIndex));

            }
        });

        download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                savePhotoToGallery();
            }
        });

    }
}