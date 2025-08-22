package com.s23010675.achievo;

import java.util.Date;

public class PredictionModel {
    public String id;
    public String goalName;
    public String predictionText;
    public Date createdAt;

    public PredictionModel(String id, String goalName, String predictionText, Date createdAt) {
        this.id = id;
        this.goalName = goalName;
        this.predictionText = predictionText;
        this.createdAt = createdAt;
    }
    public String getId() { return id; }
    public String getGoalName() { return goalName; }
    public String getPredictionText() { return predictionText; }
    public Date getCreatedAt() { return createdAt; }

    // Setters
    public void setId(String id) { this.id = id; }
    public void setGoalName(String goalName) { this.goalName = goalName; }
    public void setPredictionText(String predictionText) { this.predictionText = predictionText; }
    public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }
}
