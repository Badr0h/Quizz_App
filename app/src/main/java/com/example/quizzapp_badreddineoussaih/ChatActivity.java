package com.example.quizzapp_badreddineoussaih;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class ChatActivity extends AppCompatActivity {

    private RecyclerView rvChat;
    private ChatAdapter adapter;
    private List<Message> messages;
    private EditText etMessage;
    private ImageButton btnSend, btnBack;
    private ProgressBar pbLoading;
    private OllamaService ollamaService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        rvChat = findViewById(R.id.rvChat);
        etMessage = findViewById(R.id.etMessage);
        btnSend = findViewById(R.id.btnSend);
        btnBack = findViewById(R.id.btnBack);
        pbLoading = findViewById(R.id.pbLoading);

        messages = new ArrayList<>();
        adapter = new ChatAdapter(messages);
        rvChat.setLayoutManager(new LinearLayoutManager(this));
        rvChat.setAdapter(adapter);

        ollamaService = new OllamaService();

        // Objective 2: Add Back Button functionality
        btnBack.setOnClickListener(v -> finish());

        // Initial AI greeting
        addMessage("Mission Assistant online. How can I help you, player?", false);

        btnSend.setOnClickListener(v -> {
            String text = etMessage.getText().toString().trim();
            if (!TextUtils.isEmpty(text)) {
                sendMessage(text);
            }
        });
    }

    private void sendMessage(String text) {
        // Objective 3: Show user message instantly
        addMessage(text, true);
        etMessage.setText("");
        
        // Show loading indicator
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
                // Objective 1: Proper error handling
                Toast.makeText(ChatActivity.this, error, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void addMessage(String text, boolean isUser) {
        messages.add(new Message(text, isUser));
        adapter.notifyItemInserted(messages.size() - 1);
        // Objective 3: Ensure RecyclerView auto-scrolls to latest message
        rvChat.scrollToPosition(messages.size() - 1);
    }
}