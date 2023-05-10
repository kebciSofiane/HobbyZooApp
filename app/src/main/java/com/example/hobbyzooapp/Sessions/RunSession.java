package com.example.hobbyzooapp.Sessions;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;

import com.example.hobbyzooapp.R;

import java.util.Calendar;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class RunSession extends AppCompatActivity {

    private long countDownTime = 5000;
    private long timeLeftInMillis = countDownTime;
    public static long  totalSessionTime=0;
    TextView countdownTextView;
    CountDownTimer countDownTimer;
    Button stopButton;
    Button pauseButton;
    Button resumeButton;
    ImageView petPicture;
    Button validateButton;
    Button addTimeButton;



    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_run_session);

        countdownTextView = findViewById(R.id.countdownTextView);
        pauseButton = findViewById(R.id.pauseButton);
        stopButton = findViewById(R.id.stopButton);
        resumeButton=findViewById(R.id.resumeButton);
        petPicture = findViewById(R.id.petPicture);
        petPicture.setImageResource(R.drawable.koala);
        validateButton = findViewById(R.id.validateButton);
        addTimeButton = findViewById(R.id.addTimeButton);



        startCountdown();

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
                        .setTitle("Vous êtes sur le point de mettre fin à votre activité !")
                        .setMessage("Voulez-vous vraiment arrêter votre activité ?")
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
        Intent intent = new Intent(RunSession.this, endSession.class);
        startActivity(intent);
    }


}
