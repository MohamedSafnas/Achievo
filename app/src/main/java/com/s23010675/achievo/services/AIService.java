package com.s23010675.achievo.services;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.NonNull;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class AIService {

    public interface GoalCallback {
        void onSuccess(String resultJson);
        void onError(String error);
    }

    private final OkHttpClient client = new OkHttpClient();
    private final String API_URL = "https://gemini-api-t0jr.onrender.com/generate"; // ✅ Your live endpoint
    private final Context context;

    public AIService(Context context) {
        this.context = context;
    }

    public void generateSteps(String goalPrompt, @NonNull GoalCallback callback) {
        new Thread(() -> {
            try {
                JSONObject payload = new JSONObject();
                payload.put("goal", goalPrompt);

                Request request = new Request.Builder()
                        .url(API_URL)
                        .post(RequestBody.create(
                                payload.toString(),
                                MediaType.get("application/json")
                        ))
                        .build();

                Response response = client.newCall(request).execute();

                if (response.isSuccessful()) {
                    String body = response.body().string();
                    JSONObject json = new JSONObject(body);
                    String steps = json.getString("steps");
                    postSuccess(callback, steps);
                } else {
                    String errorBody = response.body() != null ? response.body().string() : "No response";
                    postError(callback, "HTTP " + response.code() + ": " + errorBody);
                }
            } catch (IOException | JSONException e) {
                Log.e("GeminiError", "Exception: " + e.getMessage(), e);
                postError(callback, e.getMessage());
            }
        }).start();
    }

    private void postSuccess(GoalCallback callback, String result) {
        new Handler(Looper.getMainLooper()).post(() -> callback.onSuccess(result));
    }

    private void postError(GoalCallback callback, String error) {
        new Handler(Looper.getMainLooper()).post(() -> callback.onError(error));
    }
}
