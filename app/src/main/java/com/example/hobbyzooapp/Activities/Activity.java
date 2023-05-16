package com.example.hobbyzooapp.Activities;

import com.example.hobbyzooapp.Category.Category;

public class Activity {

    private String name, petName;



    private String mnemonic;

    public Activity(String name, String petName, String mnemonic) {
        this.name = name;
        this.petName = petName;
        this.mnemonic = mnemonic;
    }

    public String getName() {return name;}
    public String getPetName() {return petName;}
    public String getMnemonic() {return mnemonic;}




}