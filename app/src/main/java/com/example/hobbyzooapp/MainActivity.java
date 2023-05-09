package com.example.hobbyzooapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.GridView;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import java.util.Random;

import com.example.hobbyzooapp.Activities.Activity;
import com.example.hobbyzooapp.Activities.MyActivities;
import com.example.hobbyzooapp.Sessions.CalendarSessions;
import com.example.hobbyzooapp.Sessions.MyDailySessions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private Button sessionButton;
    private Button activitiesButton;

    private LinearLayout linearLayout;
    private HorizontalScrollView horizontalScrollView;
    private ArrayList<Activity> myObjects;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sessionButton = findViewById(R.id.session_button);
        sessionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openMyDailySessions();
            }
        });


        activitiesButton = findViewById(R.id.activities_button);
        activitiesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openMyActivities();
            }
        });

/*
        linearLayout = findViewById(R.id.linear_layout);
        horizontalScrollView = findViewById(R.id.horizontal_scroll_view);

        // Initialisation de la liste d'objets
        myObjects = new ArrayList<>();
        myObjects.add(new Activity("Objet 1", R.drawable.img1));
        myObjects.add(new Activity("Objet 2", R.drawable.img2));
        myObjects.add(new Activity("Objet 3", R.drawable.img3));


        // Nombre maximum d'objets par page
        int maxObjectsPerPage = 6;

        // Nombre de pages
        int nbPages = (int) Math.ceil((double) myObjects.size() / maxObjectsPerPage);

        // Pour chaque page
        for (int i = 0; i < nbPages; i++) {
            // Création d'un LinearLayout horizontal pour chaque page
            LinearLayout linearLayoutHorizontal = new LinearLayout(this);
            linearLayoutHorizontal.setOrientation(LinearLayout.HORIZONTAL);
            linearLayout.addView(linearLayoutHorizontal);

            // Liste d'objets pour cette page
            ArrayList<Activity> objectsForPage = new ArrayList<>();

            // Ajout des objets dans la liste pour cette page
            for (int j = i * maxObjectsPerPage; j < (i + 1) * maxObjectsPerPage && j < myObjects.size(); j++) {
                objectsForPage.add(myObjects.get(j));
            }

            // Placement aléatoire des objets sur cette page
            Collections.shuffle(objectsForPage);

// Pour chaque objet dans la liste
            for (MyObject obj : objectsForPage) {
                // Création d'un nouveau layout pour l'objet
                LayoutInflater inflater = LayoutInflater.from(this);
                LinearLayout objectLayout = (LinearLayout) inflater.inflate(R.layout.item_object, linearLayoutHorizontal, false);

                // Récupération des vues du layout
                ImageView imageView = objectLayout.findViewById(R.id.image_view);
                TextView textViewName = objectLayout.findViewById(R.id.text_view_name);

                // Affichage de l'image et du nom de l'objet
                imageView.setImageResource(obj.getImg());
                textViewName.setText(obj.getName());

                // Placement aléatoire de l'objet sur cette page
                int randomX = (int) (Math.random() * (horizontalScrollView.getWidth() - imageView.getWidth()));
                int randomY = (int) (Math.random() * (horizontalScrollView.getHeight() - imageView.getHeight()));
                objectLayout.setX(randomX);
                objectLayout.setY(randomY);

                // Vérification qu'il n'y ait pas de superposition avec les objets déjà placés
                boolean collisionDetected;

            }
        }*/
    }

    public void openMyDailySessions() {
        Intent intent = new Intent(this, CalendarSessions.class); //vers le calendrier
        startActivity(intent);
    }

    public void openMyActivities() {
        Intent intent = new Intent(this, MyActivities.class);
        startActivity(intent);
    }


}










