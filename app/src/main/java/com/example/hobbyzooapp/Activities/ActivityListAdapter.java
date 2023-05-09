package com.example.hobbyzooapp.Activities;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.example.hobbyzooapp.OnItemClickListener;
import com.example.hobbyzooapp.R;

import java.util.List;

public class ActivityListAdapter extends BaseAdapter {

    private Context context;
    private List<Activity> activityList;
    private LayoutInflater inflater;
    private OnItemClickListener mListener;

    public ActivityListAdapter(Context context, List<Activity> activityList){
        this.context = context;
        this.activityList = activityList;
        this.inflater = LayoutInflater.from(context);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {mListener = listener;}

    @Override
    public int getCount() {
        return activityList.size();
    }

    @Override
    public Activity getItem(int i) {
        return activityList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        view = inflater.inflate(R.layout.adapter_activity_list,null);

        Activity currentActivity = getItem(i);
        String activityName = currentActivity.getName();
        String petName = currentActivity.getPetName();
        String mnemonic = currentActivity.getMnemonic();

        ImageView activityIconView = view.findViewById(R.id.activity_icon);
        String resourceName = mnemonic+"_icon";
        int resId = context.getResources().getIdentifier(resourceName,"drawable",context.getPackageName());
        activityIconView.setImageResource(resId);


        TextView activityNameView = view.findViewById(R.id.activity_name);
        activityNameView.setText(activityName);
        TextView petNameView = view.findViewById(R.id.pet_name);
        petNameView.setText(petName);

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
