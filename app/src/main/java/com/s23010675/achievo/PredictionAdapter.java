package com.s23010675.achievo;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.DateFormat;
import java.util.List;

public class PredictionAdapter extends RecyclerView.Adapter<PredictionAdapter.PredictionViewHolder> {

    private List<PredictionModel> predictionList;

    public PredictionAdapter(List<PredictionModel> predictionList) {
        this.predictionList = predictionList;
    }

    @NonNull
    @Override
    public PredictionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_prediction, parent, false);
        return new PredictionViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull PredictionViewHolder holder, int position) {
        PredictionModel prediction = predictionList.get(position);

        holder.tvGoalName.setText(prediction.goalName);
        holder.tvDate.setText(DateFormat.getDateInstance().format(prediction.createdAt));

        holder.btnDelete.setOnClickListener(v -> {
            String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
            FirebaseFirestore db = FirebaseFirestore.getInstance();

            db.collection("users")
                    .document(userId)
                    .collection("predictions")
                    .document(prediction.getId())
                    .delete()
                    .addOnSuccessListener(aVoid -> {
                        predictionList.remove(position);
                        notifyItemRemoved(position);
                        Toast.makeText(holder.itemView.getContext(), "Deleted", Toast.LENGTH_SHORT).show();
                    })
                    .addOnFailureListener(e -> {
                         Toast.makeText(holder.itemView.getContext(), "Delete failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        });
        holder.itemView.setOnClickListener(c -> {
            Intent intent = new Intent(holder.itemView.getContext(), PredictOutcomeActivity.class);
            //intent.putExtra("predictionType", prediction.getType()); // "goal" or "custom"
            intent.putExtra("goalName", prediction.getGoalName());
            intent.putExtra("title", prediction.getGoalName());// for custom, this is title
            intent.putExtra("predictionOutcome", prediction.getPredictionText());
            intent.putExtra("date", prediction.getCreatedAt()); // optional
            holder.itemView.getContext().startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return predictionList.size();
    }

    static class PredictionViewHolder extends RecyclerView.ViewHolder {
        TextView tvGoalName, tvDate;
        ImageView btnDelete;

        public PredictionViewHolder(@NonNull View itemView) {
            super(itemView);
            tvGoalName = itemView.findViewById(R.id.tvGoalName);
            tvDate = itemView.findViewById(R.id.tvDate);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }
    }
}
