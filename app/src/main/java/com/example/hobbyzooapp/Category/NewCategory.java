package com.example.hobbyzooapp.Category;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.example.hobbyzooapp.HomeActivity;
import com.example.hobbyzooapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;


public class NewCategory extends AppCompatActivity {

    String name;
    int colorRGB = 0;
    Color color;
    int red, blue, green;
    ImageView imgView;
    TextView mColorValues;
    View displayColors;
    Bitmap bitmap;
    FirebaseAuth firebaseAuth;


    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_category);
        firebaseAuth = FirebaseAuth.getInstance();

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

                    red = Color.red(pixels);
                    blue = Color.blue(pixels);
                    green = Color.green(pixels);

                    displayColors.setBackgroundColor(Color.rgb(red,green,blue));
                    colorRGB = Color.rgb(red,green,blue);

                }
                return true;
            }
        });


        validationButton.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View view) {
                EditText text = findViewById(R.id.categoryName);
                FirebaseUser user = firebaseAuth.getCurrentUser();
                String colorHex = "#";
                colorHex += Integer.toHexString(red);
                colorHex += Integer.toHexString(green);
                colorHex += Integer.toHexString(blue);

                name = text.getText().toString();
                if(name.trim().isEmpty()){
                    Toast.makeText(getApplicationContext(),"Le champ nom ne peut pas être vide!",Toast.LENGTH_LONG).show();
                }
                else if (colorRGB == 0) {
                    Toast.makeText(getApplicationContext(),"Il faut choisir une couleur!",Toast.LENGTH_LONG).show();
                } else if(user == null){

                }else {
                    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

                    DatabaseReference newChildRef = databaseReference.push();
                    String category_id = newChildRef.getKey();
                    HashMap<Object, String> hashMap = new HashMap<>();


                    hashMap.put("category_id", category_id);
                    hashMap.put("category_name", name);
                    hashMap.put("category_color", colorHex);
                    hashMap.put("user_id", user.getUid());

                    FirebaseDatabase database = FirebaseDatabase.getInstance();

                    DatabaseReference reference = database.getReference("Category");
                    reference.child(category_id).setValue(hashMap);
                    Intent intent = new Intent().setClass(getApplicationContext(), HomeActivity.class);
                    startActivity(intent);
                }
            }
        });

    }
}