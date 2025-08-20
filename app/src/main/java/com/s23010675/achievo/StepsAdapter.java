package com.s23010675.achievo;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class StepsAdapter extends RecyclerView.Adapter<StepsAdapter.StepViewHolder> {

    private final List<StepModel> steps;
    private final OnStepCheckedChangeListener listener;

    public interface OnStepCheckedChangeListener {
        void onCheckedChanged();
    }

    public StepsAdapter(List<StepModel> steps, OnStepCheckedChangeListener listener) {
        this.steps = steps;
        this.listener = listener;
    }

    public static class StepViewHolder extends RecyclerView.ViewHolder {
        CheckBox checkBox;

        public StepViewHolder(View itemView) {
            super(itemView);
            checkBox = itemView.findViewById(R.id.checkboxStep);
        }
    }

    @NonNull
    @Override
    public StepViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_step, parent, false);
        return new StepViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StepViewHolder holder, int position) {
        StepModel step = steps.get(position);

        holder.checkBox.setOnCheckedChangeListener(null);

        holder.checkBox.setText(step.getStepText());
        holder.checkBox.setChecked(step.isCompleted());

        holder.checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            step.setCompleted(isChecked);
            listener.onCheckedChanged();
        });
    }


    @Override
    public int getItemCount() {
        return steps.size();
    }

    // getters
    public int getCheckedCount() {
        int count = 0;
        for (StepModel step : steps) {
            if (step.isCompleted()) count++;
        }
        return count;
    }


    public int getTotalCount() {
        return steps.size();
    }
}
