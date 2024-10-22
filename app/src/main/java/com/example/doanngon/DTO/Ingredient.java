package com.example.doanngon.DTO;

public class Ingredient {
    private String name;

    public Ingredient() {
        // Default constructor required for calls to DataSnapshot.getValue(Ingredient.class)
    }

    public Ingredient(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
