package com.example.doanngon.DTO;

public class Step {
    private String description;

    public String getImgStep() {
        return imgStep;
    }

    private String imgStep;

    public Step() {
        // Default constructor required for calls to DataSnapshot.getValue(Step.class)
    }

    public Step(String description,String imgStep) {
        this.description = description;
        this.imgStep = imgStep;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
