package com.example.hobbyzooapp;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
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
import com.example.hobbyzooapp.Calendar.CalendarActivity;
import com.example.hobbyzooapp.Calendar.CalendarUtils;
import com.example.hobbyzooapp.Sessions.MyDailySessions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Random;

public class HomeActivity extends AppCompatActivity {
    //TODO 1 général : vérifier les boutons retour du tel de chaque activity + ajout finish() si besoin
    //TODO 2 général : enlever tous system out + commentaires inutiles + verif indentation
    //todo : panel button revien page 1 sans relancer la page ?(phone back button aussi)

    FirebaseAuth firebaseAuth;

    private int currentIndex=0;
    private int currentIndex2=0;
    private int currentIndex3=0;

    ImageButton calendarBtn, runBtn, profileBtn, panelHobbyZoo;
    Button next, previous;
    ImageView imageView1, imageView2, imageView3, imageView4, imageView5;
    TextView textView1, textView2, textView3, textView4, textView5;
    LinearLayout linearLayout1, linearLayout2, linearLayout3, linearLayout4, linearLayout5;

    ArrayList<Integer> imageList = new ArrayList<>();
    ArrayList<String> activities_name_List = new ArrayList<>();
    ArrayList<String> activities_id_List = new ArrayList<>();

    public static ArrayList<String> animalsFeeling = new ArrayList<>(Arrays.asList("gone", "sad", "angry", "neutral", "happy"));
    String uid;
    int startIndex=0;
    Boolean toRight;

    @SuppressLint("MissingInflatedId")
    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        initialisation();
        getActivities();

