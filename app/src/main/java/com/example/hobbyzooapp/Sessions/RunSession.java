package com.example.hobbyzooapp.Sessions;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;

import com.example.hobbyzooapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class RunSession extends AppCompatActivity {

    private long countDownTime = 5000;
    private long timeLeftInMillis = countDownTime;
    long totalSessionTime=0;
    TextView countdownTextView;
    CountDownTimer countDownTimer;
    ImageButton stopButton, pauseButton, resumeButton, validateButton, addTimeButton;
    ImageView petPicture;
    FirebaseAuth firebaseAuth;
    private MediaPlayer mediaPlayer;
    String activity_id, session_id, activityPet, session_duration;
    private boolean isCounting = false;


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
                    String resourceName = activityPet+"_whole_neutral";
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

                LayoutInflater inflater = getLayoutInflater();
                View dialogView = inflater.inflate(R.layout.custom_dialog_, null);

                TextView dialogTitle = dialogView.findViewById(R.id.dialogTitle);
                TextView dialogText = dialogView.findViewById(R.id.dialogText);
                Button dialogButtonResume = dialogView.findViewById(R.id.dialogButtonLeft);
                Button dialogButtonStop = dialogView.findViewById(R.id.dialogButtonRight);

                dialogTitle.setText("You are about to close your session !");
                dialogText.setText("Do you really want to stop ?");
                dialogButtonResume.setText("Resume");
                dialogButtonResume.setTextColor(Color.GREEN);
                dialogButtonStop.setText("Stop");
                dialogButtonStop.setTextColor(Color.RED);

                AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(RunSession.this);
                dialogBuilder.setView(dialogView);
                AlertDialog dialog = dialogBuilder.create();
                dialog.show();
                dialogButtonResume.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
                dialogButtonStop.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        totalSessionTime += countDownTime-timeLeftInMillis;
                        countDownTimer.cancel();
                        endSession();
                    }
                });

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

    private void initMediaPlayer() {
        mediaPlayer = MediaPlayer.create(this, R.raw.notification2);
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {

            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        pauseCountDown();
        isCounting = false;
    }
    @Override
    protected void onResume() {
        super.onResume();
        if (isCounting) {
            resumeCountDown();
        }

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
                initMediaPlayer();
                mediaPlayer.start();
                totalSessionTime += countDownTime;
                validateButton.setVisibility(View.VISIBLE);
                resumeButton.setVisibility(View.GONE);
                stopButton.setVisibility(View.GONE);
                pauseButton.setVisibility(View.GONE);
                addTimeButton.setVisibility(View.VISIBLE);

                LayoutInflater inflater = getLayoutInflater();
                View dialogView = inflater.inflate(R.layout.custom_dialog_, null);

                TextView dialogTitle = dialogView.findViewById(R.id.dialogTitle);
                Button dialogButtonAddTime = dialogView.findViewById(R.id.dialogButtonLeft);
                Button dialogButtonFinish = dialogView.findViewById(R.id.dialogButtonRight);

                AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(RunSession.this);
                dialogBuilder.setView(dialogView);

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
            }

        }.start();

    }
    private void updateCountdownText() {
        long hours = TimeUnit.MILLISECONDS.toHours(timeLeftInMillis);
        long minutes = TimeUnit.MILLISECONDS.toMinutes(timeLeftInMillis - TimeUnit.HOURS.toMillis(hours));
        long seconds = TimeUnit.MILLISECONDS.toSeconds(timeLeftInMillis - TimeUnit.HOURS.toMillis(hours) - TimeUnit.MINUTES.toMillis(minutes));
        String timeRemainingFormatted = String.format(Locale.getDefault(), "%02d:%02d:%02d", hours, minutes, seconds);
        countdownTextView.setText(timeRemainingFormatted);
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
