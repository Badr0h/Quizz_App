package com.example.quizzapp_badreddineoussaih;

import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class OllamaService {
    private static final String TAG = "OllamaService";
    
    // For Emulator use 10.0.2.2
    // For Physical device change this to your PC's local IP (e.g., http://192.168.x.x:11434/api/chat)
    private static final String BASE_URL_EMULATOR = "http://10.0.2.2:11434/api/chat";
    private static final String BASE_URL_PHYSICAL = "http://192.168.1.100:11434/api/chat"; // Replace with actual IP
    
    private String currentUrl;
    private static final MediaType JSON = MediaType.get("application/json; charset=utf-8");

    private final OkHttpClient client;
    private final Gson gson;
    private final List<JsonObject> chatHistory;

    public interface ChatCallback {
        void onResponse(String text);
        void onError(String error);
    }

    public OllamaService() {
        // Objective 1: Increase OkHttp timeouts
        this.client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .build();
        
        this.gson = new Gson();
        this.chatHistory = new ArrayList<>();
        
        // Auto-detect environment
        if (isEmulator()) {
            currentUrl = BASE_URL_EMULATOR;
            Log.d(TAG, "Environment: Emulator detected. URL: " + currentUrl);
        } else {
            currentUrl = BASE_URL_PHYSICAL;
            Log.d(TAG, "Environment: Physical device detected. URL: " + currentUrl);
        }
    }

    public void sendMessage(String userText, ChatCallback callback) {
        JsonObject userMessage = new JsonObject();
        userMessage.addProperty("role", "user");
        userMessage.addProperty("content", userText);
        chatHistory.add(userMessage);

        JsonObject requestBodyJson = new JsonObject();
        requestBodyJson.addProperty("model", "llama3");
        requestBodyJson.addProperty("stream", false);

        JsonArray messagesArray = new JsonArray();
        for (JsonObject msg : chatHistory) {
            messagesArray.add(msg);
        }
        requestBodyJson.add("messages", messagesArray);

        String jsonString = gson.toJson(requestBodyJson);
        RequestBody body = RequestBody.create(jsonString, JSON);

        Log.d(TAG, "Sending request to: " + currentUrl);
        Log.d(TAG, "Request payload: " + jsonString);

        Request request = new Request.Builder()
                .url(currentUrl)
                .post(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e(TAG, "Request failed: " + e.getMessage(), e);
                String errorMessage = "AI is currently unavailable. Please try again.";
                if (e.getMessage() != null && e.getMessage().contains("timeout")) {
                    errorMessage = "Request timed out. The AI is taking too long to respond.";
                }
                final String finalMsg = errorMessage;
                new Handler(Looper.getMainLooper()).post(() -> callback.onError(finalMsg));
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful()) {
                    Log.e(TAG, "Server error: " + response.code());
                    new Handler(Looper.getMainLooper()).post(() -> callback.onError("AI Server error (" + response.code() + "). Check if Ollama is running."));
                    return;
                }

                if (response.body() == null) {
                    new Handler(Looper.getMainLooper()).post(() -> callback.onError("Received empty response from AI."));
                    return;
                }

                String responseData = response.body().string();
                Log.d(TAG, "Response received: " + responseData);
                
                try {
                    JsonObject jsonResponse = gson.fromJson(responseData, JsonObject.class);
                    String aiText = jsonResponse.getAsJsonObject("message").get("content").getAsString();

                    JsonObject aiMessage = new JsonObject();
                    aiMessage.addProperty("role", "assistant");
                    aiMessage.addProperty("content", aiText);
                    chatHistory.add(aiMessage);

                    new Handler(Looper.getMainLooper()).post(() -> callback.onResponse(aiText));
                } catch (Exception e) {
                    Log.e(TAG, "JSON parsing error", e);
                    new Handler(Looper.getMainLooper()).post(() -> callback.onError("Failed to decode AI response."));
                }
            }
        });
    }

    private boolean isEmulator() {
        return (Build.BRAND.startsWith("generic") && Build.DEVICE.startsWith("generic"))
                || Build.FINGERPRINT.startsWith("generic")
                || Build.FINGERPRINT.startsWith("unknown")
                || Build.HARDWARE.contains("goldfish")
                || Build.HARDWARE.contains("ranchu")
                || Build.MODEL.contains("google_sdk")
                || Build.MODEL.contains("Emulator")
                || Build.MODEL.contains("Android SDK built for x86")
                || Build.MANUFACTURER.contains("Genymotion")
                || Build.PRODUCT.contains("sdk_google")
                || Build.PRODUCT.contains("google_sdk")
                || Build.PRODUCT.contains("sdk")
                || Build.PRODUCT.contains("sdk_x86")
                || Build.PRODUCT.contains("vbox86p")
                || Build.PRODUCT.contains("emulator")
                || Build.PRODUCT.contains("simulator");
    }
}