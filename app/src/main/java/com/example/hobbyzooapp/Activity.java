package com.example.hobbyzooapp;

public class Activity {

    private String name, animalName;
    private Category category;
    private int idAnimal;

    public Activity(String name, String animalName, Category category, int idAnimal) {
        this.name = name;
        this.animalName = animalName;
        this.category = category;
        this.idAnimal = idAnimal;
    }
}