        panelHobbyZoo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentIndex=0;
                currentIndex2=0;
                currentIndex3=0;
                showAnimals();
            }
        });

        calendarBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(HomeActivity.this,CalendarActivity.class));
            }
        });

        runBtn.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View v) {
                CalendarUtils.selectedDate= LocalDate.now();
                startActivity(new Intent(HomeActivity.this, MyDailySessions.class));

            }
        });

        profileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(HomeActivity.this,ProfileActivity.class));
            }
        });

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toRight=true;
                showAnimals();
            }
        });

        previous.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toRight=false;
                showAnimals();
            }
        });

        linearLayout1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(HomeActivity.this, ActivityPage.class);
                intent.putExtra("activity_id",(String) v.getTag());
                startActivity(intent);
            }
        });

        linearLayout2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(HomeActivity.this, ActivityPage.class);
                intent.putExtra("activity_id",(String) v.getTag());
                startActivity(intent);
            }
        });

        linearLayout3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(HomeActivity.this, ActivityPage.class);
                intent.putExtra("activity_id",(String) v.getTag());
                startActivity(intent);            }
        });

        linearLayout4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(HomeActivity.this, ActivityPage.class);
                intent.putExtra("activity_id",(String) v.getTag());
                startActivity(intent);
            }
        });

        linearLayout5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(HomeActivity.this, ActivityPage.class);
                intent.putExtra("activity_id",(String) v.getTag());
                startActivity(intent);
            }
        });
    }

    private void initialisation() {
        firebaseAuth = FirebaseAuth.getInstance();
        next = findViewById(R.id.scrollAnimalsRight);
        previous = findViewById(R.id.scrollAnimalsLeft);
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
        panelHobbyZoo = findViewById(R.id.panel_hobby_zoo);
        calendarBtn = findViewById(R.id.calendar_btn);
        runBtn = findViewById(R.id.run_btn);
        profileBtn = findViewById(R.id.profile_btn);
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
    public void onBackPressed() {}
/*
   //TODO ca sert a rien ca ???

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
    */

    public List<Integer> showNextActivities(int batchSize) {
        List<Integer> intElements ;

        int startIndex = currentIndex;
        int endIndex = startIndex + batchSize;

        if (endIndex > imageList.size()) {
            endIndex = imageList.size();
        }
        else{
            currentIndex = endIndex;
        }
        intElements = imageList.subList(startIndex, endIndex);
        //currentIndex = endIndex;

        return intElements;
    }

    public List<Integer> showPreviousActivities(int batchSize) {
        List<Integer> intElements ;

        int endIndex = currentIndex;
        int startIndex = endIndex - batchSize;

        if (startIndex < 0) {
            startIndex = 0;
            currentIndex = imageList.size() - 1;
        }

        intElements = imageList.subList(startIndex, endIndex);
        currentIndex = startIndex;

        return intElements;
    }

    public List<String> showNextActivityNames(int batchSize) {
        List<String> intElements ;

        int startIndex = currentIndex2;
        int endIndex = startIndex + batchSize;

            if (endIndex > activities_name_List.size()) {
                endIndex = activities_name_List.size();
            //currentIndex2 = 0;
            } else { currentIndex2 = endIndex; }

        intElements = activities_name_List.subList(startIndex, endIndex);
        ///currentIndex2 = endIndex;

        return intElements;
    }

    public List<String> showPreviousActivityNames(int batchSize) {
        List<String> intElements = new ArrayList<>();

        int endIndex = currentIndex2;
        int startIndex = endIndex - batchSize;

        if (startIndex < 0) {
            startIndex = 0;
            currentIndex2 = activities_name_List.size() - 1;
        }

        intElements = activities_name_List.subList(startIndex, endIndex);
        currentIndex2 = startIndex;

        return intElements;
    }

    public List<String> showNextActivityId(int batchSize) {
        List<String> intElements = new ArrayList<>();

        int startIndex = currentIndex3;
        int endIndex = startIndex + batchSize;

        if (endIndex > activities_id_List.size()) {
            endIndex = activities_id_List.size();
            //currentIndex = ;
        } else { currentIndex3 = endIndex; }

        intElements = activities_id_List.subList(startIndex, endIndex);
        //currentIndex3 = endIndex;

        return intElements;
    }

    public List<String> showPreviousActivityId(int batchSize) {
        List<String> intElements = new ArrayList<>();

        int endIndex = currentIndex3;
        int startIndex = endIndex - batchSize;

        if (startIndex < 0) {
            startIndex = 0;
            currentIndex3 = activities_id_List.size() - 1;
        }

        intElements = activities_id_List.subList(startIndex, endIndex);
        currentIndex3 = startIndex;

        return intElements;
    }

///////////////////////

    public void getActivities(){
        FirebaseUser user = firebaseAuth.getCurrentUser();
        String email = user.getEmail();
        uid = user.getUid();
        FirebaseDatabase database = FirebaseDatabase.getInstance();

        DatabaseReference referenceActivity = database.getReference("Activity");
        referenceActivity.orderByChild("user_id").equalTo(uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String activity_id = snapshot.child("activity_id").getValue(String.class);
                    String activity_pet = snapshot.child("activity_pet").getValue(String.class);
                    String activity_name = snapshot.child("activity_name").getValue(String.class).replace(",", " ");
                    int feelingPointer = Integer.parseInt(Objects.requireNonNull(snapshot.child("feeling").getValue(String.class)));
                    String resourceName;
                    if(feelingPointer == 0)
                        resourceName = "none_whole_gone";
                    else
                        resourceName = activity_pet + "_whole_" + animalsFeeling.get(feelingPointer);
                    int resId = HomeActivity.this.getResources().getIdentifier(resourceName,"drawable",HomeActivity.this.getPackageName());
                    imageList.add(resId);
                    activities_name_List.add(activity_name);
                    activities_id_List.add(activity_id);
                }
                toRight=true;
                showAnimals();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w("TAG", "Erreur lors de la récupération des données", databaseError.toException());
            }
        });
    }

    private void showAnimals() {

        Random random = new Random();

        //FirebaseUser user = firebaseAuth.getCurrentUser();
        //user.getUid()
        ArrayList<TextView> textViewList = new ArrayList<>();
        ArrayList<ImageView> imageViewList = new ArrayList<>();
        ArrayList<LinearLayout> linearLayoutList = new ArrayList<>();

        textViewList.add(textView1);
        textViewList.add(textView2);
        textViewList.add(textView3);
        textViewList.add(textView4);
        textViewList.add(textView5);

        imageViewList.add(imageView1);
        imageViewList.add(imageView2);
        imageViewList.add(imageView3);
        imageViewList.add(imageView4);
        imageViewList.add(imageView5);

        linearLayoutList.add(linearLayout1);
        linearLayoutList.add(linearLayout2);
        linearLayoutList.add(linearLayout3);
        linearLayoutList.add(linearLayout4);
        linearLayoutList.add(linearLayout5);

        for (int i = startIndex; i < 5; i++) {
            linearLayoutList.get(i).setVisibility(View.GONE);
        }

        int batchSize = 5;
        List<Integer> batchElements;
        List<String> activities_names ;
        List<String> activities_id ;
        if (toRight){
            batchElements = showNextActivities(batchSize);
            activities_names = showNextActivityNames(batchSize);
            activities_id = showNextActivityId(batchSize);
        } else {
            batchElements = showPreviousActivities(batchSize);
            activities_names = showPreviousActivityNames(batchSize);
            activities_id = showPreviousActivityId(batchSize);
        }

        for (int i = 0; i < batchElements.size(); i++) {
                imageViewList.get(i).setImageResource(batchElements.get(i));
                textViewList.get(i).setText(activities_names.get(i));
                linearLayoutList.get(i).setVisibility(View.VISIBLE);
                linearLayoutList.get(i).setTag(activities_id.get(i));
        }

        // Obtenir le gestionnaire de fenêtres
            WindowManager windowManager = getWindowManager();

            // Obtenir les métriques d'affichage
            DisplayMetrics displayMetrics = new DisplayMetrics();
            ((WindowManager) windowManager).getDefaultDisplay().getMetrics(displayMetrics);

            // Obtenir la largeur de l'écran
            int surfaceWidth = displayMetrics.widthPixels;

            // Obtenir la hauteur de l'écran
            int surfaceHeight = displayMetrics.heightPixels;

            int cellHeight = surfaceHeight / 5;
            int cellWidth = surfaceWidth / 5;

            ArrayList<int[]> cellList = new ArrayList<>();

            for (int i = 0; i < 5; i++)
                for (int j = 0; j < 4; j++) {
                    int[] val = new int[2];
                    val[0] = i * cellWidth;
                    val[1] = j * cellHeight;
                    cellList.add(val);
                }

            System.out.println(cellList);

            // Générez les coordonnées aléatoires pour chaque ImageView

            int image1X = 0;
            int image1Y = 0;
            int val = random.nextInt(cellList.size() - 1);
            image1X = cellList.get(val)[0];
            image1Y = cellList.get(val)[1];
            cellList.remove(val);

            int image2X = 0;
            int image2Y = 0;
            val = random.nextInt(cellList.size() - 1);
            image2X = cellList.get(val)[0];
            image2Y = cellList.get(val)[1];
            cellList.remove(val);

            int image3X = 0;
            int image3Y = 0;
            val = random.nextInt(cellList.size() - 1);
            image3X = cellList.get(val)[0];
            image3Y = cellList.get(val)[1];
            cellList.remove(val);

            int image4X = 0;
            int image4Y = 0;
            val = random.nextInt(cellList.size() - 1);
            image4X = cellList.get(val)[0];
            image4Y = cellList.get(val)[1];
            cellList.remove(val);

            int image5X = 0;
            int image5Y = 0;
            val = random.nextInt(cellList.size() - 1);
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