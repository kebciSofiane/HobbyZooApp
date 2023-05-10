package com.example.hobbyzooapp.Activities;

import com.example.hobbyzooapp.Category.Category;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ExpandableListData {
    public static HashMap<String, List<Activity>> getData() {
        HashMap<String, List<Activity>> expandableListDetail = new HashMap<String, List<Activity>>();


        ArrayList<Activity> activities = new ArrayList<>();
        activities.add(new Activity("Dessin", "Biquette", 10, "sheep"));
        activities.add(new Activity("poetry", "Coco", 10, "koala"));
        activities.add(new Activity("poetry", "Coco", 10, "koala"));

        ArrayList<Activity> activities2 = new ArrayList<>();
        activities2.add(new Activity("Dessin", "Biquette", 10, "sheep"));

        ArrayList<Activity> activities3 = new ArrayList<>();
        activities3.add(new Activity("Dessin", "Biquette", 10, "sheep"));
        activities3.add(new Activity("poetry", "Coco", 10, "koala"));



        expandableListDetail.put("Sport",activities);
        expandableListDetail.put("Cooking", activities2);
        expandableListDetail.put("Art", activities3);

        return expandableListDetail;
    }
}