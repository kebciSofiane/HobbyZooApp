package com.example.hobbyzooapp.Category;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.hobbyzooapp.Activities.NewActivity;
import com.example.hobbyzooapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.hdodenhof.circleimageview.CircleImageView;


public class NewCategory extends AppCompatActivity {

    String name, user_id, category_id, colorHex;
    int colorRGB = 0;
    int red, blue, green;
    CircleImageView colorPicker;
    View displayColors;
    Bitmap bitmap;
    FirebaseAuth firebaseAuth;
    ImageView validationButton, returnButton;
    String regexPattern = "^[a-zA-Z0-9 ]+$";
    Pattern pattern;


    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_category);
        initialisation();

        colorPicker.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN || event.getAction() == MotionEvent.ACTION_MOVE){
                float x = event.getX();
                float y = event.getY();

                float centerX = colorPicker.getWidth() / 2f;
                float centerY = colorPicker.getHeight() / 2f;
                float radius = colorPicker.getWidth() / 2f;

                if (Math.pow(x - centerX, 2) + Math.pow(y - centerY, 2) <= Math.pow(radius, 2)) {
                    colorPicker.setDrawingCacheEnabled(true);
                    colorPicker.buildDrawingCache(true);
                    bitmap = colorPicker.getDrawingCache();

                    int pixels = bitmap.getPixel((int) x, (int) y);

                    red = Color.red(pixels);
                    blue = Color.blue(pixels);
                    green = Color.green(pixels);

                    displayColors.setBackgroundColor(Color.rgb(red, green, blue));
                    colorRGB = Color.rgb(red, green, blue);
                    colorHex = "#" + Integer.toHexString(red) + Integer.toHexString(green) + Integer.toHexString(blue);
                    try {
                        int color = Color.parseColor(colorHex);
                    } catch (IllegalArgumentException e) {
                        colorHex = "#8E7F6F";
                    }
                }
                if(colorHex.equals("#FFFFFF") || colorHex.equals("#000000")){
                    colorHex = "#606060";
                }
            }

            return true;
        });

        returnButton.setOnClickListener(view -> {
            startActivity(new Intent(NewCategory.this, NewActivity.class));
            finish();
        });

        validationButton.setOnClickListener(view -> {
            EditText text = findViewById(R.id.categoryName);
            name = text.getText().toString();
            Matcher matcherCategoryName = pattern.matcher(name);
            if(name.trim().isEmpty() || colorRGB == 0){
                Toast.makeText(getApplicationContext(),"Field can't be empty!!",Toast.LENGTH_LONG).show();
            }
            else if(!matcherCategoryName.matches()){
                Toast.makeText(getApplicationContext(),"Name fields can't have special characters!",Toast.LENGTH_LONG).show();
            }
            else if(name.length() > 15)
                Toast.makeText(getApplicationContext(),"Name fields can't have more then 15 characters!",Toast.LENGTH_LONG).show();
            else {
                List<String> categories = new ArrayList<>();
                DatabaseReference databaseReferenceChild = FirebaseDatabase.getInstance().getReference().child("Category");
                databaseReferenceChild.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            String userId = snapshot.child("user_id").getValue(String.class);
                            String activityName = snapshot.child("category_name").getValue(String.class);
                            if(userId.equals(user_id) && activityName.equals(name))
                                categories.add(name);
                        }
                        if(categories.size() == 0){
                            addDBCategory();
                            Intent intent = new Intent(NewCategory.this, NewActivity.class);
                            intent.putExtra("category_name", name.replace(",", " "));
                            intent.putExtra("category_id", category_id);
                            startActivity(intent);
                            finish();
                        }
                        else{
                            Toast.makeText(getApplicationContext(),"This Category already exists!",Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                    }
                });
            }
        });

    }

    private void initialisation() {
        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser user = firebaseAuth.getCurrentUser();
        user_id = user.getUid();
        validationButton = findViewById(R.id.validationButton);
        returnButton = findViewById(R.id.returnButton);
        colorPicker = findViewById(R.id.colorPickers);
        displayColors = findViewById(R.id.displayColors);
        colorPicker.setDrawingCacheEnabled(true);
        colorPicker.buildDrawingCache(true);
        pattern = Pattern.compile(regexPattern);
    }

    @Override
    public void onBackPressed() {}
    private void addDBCategory(){
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        DatabaseReference newChildRef = databaseReference.push();
        category_id = newChildRef.getKey();
        HashMap<Object, String> hashMap = new HashMap<>();
        hashMap.put("category_id", category_id);
        hashMap.put("category_name", name.replace(" ", ","));
        hashMap.put("category_color", colorHex);
        hashMap.put("user_id", user_id);

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference reference = database.getReference("Category");
        reference.child(category_id).setValue(hashMap);
    }

}