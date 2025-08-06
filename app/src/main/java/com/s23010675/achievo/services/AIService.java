package com.s23010675.achievo.services;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.NonNull;

import org.json.JSONArray;
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
    private final String API_URL = "https://api-inference.huggingface.co/models/google/flan-t5-base";
    private final String API_TOKEN = "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"; // 🔁 Replace with your Hugging Face token

    private final Context context;

    public AIService(Context context) {
        this.context = context;
    }

    public void generateSteps(String goalPrompt, @NonNull GoalCallback callback) {
        new Thread(() -> {
            try {
                JSONObject payload = new JSONObject();
                payload.put("inputs", "Instruction: Give 5 clear steps to achieve the goal.\nGoal: " + goalPrompt);

                JSONObject parameters = new JSONObject();
                parameters.put("temperature", 0.7);
                parameters.put("max_new_tokens", 200);
                payload.put("parameters", parameters);

                Request request = new Request.Builder()
                        .url(API_URL)
                        .addHeader("Authorization", "Bearer " + API_TOKEN) // ✅ Important: Must start with "Bearer "
                        .post(RequestBody.create(
                                payload.toString(),
                                MediaType.get("application/json")))
                        .build();

                Response response = client.newCall(request).execute();

                String body = response.body() != null ? response.body().string() : "null";
                if (response.isSuccessful()) {
                    Log.d("DeepSeekSuccess", body);
                    String result = parseGeneratedText(body);
                    postSuccess(callback, result);
                } else {
                    Log.e("DeepSeekError", "HTTP " + response.code() + ": " + body);
                    postError(callback, "HTTP " + response.code() + ": " + body);
                }
            } catch (IOException | JSONException e) {
                Log.e("DeepSeekError", "Exception: " + e.getMessage(), e);
                postError(callback, "Exception: " + e.getMessage());
            }
        }).start();
    }


    private void postSuccess(GoalCallback callback, String result) {
        new Handler(Looper.getMainLooper()).post(() -> callback.onSuccess(result));
    }

    private void postError(GoalCallback callback, String error) {
        new Handler(Looper.getMainLooper()).post(() -> callback.onError(error));
    }

    private String parseGeneratedText(String json) throws JSONException {
        JSONArray arr = new JSONArray(json);
        JSONObject obj = arr.getJSONObject(0);
        return obj.getString("generated_text");
    }
}
