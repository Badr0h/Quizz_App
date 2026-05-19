package com.example.quizzapp_badreddineoussaih;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ChatActivity extends AppCompatActivity {

    private static final String TAG = "ChatActivity";
    private RecyclerView rvChat;
    private ChatAdapter adapter;
    private List<Message> messages;
    private EditText etMessage;
    private ImageButton btnSend, btnBack, btnMic;
    private ProgressBar pbLoading;
    private OllamaService ollamaService;
    private SpeechRecognizer speechRecognizer;
    private Intent speechRecognizerIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        rvChat = findViewById(R.id.rvChat);
        etMessage = findViewById(R.id.etMessage);
        btnSend = findViewById(R.id.btnSend);
        btnBack = findViewById(R.id.btnBack);
        btnMic = findViewById(R.id.btnMic);
        pbLoading = findViewById(R.id.pbLoading);

        messages = new ArrayList<>();
        adapter = new ChatAdapter(messages);
        rvChat.setLayoutManager(new LinearLayoutManager(this));
        rvChat.setAdapter(adapter);

        ollamaService = new OllamaService();

        btnBack.setOnClickListener(v -> finish());

        addMessage("Mission Assistant online. How can I help you, player?", false);

        btnSend.setOnClickListener(v -> {
            String text = etMessage.getText().toString().trim();
            if (!TextUtils.isEmpty(text)) {
                sendMessage(text);
            }
        });

        initSpeechRecognizer();

        btnMic.setOnClickListener(v -> {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO}, 101);
            } else {
                startListening();
            }
        });
    }

    private void initSpeechRecognizer() {
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this);
        speechRecognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());

        speechRecognizer.setRecognitionListener(new RecognitionListener() {
            @Override
            public void onReadyForSpeech(Bundle params) {
                Log.d(TAG, "onReadyForSpeech");
                Toast.makeText(ChatActivity.this, "Listening...", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onBeginningOfSpeech() {
                Log.d(TAG, "onBeginningOfSpeech");
            }

            @Override
            public void onRmsChanged(float rmsdB) {}

            @Override
            public void onBufferReceived(byte[] buffer) {}

            @Override
            public void onEndOfSpeech() {
                Log.d(TAG, "onEndOfSpeech");
            }

            @Override
            public void onError(int error) {
                String message;
                switch (error) {
                    case SpeechRecognizer.ERROR_AUDIO: message = "Audio recording error"; break;
                    case SpeechRecognizer.ERROR_CLIENT: message = "Client side error"; break;
                    case SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS: message = "Insufficient permissions"; break;
                    case SpeechRecognizer.ERROR_NETWORK: message = "Network error"; break;
                    case SpeechRecognizer.ERROR_NETWORK_TIMEOUT: message = "Network timeout"; break;
                    case SpeechRecognizer.ERROR_NO_MATCH: message = "No match found"; break;
                    case SpeechRecognizer.ERROR_RECOGNIZER_BUSY: message = "Recognition service busy"; break;
                    case SpeechRecognizer.ERROR_SERVER: message = "Server error"; break;
                    case SpeechRecognizer.ERROR_SPEECH_TIMEOUT: message = "No speech input"; break;
                    default: message = "Didn't understand, please try again."; break;
                }
                Log.e(TAG, "Speech Error: " + message);
                Toast.makeText(ChatActivity.this, message, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onResults(Bundle results) {
                ArrayList<String> data = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
                if (data != null && !data.isEmpty()) {
                    etMessage.setText(data.get(0));
                }
            }

            @Override
            public void onPartialResults(Bundle partialResults) {}

            @Override
            public void onEvent(int eventType, Bundle params) {}
        });
    }

    private void startListening() {
        speechRecognizer.startListening(speechRecognizerIntent);
    }

    private void sendMessage(String text) {
        addMessage(text, true);
        etMessage.setText("");
        
        pbLoading.setVisibility(View.VISIBLE);
        btnSend.setEnabled(false);

        ollamaService.sendMessage(text, new OllamaService.ChatCallback() {
            @Override
            public void onResponse(String response) {
                pbLoading.setVisibility(View.GONE);
                btnSend.setEnabled(true);
                addMessage(response, false);
            }

            @Override
            public void onError(String error) {
                pbLoading.setVisibility(View.GONE);
                btnSend.setEnabled(true);
                Toast.makeText(ChatActivity.this, error, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void addMessage(String text, boolean isUser) {
        messages.add(new Message(text, isUser));
        adapter.notifyItemInserted(messages.size() - 1);
        rvChat.scrollToPosition(messages.size() - 1);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (speechRecognizer != null) {
            speechRecognizer.destroy();
        }
    }
}