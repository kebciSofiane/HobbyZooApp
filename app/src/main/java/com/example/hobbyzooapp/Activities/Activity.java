package com.example.hobbyzooapp.Activities;

import com.example.hobbyzooapp.Category.Category;

public class Activity {

    private String name, petName, activity_id;



    private String mnemonic;

    public Activity(String id,String name, String petName, String mnemonic) {
        this.name = name;
        this.petName = petName;
        this.mnemonic = mnemonic;
        this.activity_id = id;

    }

    public String getActivity_id() {
        return activity_id;
    }
    public String getName() {return name;}
    public String getPetName() {return petName;}
    public String getMnemonic() {return mnemonic;}




}