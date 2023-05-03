package com.example.hobbyzooapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class endSession extends AppCompatActivity {



    private static final int RETOUR_PRENDRE_PHOTO =1;
    ImageView petPic;
    Button takeApic;
    ImageView takenImage;
    private String photoPath =null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_end_session);
        petPic = findViewById(R.id.petPicture);
        petPic.setImageResource(R.drawable.koala);
        takeApic=findViewById(R.id.takeAPic);
        takenImage =findViewById(R.id.takenImage);

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


    }

    private void takePicture() throws IOException {
        Intent intent=new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        if (intent.resolveActivity(getPackageManager())!=null){
            String date = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            File photoDir =getExternalFilesDir(Environment.DIRECTORY_PICTURES);
            File photoFile= File.createTempFile("Session of "+date,".jpg",photoDir);
            photoPath =photoFile.getAbsolutePath();
                Uri photoUri = FileProvider.getUriForFile(endSession.this,
                        endSession.this.getApplicationContext().getOpPackageName()+".provider",
                        photoFile);
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
        takeApic.setVisibility(View.GONE);
        petPic.setImageResource(R.drawable.koa);




    }
}