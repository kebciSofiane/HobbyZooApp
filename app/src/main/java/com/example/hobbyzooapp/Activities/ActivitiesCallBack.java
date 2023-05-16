package com.example.hobbyzooapp.Activities;

import com.example.hobbyzooapp.Category.Category;

import java.util.HashMap;

public interface ActivitiesCallBack {
    void onActivitiesLoaded(HashMap<String, Category> expandableListDetail);
}