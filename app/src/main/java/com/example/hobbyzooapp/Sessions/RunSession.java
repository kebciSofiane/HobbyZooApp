package com.example.hobbyzooapp.Sessions;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;

import com.example.hobbyzooapp.Activities.ActivityPage;
import com.example.hobbyzooapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.sql.Time;
import java.util.Calendar;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class RunSession extends AppCompatActivity {

    private long countDownTime = 5000;
    private long timeLeftInMillis = countDownTime;
    long totalSessionTime=0;
    TextView countdownTextView;
    CountDownTimer countDownTimer;
    ImageButton stopButton;
    ImageButton pauseButton;
    ImageButton resumeButton;
    ImageView petPicture;
    ImageButton validateButton;
    ImageButton addTimeButton;
    FirebaseAuth firebaseAuth;
    String activity_id;
    String session_id;
    String activityPet;
    String session_duration;


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_run_session);
        firebaseAuth = FirebaseAuth.getInstance();

        Intent intent = getIntent();
        activity_id = intent.getStringExtra("activity_id");
        session_id = intent.getStringExtra("session_id");




        FirebaseDatabase database = FirebaseDatabase.getInstance();

        DatabaseReference referenceActivity = database.getReference("Activity");
        referenceActivity.child(activity_id).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    activityPet = dataSnapshot.child("activity_pet").getValue(String.class);
                    petPicture = findViewById(R.id.petPicture);
                    String resourceName = activityPet+"_full_icon";
                    int resId = RunSession.this.getResources().getIdentifier(resourceName,"drawable",RunSession.this.getPackageName());
                    petPicture.setImageResource(resId);
                } else {
                    // L'activité n'existe pas dans la base de données
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Une erreur s'est produite lors de la récupération des données
            }
        });

        DatabaseReference referenceSession = database.getReference("Session");
        referenceSession.child(session_id).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    session_duration = dataSnapshot.child("session_duration").getValue(String.class);
                    countDownTime = (long) Integer.parseInt(session_duration) *60*1000;
                    startCountdown();


                } else {
                    // L'activité n'existe pas dans la base de données
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Une erreur s'est produite lors de la récupération des données
            }
        });


        countdownTextView = findViewById(R.id.countdownTextView);
        pauseButton = findViewById(R.id.pauseButton);
        stopButton = findViewById(R.id.stopButton);
        resumeButton=findViewById(R.id.resumeButton);

        validateButton = findViewById(R.id.validateButton);
        addTimeButton = findViewById(R.id.addTimeButton);




        pauseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pauseCountDown();
            }
        });
        resumeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resumeCountDown();
            }
        });


        stopButton.setOnClickListener(new  View.OnClickListener(){
            @Override
            public void onClick(View view) {
                new AlertDialog.Builder(RunSession.this)
                        .setTitle("You are about to close your session !")
                        .setMessage("Do you really want to stop ?")
                        .setNegativeButton(android.R.string.no, null)
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface arg0, int arg1) {
                                totalSessionTime += countDownTime-timeLeftInMillis;
                                countDownTimer.cancel();
                                endSession();
                            }
                        }).create().show();

            }
        });

        validateButton.setOnClickListener(new  View.OnClickListener(){
            @Override
            public void onClick(View view) {
                endSession();
            }
        });

        addTimeButton.setOnClickListener(new  View.OnClickListener(){
            @Override
            public void onClick(View view) {
                showDurationDialog();

            }
        });

    }

    public void showDurationDialog() {
        Calendar calendar = Calendar.getInstance();
        int currentHour = calendar.get(Calendar.HOUR_OF_DAY);
        int currentMinute = calendar.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                long durationMillis  = (hourOfDay * 60 * 60 * 1000) + (minute * 60 * 1000);
                countDownTime = durationMillis;
                startCountdown();
                stopButton.setVisibility(View.VISIBLE);
                pauseButton.setVisibility(View.VISIBLE);
                addTimeButton.setVisibility(View.GONE);
                validateButton.setVisibility(View.GONE);
            }
        }, currentHour, currentMinute, true);
        timePickerDialog.show();
    }



    private void pauseCountDown(){
        countDownTimer.cancel();
        resumeButton.setVisibility(View.VISIBLE);
        pauseButton.setVisibility(View.GONE);
    }
    private void resumeCountDown(){
        countDownTime = timeLeftInMillis;
        startCountdown();
        pauseButton.setVisibility(View.VISIBLE);
        resumeButton.setVisibility(View.GONE);
    }
    private void startCountdown(){
        countDownTimer=new CountDownTimer(countDownTime, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                timeLeftInMillis = millisUntilFinished;
                updateCountdownText();
            }
            @Override
            public void onFinish() {
                totalSessionTime += countDownTime;
                validateButton.setVisibility(View.VISIBLE);
                resumeButton.setVisibility(View.GONE);
                stopButton.setVisibility(View.GONE);
                pauseButton.setVisibility(View.GONE);
                addTimeButton.setVisibility(View.VISIBLE);

                //////////////
                // Créer un LayoutInflater pour inflater le fichier XML
                LayoutInflater inflater = getLayoutInflater();
                View dialogView = inflater.inflate(R.layout.custom_dialog_run_session, null);

                // Référencer les vues dans le fichier XML
                TextView dialogTitle = dialogView.findViewById(R.id.dialogTitle);
                Button dialogButtonAddTime = dialogView.findViewById(R.id.dialogButtonAddTime);
                Button dialogButtonFinish = dialogView.findViewById(R.id.dialogButtonFinish);

                // Définir le titre et le texte du bouton
                //dialogTitle.setText("Session finished");
                //dialogButton.setText("Finish");

                // Créer la boîte de dialogue personnalisée
                AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(RunSession.this);
                dialogBuilder.setView(dialogView);

                // Afficher la boîte de dialogue
                AlertDialog dialog = dialogBuilder.create();
                dialog.show();

                dialogButtonAddTime.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showDurationDialog();
                        dialog.dismiss();
                    }
                });

                dialogButtonFinish.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        endSession();
                        dialog.dismiss();
                    }
                });

                /////////////////////////

            }
        }.start();

    }
    private void updateCountdownText() {
        long hours = TimeUnit.MILLISECONDS.toHours(timeLeftInMillis);
        long minutes = TimeUnit.MILLISECONDS.toMinutes(timeLeftInMillis - TimeUnit.HOURS.toMillis(hours));
        long seconds = TimeUnit.MILLISECONDS.toSeconds(timeLeftInMillis - TimeUnit.HOURS.toMillis(hours) - TimeUnit.MINUTES.toMillis(minutes));
        String timeRemainingFormatted = String.format(Locale.getDefault(), "%02d:%02d:%02d", hours, minutes, seconds);
        countdownTextView.setText("Time left : " + timeRemainingFormatted);
    }
    private void endSession(){
        Intent intent = new Intent(RunSession.this, EndSession.class);
        intent.putExtra("activity_id", activity_id);
        intent.putExtra("session_id", session_id);
        intent.putExtra("spent_time", totalSessionTime);
        startActivity(intent);
        finish();
    }


    @Override
    public void onBackPressed() {

    }



}
