package com.example.hobbyzooapp.Activities;

import com.example.hobbyzooapp.Category.Category;

public class Activity {

    private String name, petName;
    private int idPet;



    private String mnemonic;

    public Activity(String name, String petName, int idPet, String mnemonic) {
        this.name = name;
        this.petName = petName;
        this.idPet = idPet;
        this.mnemonic = mnemonic;
    }

    public String getName() {return name;}
    public int getPet() {return idPet;}
    public String getPetName() {return petName;}
    public String getMnemonic() {return mnemonic;}




}