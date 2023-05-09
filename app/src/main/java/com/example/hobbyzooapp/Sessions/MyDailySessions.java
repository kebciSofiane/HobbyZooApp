package com.example.hobbyzooapp.Sessions;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputFilter;
import android.view.View;
import android.widget.Button;
import android.widget.GridView;
import android.widget.EditText;


import androidx.appcompat.app.AppCompatActivity;

import com.example.hobbyzooapp.MainActivity;
import com.example.hobbyzooapp.R;
import com.example.hobbyzooapp.OnItemClickListener;
import com.example.hobbyzooapp.RunSession;

import java.sql.Time;
import java.util.ArrayList;
import java.util.List;

public class MyDailySessions extends AppCompatActivity {

    private Button homeButton;
    private View sessionButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_daily_sessions);

        homeButton = findViewById(R.id.homeButton);
        homeButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                openMainActivity();
            }
        });

        sessionButton = findViewById(R.id.session);


        List<Session> sessionList = new ArrayList<>();
        sessionList.add(new Session("Dessin", new Time(0, 1, 10), 10, 10));
        sessionList.add(new Session("Bougie",new Time(0,2,0),5,2));


        //fonctionne pas ????
        //Date date = new Date(2023,10,10);
        GridView sessionListView = findViewById(R.id.session_list_view);
        DailySessionAdapter adapter = new DailySessionAdapter(this,sessionList,10,10);
        adapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                openMainActivity();
            }
        });
        sessionListView.setAdapter(adapter);

        //GridView sessionListView = findViewById(R.id.session_list_view);
        //sessionListView.setAdapter(new SessionAdapter(this,sessionList,10,10));
        //je sais pas comment faire marcher ca
        // sessionListView.setOnItemClickListener();


        }


    public void openMainActivity(){
        Intent intent = new Intent(this, RunSession.class);
        startActivity(intent);
        finish();
    }
}