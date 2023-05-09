package com.example.hobbyzooapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.InputFilter;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class ActivityPage extends AppCompatActivity {

    ImageView petPic;
    TextView petName;
    Button editNamePetButton;
    EditText editTextPetName;
    Button validatePetNAme;
    Button showMoreButton;
    Button showLessButton;
    RecyclerView recyclerView;
    TextView goalsText;
    List<String> items = new ArrayList<>();
    listSessionsAdapter adapter;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_page);
        items.addAll(List.of("5 juin à 13h00 - 15 min","7 juin à 13h00 - 15 min","13 juin à 13h00 - 15 min",
                "5 juin à 13h00 - 15 min","5 juin à 13h00 - 15 min","5 juin à 13h00 - 15 min",
                "5 juin à 13h00 - 15 min","7 juin à 13h00 - 15 min","16 juillet à 13h00 - 15 min",
                "5 juin à 13h00 - 15 min","5 juin à 13h00 - 15 min","5 juin à 13h00 - 15 min"));

        petPic =findViewById(R.id.activityPagePetPic);
        petName = findViewById(R.id.activityPagePetName);
        petName.setText("Coco");
        petPic.setImageResource(R.drawable.koa);
        showMoreButton=findViewById(R.id.activityPageShowMoreButton);
        showLessButton = findViewById(R.id.activityPageShowLessButton);
        editNamePetButton=findViewById(R.id.activityPageEditPetNameButton);
        goalsText =findViewById(R.id.activityPageGoalsText);
        goalsText.setText("Goal: 2h/5h");
        recyclerView=findViewById(R.id.activityPageRecyclerView);
        GridLayoutManager layoutManager = new GridLayoutManager(this, 5, GridLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new listSessionsAdapter(items);
        recyclerView.setAdapter(adapter);









        editTextPetName = findViewById(R.id.activityPagePetNameEdit);
        InputFilter[] filters = new InputFilter[1];
        filters[0] = new InputFilter.LengthFilter(10);
        editTextPetName.setFilters(filters);

        validatePetNAme = findViewById(R.id.activityPagecheckPetNameButton);
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.koa);
        /*
        int newWidth = (int) (bitmap.getWidth() * (70 / 100.0));
        int newHeight = (int) (bitmap.getHeight() * (70 / 100.0));
        Bitmap resizedBitmap = Bitmap.createScaledBitmap(bitmap, newWidth, newHeight, false);
        petPic.setScaleType(ImageView.ScaleType.CENTER_CROP);*/

        petPic.setImageBitmap(bitmap);

        editNamePetButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                editTextPetName.setText(petName.getText());
                editTextPetName.setVisibility(View.VISIBLE);
                editNamePetButton.setVisibility(View.GONE);
                validatePetNAme.setVisibility(View.VISIBLE);
                petName.setVisibility(View.GONE);
            }
        });

        showMoreButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                adapter.setExpanded(!adapter.isExpanded());
                adapter.notifyDataSetChanged();
                showLessButton.setVisibility(View.VISIBLE);
                showMoreButton.setVisibility(View.GONE);

            }
        });

        showLessButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                adapter.setExpanded(!adapter.isExpanded());
                adapter.notifyDataSetChanged();
                showMoreButton.setVisibility(View.VISIBLE);
                showLessButton.setVisibility(View.GONE);


            }
        });

        validatePetNAme.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                editTextPetName.setVisibility(View.GONE);
                String newPetName = String.valueOf(editTextPetName.getText());;
                petName.setText(newPetName);
                editNamePetButton.setVisibility(View.VISIBLE);
                validatePetNAme.setVisibility(View.GONE);
                petName.setVisibility(View.VISIBLE);
            }
        });



    }

}