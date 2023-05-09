package com.example.hobbyzooapp.Sessions;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;


import com.example.hobbyzooapp.OnItemClickListener;
import com.example.hobbyzooapp.R;
import com.example.hobbyzooapp.Sessions.Session;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

public class DailySessionAdapter extends BaseAdapter{

    private Context context;
    private List<Session> sessionList;
    private List<Session> sessionListFiltered = new ArrayList<Session>();
    private LayoutInflater inflater;
    private int day;
    private int month;
    private OnItemClickListener mListener;

    //private Date date;

    public DailySessionAdapter(Context context, List<Session> sessionList, int month, int day){
        this.context = context;
        this.sessionList = sessionList;
        this.inflater = LayoutInflater.from(context);
        this.day = day;
        this.month = month;
        for(Session session : sessionList){
            if ((session.getDay() == this.day) && (session.getMonth()==this.month)){
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
        String sessionName = currentSession.getName();
        String sessionTime = currentSession.getTime().toString();

        TextView sessionNameView = view.findViewById(R.id.session_name);
        sessionNameView.setText(sessionName);

        TextView sessionTimeView = view.findViewById(R.id.session_time);
        sessionTimeView.setText(sessionTime);

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null) {
                    mListener.onItemClick(i);
                }
            }
        });

        return view;
    }
}
