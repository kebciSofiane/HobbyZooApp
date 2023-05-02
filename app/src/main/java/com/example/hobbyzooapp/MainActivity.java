package com.example.hobbyzooapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.widget.TextView;

import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {



    private static final long COUNTDOWN_TIME = 3000000;
    private long timeLeftInMillis = COUNTDOWN_TIME;
    TextView countdownTextView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        countdownTextView = findViewById(R.id.countdownTextView);

        new CountDownTimer(COUNTDOWN_TIME, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                timeLeftInMillis = millisUntilFinished;
                updateCountdownText();
            }

            @Override
            public void onFinish() {
                // Le compte à rebours est terminé
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

}