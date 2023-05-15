package com.example.hobbyzooapp;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.hobbyzooapp.Activities.ActivityPage;
import com.example.hobbyzooapp.Activities.MyActivities;
import com.example.hobbyzooapp.Calendar.CalendarActivity;
import com.example.hobbyzooapp.Sessions.RunSession;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.Random;

public class HomeActivity extends AppCompatActivity {

    FirebaseAuth firebaseAuth;

    ActionBar actionBar;

    ImageView fenceImage;

    Button next;



    ImageButton calendarBtn, runBtn, profileBtn;

    ImageView imageView1;
    ImageView imageView2;
    ImageView imageView3;
    ImageView imageView4;
    ImageView imageView5;

    TextView textView1;
    TextView textView2;
    TextView textView3;
    TextView textView4;
    TextView textView5;

    LinearLayout linearLayout1;
    LinearLayout linearLayout2 ;
    LinearLayout linearLayout3 ;
    LinearLayout linearLayout4 ;
    LinearLayout linearLayout5 ;




    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        firebaseAuth = FirebaseAuth.getInstance();

        next = findViewById(R.id.scrollAnimals);


        ArrayList<Integer> imageList = new ArrayList<>();
        imageList.add(R.drawable.lion);
        imageList.add(R.drawable.koa);
        //imageList.add(R.drawable.singe);
        imageList.add(R.drawable.cat);
        imageList.add(R.drawable.chick);
        imageList.add(R.drawable.beaver);
        imageList.add(R.drawable.cow);
        imageList.add(R.drawable.rabbit);
        imageList.add(R.drawable.tl);
        imageList.add(R.drawable.tiger);


        showAnimals(imageList);



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
                startActivity(new Intent(HomeActivity.this, RunSession.class));

            }
        });
        profileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(HomeActivity.this,MyActivities.class));

            }
        });

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAnimals(imageList);
            }
        });


        imageView1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               startActivity(new Intent(HomeActivity.this, ActivityPage.class));
            }
        });

        imageView2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(HomeActivity.this, ActivityPage.class));

            }
        });

        imageView3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(HomeActivity.this,ActivityPage.class));

            }
        });

        imageView4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent =new Intent(HomeActivity.this,ActivityPage.class);
                startActivity(intent);
            }
        });

        imageView5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(HomeActivity.this,ActivityPage.class));

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

    private void showAnimals(ArrayList<Integer> imageList){
        imageView1 = findViewById(R.id.imageView1);
        imageView2 = findViewById(R.id.imageView2);
        imageView3 = findViewById(R.id.imageView3);
        imageView4 = findViewById(R.id.imageView4);
        imageView5 = findViewById(R.id.imageView5);

        textView1 = findViewById(R.id.homePageAnimalText1);
        textView2 = findViewById(R.id.homePageAnimalText2);
        textView3 = findViewById(R.id.homePageAnimalText3);
        textView4 = findViewById(R.id.homePageAnimalText4);
        textView5 = findViewById(R.id.homePageAnimalText5);

        linearLayout1 = findViewById(R.id.linearLayoutHomePageAnimal1);
        linearLayout2 = findViewById(R.id.linearLayoutHomePageAnimal2);
        linearLayout3 = findViewById(R.id.linearLayoutHomePageAnimal3);
        linearLayout4 = findViewById(R.id.linearLayoutHomePageAnimal4);
        linearLayout5 = findViewById(R.id.linearLayoutHomePageAnimal5);


        //FirebaseUser user = firebaseAuth.getCurrentUser();
        //user.getUid()
        textView1.setText("Dessin");
        textView2.setText("Muscu");
        textView3.setText("Dance");
        textView4.setText("DIY");
        textView5.setText("Yoga");


Random random = new Random();


        imageView1.setImageResource(imageList.get(random.nextInt(imageList.size()-1)));
        imageView2.setImageResource(imageList.get(random.nextInt(imageList.size()-1)));
        imageView3.setImageResource(imageList.get(random.nextInt(imageList.size()-1)));
        imageView4.setImageResource(imageList.get(random.nextInt(imageList.size()-1)));
        imageView5.setImageResource(imageList.get(random.nextInt(imageList.size()-1)));



        // Obtenir le gestionnaire de fenêtres
        WindowManager windowManager = getWindowManager();

        // Obtenir les métriques d'affichage
        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((WindowManager) windowManager).getDefaultDisplay().getMetrics(displayMetrics);

        // Obtenir la largeur de l'écran
        int surfaceWidth = displayMetrics.widthPixels;

        // Obtenir la hauteur de l'écran
        int surfaceHeight = displayMetrics.heightPixels;

        int cellHeight= surfaceHeight/5;
        int cellWidth = surfaceWidth/5;

        ArrayList<int[]> cellList = new ArrayList<>();

        for (int i=0; i<5;i++)
            for (int j=0; j<4;j++)
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
        linearLayout1.setLayoutParams(params1);

        RelativeLayout.LayoutParams params2 = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);
        params2.leftMargin = image2X;
        params2.topMargin = image2Y;
        linearLayout2.setLayoutParams(params2);

        RelativeLayout.LayoutParams params3 = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);
        params3.leftMargin = image3X;
        params3.topMargin = image3Y;
        linearLayout3.setLayoutParams(params3);

        RelativeLayout.LayoutParams params4 = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);
        params4.leftMargin = image4X;
        params4.topMargin = image4Y;
        linearLayout4.setLayoutParams(params4);

        RelativeLayout.LayoutParams params5 = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);
        params5.leftMargin = image5X;
        params5.topMargin = image5Y;
        linearLayout5.setLayoutParams(params5);
    }

}
