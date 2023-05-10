package com.example.hobbyzooapp.Category;
import android.graphics.Color;

import com.example.hobbyzooapp.Activities.Activity;

import java.util.ArrayList;

public class Category {
    private int id;
    private String name;
    private Color color;
    private ArrayList<Activity> activities;

    public ArrayList<Activity> getActivities() {
        return activities;
    }

    public Category(int id, String name, Color color, ArrayList<Activity> activities) {
        this.id = id;
        this.name = name;
        this.color = color;
        this.activities=activities;

    }
    public String getName() {return name;}

}