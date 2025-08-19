package com.s23010675.achievo;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.s23010675.achievo.R;
import com.s23010675.achievo.ViewStepsActivity;
import com.s23010675.achievo.GoalModel;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class GoalAdapter extends RecyclerView.Adapter<GoalAdapter.GoalViewHolder> {

    private Context context;
    private List<GoalModel> goals;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseAuth auth = FirebaseAuth.getInstance();

    public GoalAdapter(Context context, List<GoalModel> goals) {
        this.context = context;
        this.goals = goals;
    }

    public void setGoals(List<GoalModel> newGoals) {
        goals.clear();           // clear current items
        goals.addAll(newGoals);  // add new items
        notifyDataSetChanged();  // refresh RecyclerView
    }



    @NonNull
    @Override
    public GoalViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_goal, parent, false);
        return new GoalViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull GoalViewHolder holder, int position) {
        GoalModel goal = goals.get(position);

        holder.nameText.setText(goal.getName());
        if(goal.getDate() != null){
            SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault());
            holder.dateText.setText(sdf.format(goal.getDate().toDate()));
        } else {
            holder.dateText.setText("No Date");
        }

        holder.progressBar.setProgress(goal.getCompletedPercent());

        // Delete goal
        holder.deleteIcon.setOnClickListener(v -> {
            db.collection("users")
                    .document(auth.getCurrentUser().getUid())
                    .collection("goals")
                    .document(goal.getId())
                    .delete()
                    .addOnSuccessListener(aVoid -> Toast.makeText(context, "Goal deleted", Toast.LENGTH_SHORT).show())
                    .addOnFailureListener(e -> Toast.makeText(context, "Failed to delete", Toast.LENGTH_SHORT).show());
        });

        // Open ViewStepsActivity
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, ViewStepsActivity.class);
            intent.putExtra("goal_id", goal.getId());
            intent.putExtra("goal_name", goal.getName());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return goals.size();
    }

    public static class GoalViewHolder extends RecyclerView.ViewHolder {
        TextView nameText, dateText;
        ProgressBar progressBar;
        ImageView deleteIcon;

        public GoalViewHolder(@NonNull View itemView) {
            super(itemView);
            nameText = itemView.findViewById(R.id.goalNameText);
            dateText = itemView.findViewById(R.id.goalDateText);
            progressBar = itemView.findViewById(R.id.goalProgressBar);
            deleteIcon = itemView.findViewById(R.id.deleteGoalIcon);
        }
    }
}
