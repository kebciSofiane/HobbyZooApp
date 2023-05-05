package com.example.hobbyzooapp;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {



    private static final long countDownTime = 30000;
    private long timeLeftInMillis = countDownTime;
    TextView countdownTextView;
    CountDownTimer countDownTimer;
    Button stopButton;
    Button pauseButton;
    Button resumeButton;
    ImageView petPicture;


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        countdownTextView = findViewById(R.id.countdownTextView);
        pauseButton = findViewById(R.id.pauseButton);
        stopButton = findViewById(R.id.stopButton);
        resumeButton=findViewById(R.id.resumeButton);
        petPicture = findViewById(R.id.petPicture);
        petPicture.setImageResource(R.drawable.koala);



        startCountdown(countDownTime);

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
                new AlertDialog.Builder(MainActivity.this)
                        .setTitle("Vous êtes sur le point de mettre fin à votre activité !")
                        .setMessage("Voulez-vous vraiment arrêter votre activité ?")
                        .setNegativeButton(android.R.string.no, null)
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface arg0, int arg1) {
                                countDownTimer.cancel();
                                endSession();
                            }
                        }).create().show();

            }
        });
    }


    private void pauseCountDown(){
        countDownTimer.cancel();
        resumeButton.setVisibility(View.VISIBLE);
        pauseButton.setVisibility(View.GONE);
    }
    private void resumeCountDown(){
        startCountdown(timeLeftInMillis);
        countDownTimer.start();
        pauseButton.setVisibility(View.VISIBLE);
        resumeButton.setVisibility(View.GONE);
    }
    private void startCountdown(long time){
        countDownTimer=new CountDownTimer(time, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                timeLeftInMillis = millisUntilFinished;
                updateCountdownText();
            }
            @Override
            public void onFinish() {
                endSession();
            }
        }.start();

    }
    private void updateCountdownText() {
        long hours = TimeUnit.MILLISECONDS.toHours(timeLeftInMillis);
        long minutes = TimeUnit.MILLISECONDS.toMinutes(timeLeftInMillis - TimeUnit.HOURS.toMillis(hours));
        long seconds = TimeUnit.MILLISECONDS.toSeconds(timeLeftInMillis - TimeUnit.HOURS.toMillis(hours) - TimeUnit.MINUTES.toMillis(minutes));
        String timeRemainingFormatted = String.format(Locale.getDefault(), "%02d:%02d:%02d", hours, minutes, seconds);
        countdownTextView.setText("Temps restant : " + timeRemainingFormatted);
    }
    private void endSession(){
        Intent intent = new Intent(MainActivity.this, endSession.class);
        startActivity(intent);
    }


}
