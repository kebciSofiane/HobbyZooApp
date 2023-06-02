package com.example.hobbyzooapp.Sessions;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;


import androidx.core.content.ContextCompat;

import com.example.hobbyzooapp.Calendar.CalendarActivity;
import com.example.hobbyzooapp.OnItemClickListener;
import com.example.hobbyzooapp.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;

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
    private Boolean isDeleteMode;

    public MyDailySessionsAdapter(Context context, List<Session> sessionList, LocalDate localDate, boolean isDeleteMode){
        this.context = context;
        this.sessionList = sessionList;
        this.isDeleteMode = isDeleteMode;

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
        String sessionTime = currentSession.getTime().toString();

        TextView sessionNameView = view.findViewById(R.id.session_name);
        sessionNameView.setText(sessionName);

        TextView sessionTimeView = view.findViewById(R.id.session_time);
        sessionTimeView.setText(sessionTime);

        String sessionPetIconRes = currentSession.getPet()+"_icon_neutral";
        int resId = context.getResources().getIdentifier(sessionPetIconRes,"drawable",context.getPackageName());

        ImageView sessionPetIcon = view.findViewById(R.id.session_icon);
        sessionPetIcon.setImageResource(resId);

        String sessionDone = currentSession.getDone();
        LinearLayout sessionSquare = view.findViewById(R.id.itemSessionList);
        Drawable drawableDone = ContextCompat.getDrawable(context, R.drawable.shape_border_gray);
        Drawable drawableDelete = ContextCompat.getDrawable(context, R.drawable.shape_border_red);
        if (sessionDone.equals("TRUE")) sessionSquare.setBackground(drawableDone);
        else if (isDeleteMode.equals(Boolean.TRUE)) sessionSquare.setBackground(drawableDelete);

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

    public void setIsDeleteMode(Boolean deleteMode) {
        isDeleteMode = deleteMode;

    }
}