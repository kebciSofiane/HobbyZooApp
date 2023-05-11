package com.example.hobbyzooapp;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class HomeActivity extends AppCompatActivity {

    FirebaseAuth firebaseAuth;

    ActionBar actionBar;

    ImageView fenceImage;




    ImageButton calendarBtn, runBtn, profileBtn;



    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        actionBar = getSupportActionBar();
        actionBar.setTitle("Profile");

        //firebaseAuth = FirebaseAuth.getInstance();

        ArrayList<Integer> imageList = new ArrayList<>();
        imageList.add(R.drawable.bear);
        imageList.add(R.drawable.fox);
        imageList.add(R.drawable.panda);
        imageList.add(R.drawable.dog_bizarre);
        imageList.add(R.drawable.pig);




        ImageView imageView1 = findViewById(R.id.imageView1);
        ImageView imageView2 = findViewById(R.id.imageView2);
        ImageView imageView3 = findViewById(R.id.imageView3);
        ImageView imageView4 = findViewById(R.id.imageView4);
        ImageView imageView5 = findViewById(R.id.imageView5);

        imageView1.setImageResource(imageList.get(0));
        imageView2.setImageResource(imageList.get(1));
        imageView3.setImageResource(imageList.get(2));
        imageView4.setImageResource(imageList.get(3));
        imageView5.setImageResource(imageList.get(4));


        Random random = new Random();

        // Récupérez les dimensions de la surface prédéfinie
        int surfaceWidth = 800;
        int surfaceHeight =800 ;

        int cellHeight= surfaceHeight/5;
        int cellWidth = surfaceWidth/5;

        ArrayList<int[]> cellList = new ArrayList<>();

        for (int i=0; i<5;i++)
            for (int j=0; j<5;j++)
            {
                int[] val = new int[2];
                val[0] = i*cellWidth;
                val[1] = j*cellHeight;
                cellList.add(val);
            }


        System.out.println(cellList);

        // Générez les coordonnées aléatoires pour chaque ImageView


        int image1X = 0;
        int image1Y = 0;
        int val =random.nextInt(cellList.size()-1);
             image1X = cellList.get(val)[0];
             image1Y = cellList.get(val)[1];
        cellList.remove(val);

        int image2X = 0;
        int image2Y = 0;
        val =random.nextInt(cellList.size()-1);
        image2X = cellList.get(val)[0];
        image2Y = cellList.get(val)[1];
        cellList.remove(val);

        int image3X = 0;
        int image3Y = 0;
        val =random.nextInt(cellList.size()-1);
        image3X = cellList.get(val)[0];
        image3Y = cellList.get(val)[1];
        cellList.remove(val);


        int image4X = 0;
        int image4Y = 0;
        val =random.nextInt(cellList.size()-1);
        image4X = cellList.get(val)[0];
        image4Y = cellList.get(val)[1];
        cellList.remove(val);


        int image5X = 0;
        int image5Y = 0;
        val =random.nextInt(cellList.size()-1);
        image5X = cellList.get(val)[0];
        image5Y = cellList.get(val)[1];
        cellList.remove(val);





        RelativeLayout.LayoutParams params1 = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);
        params1.leftMargin = image1X;



        params1.topMargin = image1Y;
        imageView1.setLayoutParams(params1);

        RelativeLayout.LayoutParams params2 = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);
        params2.leftMargin = image2X;
        params2.topMargin = image2Y;
        imageView2.setLayoutParams(params2);

        RelativeLayout.LayoutParams params3 = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);
        params3.leftMargin = image3X;
        params3.topMargin = image3Y;
        imageView3.setLayoutParams(params3);

        RelativeLayout.LayoutParams params4 = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);
        params4.leftMargin = image4X;
        params4.topMargin = image4Y;
        imageView4.setLayoutParams(params4);

        RelativeLayout.LayoutParams params5 = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);
        params5.leftMargin = image5X;
        params5.topMargin = image5Y;
        imageView5.setLayoutParams(params5);



        // Modifier la taille de l'image1
       params1 = (RelativeLayout.LayoutParams) imageView1.getLayoutParams();
        params1.width = 150;
        params1.height = 150;
        imageView1.setLayoutParams(params1);

// Modifier la taille de l'image2
        params2 = (RelativeLayout.LayoutParams) imageView2.getLayoutParams();
        params2.width = 150;
        params2.height = 150;
        imageView2.setLayoutParams(params2);

// Modifier la taille de l'image3
         params3 = (RelativeLayout.LayoutParams) imageView3.getLayoutParams();
        params3.width = 150;
        params3.height = 150;
        imageView3.setLayoutParams(params3);

// Modifier la taille de l'image4
        params4 = (RelativeLayout.LayoutParams) imageView4.getLayoutParams();
        params4.width = 150;
        params4.height = 150;
        imageView4.setLayoutParams(params4);

// Modifier la taille de l'image5
         params5 = (RelativeLayout.LayoutParams) imageView5.getLayoutParams();
        params5.width = 150;
        params5.height = 150;
        imageView5.setLayoutParams(params5);


        //buttons

        calendarBtn = findViewById(R.id.calendar_btn);
        runBtn = findViewById(R.id.run_btn);
        profileBtn = findViewById(R.id.profile_btn);

        calendarBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(HomeActivity.this,CalendarActivity.class));

            }
        });
        runBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(HomeActivity.this,CalendarActivity.class));

            }
        });
        profileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(HomeActivity.this,CalendarActivity.class));

            }
        });

        imageView1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(HomeActivity.this,CalendarActivity.class));

            }
        });

        imageView2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(HomeActivity.this,CalendarActivity.class));

            }
        });

        imageView3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(HomeActivity.this,CalendarActivity.class));

            }
        });

        imageView4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(HomeActivity.this,CalendarActivity.class));

            }
        });

        imageView5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(HomeActivity.this,CalendarActivity.class));

            }
        });



    }


    private void checkUserStatus(){
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if(user != null){

        }
        else{
            //startActivity(new Intent(HomeActivity.this, RegistrationOrConnexion.class));
            //finish();
        }
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if(id == R.id.action_logout){
            firebaseAuth.signOut();
            checkUserStatus();
        }
        return super.onOptionsItemSelected(item);
    }
}
