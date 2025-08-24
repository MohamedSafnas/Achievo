package com.s23010675.achievo;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class PredictionsListActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    PredictionAdapter adapter;
    List<PredictionModel> predictionList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_predictions);

        ImageView home = findViewById(R.id.homeI);
        ImageView profile = findViewById(R.id.profileI);

        recyclerView = findViewById(R.id.recyclerPredictions);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new PredictionAdapter(predictionList);
        recyclerView.setAdapter(adapter);

        loadPredictions();

        LinearLayout getNewPrediction = findViewById(R.id.getNewPrediction);

        getNewPrediction.setOnClickListener(v -> {
            Intent intent = new Intent(PredictionsListActivity.this, PredictSelectMethodActivity.class);
            startActivity(intent);
        });

        //navigate to Profile page
        profile.setOnClickListener(v -> {
            Intent intent = new Intent(PredictionsListActivity.this, ProfileActivity.class);
            startActivity(intent);
        });

        //navigate to home page
        home.setOnClickListener(v -> {
            Intent intent = new Intent(PredictionsListActivity.this, DashboardActivity.class);
            startActivity(intent);
        });
    }

    private void loadPredictions() {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("users")
                .document(userId)
                .collection("predictions")
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    predictionList.clear();
                    for (DocumentSnapshot doc : querySnapshot) {
                        String id = doc.getId();
                        String goalName = doc.getString("goalName");
                        Date createdAt = doc.getDate("createdAt");
                        String predictionText = doc.getString("predictionText");

                        predictionList.add(new PredictionModel(id, goalName, predictionText, createdAt));
                    }
                    adapter.notifyDataSetChanged();
                });
    }
}

