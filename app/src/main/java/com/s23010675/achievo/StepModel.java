package com.s23010675.achievo;

public class StepModel {
    private String stepText;
    private boolean completed;
    private String id;
    private int stepNumber;
    public StepModel() {}

    public StepModel(String stepText, boolean completed, String id) {
        this.stepText = stepText;
        this.completed = completed;
        this.id = id;

    }

    // Getters and setters
    public int getStepNumber() { return stepNumber; }
    public void setStepNumber(int stepNumber) { this.stepNumber = stepNumber; }
    public String getStepText() { return stepText; }
    public void setStepText(String stepText) { this.stepText = stepText; }
    public boolean isCompleted() { return completed; }
    public void setCompleted(boolean completed) { this.completed = completed; }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
}
