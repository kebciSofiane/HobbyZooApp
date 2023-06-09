package com.example.hobbyzooapp.Activities;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.hobbyzooapp.Category.Category;
import com.example.hobbyzooapp.HomeActivity;
import com.example.hobbyzooapp.R;

import java.util.HashMap;
import java.util.List;

public class ExpandableListAdapter extends BaseExpandableListAdapter {
    private Context context;
    private List<String> expandableListTitle;
    private HashMap<String, Category> expandableListDetail;

    public ExpandableListAdapter(Context context, List<String> expandableListTitle,
                                 HashMap<String, Category> expandableListDetail) {
        this.context = context;
        this.expandableListTitle = expandableListTitle;
        this.expandableListDetail = expandableListDetail;
    }

    @Override
    public Object getChild(int listPosition, int expandedListPosition) {
        return this.expandableListDetail.get(this.expandableListTitle.get(listPosition)).getActivities()
                .get(expandedListPosition);
    }

    @Override
    public long getChildId(int listPosition, int expandedListPosition) {
        return expandedListPosition;
    }

    @Override
    public View getChildView(int listPosition, final int expandedListPosition,
                             boolean isLastChild, View convertView, ViewGroup parent) {
        Activity activity =(Activity) getChild(listPosition, expandedListPosition);
        final String expandedListText = activity.getName();
        if (convertView == null) {
            LayoutInflater layoutInflater = (LayoutInflater) this.context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.adapter_activity_list, null);
        }
        TextView expandedListTextView = (TextView) convertView
                .findViewById(R.id.child_text);


        TextView petNameView = convertView.findViewById(R.id.pet_name);
        petNameView.setText(activity.getPetName());

        ImageView activityIconView = convertView.findViewById(R.id.activity_icon);
        String feeling = HomeActivity.animalsFeeling.get(Integer.parseInt(activity.getFeeling()));
        String resourceName;
        if(Integer.parseInt(activity.getFeeling()) == 0)
            resourceName = "none_icon_gone";
        else
            resourceName = activity.getPet()+"_icon_"+feeling;
        int resId = context.getResources().getIdentifier(resourceName,"drawable",context.getPackageName());
        activityIconView.setImageResource(resId);

        expandedListTextView.setText(expandedListText);
        return convertView;
    }

    @Override
    public int getChildrenCount(int listPosition) {
        return this.expandableListDetail.get(this.expandableListTitle.get(listPosition)).getActivities()
                .size();
    }

    @Override
    public Object getGroup(int listPosition) {
        return this.expandableListTitle.get(listPosition);
    }

    @Override
    public int getGroupCount() {
        return this.expandableListTitle.size();
    }

    @Override
    public long getGroupId(int listPosition) {
        return listPosition;
    }

    @Override
    public View getGroupView(int listPosition, boolean isExpanded,
                             View convertView, ViewGroup parent) {
        String listTitle = (String) getGroup(listPosition);
        if (convertView == null) {
            LayoutInflater layoutInflater = (LayoutInflater) this.context.
                    getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.adapter_category_list, null);
        }
        TextView listTitleTextView = (TextView) convertView
                .findViewById(R.id.text);
        listTitleTextView.setTypeface(null, Typeface.BOLD);
        listTitleTextView.setText(listTitle);
        return convertView;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public boolean isChildSelectable(int listPosition, int expandedListPosition) {
        return true;
    }
}