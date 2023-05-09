package com.example.hobbyzooapp;

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

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class endSession extends AppCompatActivity {



    private static final int RETOUR_PRENDRE_PHOTO =1;
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
    private String photoPath =null;
    private static final int REQUEST_WRITE_EXTERNAL_STORAGE = 1;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_end_session);
        petPic = findViewById(R.id.petPicture);
        petPic.setImageResource(R.drawable.koala);
        takeApic=findViewById(R.id.takeAPic);
        takenImage =findViewById(R.id.takenImage);
        commentField =findViewById(R.id.commentText);
        validateButton = findViewById(R.id.validateButton);
        skipButton=findViewById(R.id.skipButton);
        commentValidated = findViewById(R.id.commentValidated);
        validateButton2 = findViewById(R.id.validateButton2);
        sessionCount= findViewById(R.id.sessionCount);
        modifyPicButton=findViewById(R.id.ModifyPicButton);
        modifyCommentButton = findViewById(R.id.ModifyCommentButton);

        updateSessionCount();

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
                endSession();
            }
        });

        skipButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                endSession();
            }
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
        Intent intent=new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        if (intent.resolveActivity(getPackageManager())!=null){
            String date = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            File photoDir =getExternalFilesDir(Environment.DIRECTORY_PICTURES);
            File photoFile= File.createTempFile("Session of "+date,".jpg",photoDir);
            photoPath =photoFile.getAbsolutePath();
            Uri photoUri = null;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
                photoUri = FileProvider.getUriForFile(endSession.this,
                        endSession.this.getApplicationContext().getOpPackageName()+".provider",
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
        petPic.setImageResource(R.drawable.koa);
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
        long totalSessionTime = RunSession.totalSessionTime;
        long hours = TimeUnit.MILLISECONDS.toHours(totalSessionTime);
        long minutes = TimeUnit.MILLISECONDS.toMinutes(totalSessionTime - TimeUnit.HOURS.toMillis(hours));
        sessionCount.setText(hours+"h:"+minutes+"mn/10h:30mn");
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

    private void endSession(){
        Intent intent = new Intent(endSession.this, ActivityPage.class);
        startActivity(intent);
    }

}