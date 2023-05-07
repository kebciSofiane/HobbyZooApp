package com.example.hobbyzooapp.new_activities;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.hobbyzooapp.Category;
import com.example.hobbyzooapp.R;


public class NewCategory extends AppCompatActivity {

    String name;
    int color = 0;
    ImageView imgView;
    TextView mColorValues;
    View displayColors;
    Bitmap bitmap;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_category);

        Button validationButton = findViewById(R.id.validationButton);
        imgView = findViewById(R.id.colorPickers);
        displayColors = findViewById(R.id.displayColors);

        imgView.setDrawingCacheEnabled(true);
        imgView.buildDrawingCache(true);

        imgView.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN || event.getAction() == MotionEvent.ACTION_MOVE){
                    bitmap = imgView.getDrawingCache();
                    int pixels = bitmap.getPixel((int)event.getX(), (int)event.getY());

                    int r = Color.red(pixels);
                    int b = Color.blue(pixels);
                    int g = Color.green(pixels);

                    displayColors.setBackgroundColor(Color.rgb(r,g,b));
                    color = Color.rgb(r,g,b);

                }
                return true;
            }
        });
        validationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                name = findViewById(R.id.activityName).toString();
                if(name.trim().isEmpty()){
                    Toast.makeText(getApplicationContext(),"Le champ nom ne peut pas être vide!",Toast.LENGTH_LONG).show();
                }
                else if(color == 0){
                    Toast.makeText(getApplicationContext(),"Il faut choisir une couleur!",Toast.LENGTH_LONG).show();
                }
                else{
                    Category category = new Category(name, color);
                    finish();
                }
            }
        });

    }
}