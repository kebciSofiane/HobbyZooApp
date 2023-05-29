package com.example.hobbyzooapp.Activities;

public class Activity {

    private String name, petName, activity_id;
    private String pet,state;

    public Activity(String id,String name, String petName, String pet) {
        this.name = name;
        this.petName = petName;
        this.pet = pet;
        this.activity_id = id;
        //this.state = state;

    }

    public String getActivity_id() {
        return activity_id;
    }
    public String getName() {return name;}
    public String getPetName() {return petName;}
    public String getPet() {return pet;}




}