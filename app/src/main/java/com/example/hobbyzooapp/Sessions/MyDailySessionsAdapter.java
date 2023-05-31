package com.example.hobbyzooapp.Sessions;

import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;


import com.example.hobbyzooapp.OnItemClickListener;
import com.example.hobbyzooapp.R;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class MyDailySessionsAdapter extends BaseAdapter{

    private Context context;
    private List<Session> sessionList;
    private List<Session> sessionListFiltered = new ArrayList<Session>();
    private LayoutInflater inflater;
    private int day, month, year;
    private OnItemClickListener mListener;

    public MyDailySessionsAdapter(Context context, List<Session> sessionList, LocalDate localDate){
        this.context = context;
        this.sessionList = sessionList;

        this.inflater = LayoutInflater.from(context);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            this.day = localDate.getDayOfMonth();
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            this.month = localDate.getMonthValue();
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            this.year = localDate.getYear();
        }
        for(Session session : sessionList){
            if ((session.getDay() == this.day) && (session.getMonth()==this.month) && (session.getYear()==this.year)){
                sessionListFiltered.add(session);
            }
        }
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }

    @Override
    public int getCount() {
        return sessionListFiltered.size();
    }

    @Override
    public Session getItem(int i) {
        return sessionListFiltered.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        view = inflater.inflate(R.layout.adapter_session_list,null);

        Session currentSession = getItem(i);
        String sessionName = currentSession.getActivityName().replace(",", " ");
        String sessionTime = currentSession.getTime().toString(); //todo enlever les secondes ?

        TextView sessionNameView = view.findViewById(R.id.session_name);
        sessionNameView.setText(sessionName);

        TextView sessionTimeView = view.findViewById(R.id.session_time);
        sessionTimeView.setText(sessionTime);

        String sessionPetIconRes = currentSession.getPet()+"_icon_neutral";
        int resId = context.getResources().getIdentifier(sessionPetIconRes,"drawable",context.getPackageName());

        ImageView sessionPetIcon = view.findViewById(R.id.session_icon);
        sessionPetIcon.setImageResource(resId);

        String sessionDone = currentSession.getDone();
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null && sessionDone.equals("FALSE")) {
                    mListener.onItemClick(i);
                }
            }
        });

        return view;
    }
}