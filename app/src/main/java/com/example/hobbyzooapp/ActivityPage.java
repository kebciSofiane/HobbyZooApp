package com.example.hobbyzooapp;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

public class ActivityPage extends AppCompatActivity {

    ImageView petPic;
    TextView petName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_page);
        petPic =findViewById(R.id.activityPagePetPic);
        petName = findViewById(R.id.activityPagePetName);
        petName.setText("Coco");
        petPic.setImageResource(R.drawable.koa);

        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.koa);
        /*
        int newWidth = (int) (bitmap.getWidth() * (70 / 100.0));
        int newHeight = (int) (bitmap.getHeight() * (70 / 100.0));
        Bitmap resizedBitmap = Bitmap.createScaledBitmap(bitmap, newWidth, newHeight, false);
        petPic.setScaleType(ImageView.ScaleType.CENTER_CROP);*/

        petPic.setImageBitmap(bitmap);

    }

}