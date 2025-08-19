package com.s23010675.achievo;

import com.google.firebase.Timestamp;
import java.util.List;
import java.util.Map;

public class GoalModel {
    private String id;
    private String name;
    private Timestamp date;
    private List<Map<String, Object>> steps;
    private int completedPercent;

    public GoalModel() {}

    public GoalModel(String id, String name, Timestamp date, List<Map<String,Object>> steps, int completedPercent) {
        this.id = id;
        this.name = name;
        this.date = date;
        this.steps = steps;
        this.completedPercent = completedPercent;
    }

    // Getters and setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public Timestamp getDate() { return date; }
    public void setDate(Timestamp date) { this.date = date; }

    public List<Map<String, Object>> getSteps() { return steps; }
    public void setSteps(List<Map<String, Object>> steps) { this.steps = steps; }

    public int getCompletedPercent() { return completedPercent; }
    public void setCompletedPercent(int completedPercent) { this.completedPercent = completedPercent; }
}

