package com.s23010675.achievo;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class LocationAdapter extends RecyclerView.Adapter<LocationAdapter.LocationViewHolder> {

    private Context context;
    private List<LocationModel> locationList;

    public LocationAdapter(Context context, List<LocationModel> locationList) {
        this.context = context;
        this.locationList = locationList;
    }

    @NonNull
    @Override
    public LocationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_location, parent, false);
        return new LocationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LocationViewHolder holder, int position) {
        LocationModel location = locationList.get(position);
        holder.textViewName.setText(location.getName());

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, FindLocationActivity.class);
            intent.putExtra("latitude", location.getLatitude());
            intent.putExtra("longitude", location.getLongitude());
            intent.putExtra("locationName", location.getName());
            context.startActivity(intent);
        });

        holder.deleteIcon.setOnClickListener(v -> {
            int pos = holder.getAdapterPosition();
            if (pos != RecyclerView.NO_POSITION) {
                deleteLocation(location.getId(), pos);
            }
        });
    }

    @Override
    public int getItemCount() {
        return locationList.size();
    }

    public static class LocationViewHolder extends RecyclerView.ViewHolder {
        TextView textViewName;
        ImageView deleteIcon;

        public LocationViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewName = itemView.findViewById(R.id.locationName);
            deleteIcon = itemView.findViewById(R.id.deleteLocation);
        }
    }

    private void deleteLocation(String docId, int position) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseAuth auth = FirebaseAuth.getInstance();
        String userId = auth.getCurrentUser().getUid();

        db.collection("users")
                .document(userId)
                .collection("locations")
                .document(docId)
                .delete()
                .addOnSuccessListener(aVoid -> {
                    locationList.remove(position);
                    notifyItemRemoved(position);
                    Toast.makeText(context, "Deleted", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e ->
                        Toast.makeText(context, "Delete failed", Toast.LENGTH_SHORT).show());
    }

}
