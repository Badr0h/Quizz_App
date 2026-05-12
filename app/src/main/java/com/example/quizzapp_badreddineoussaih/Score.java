package com.example.quizzapp_badreddineoussaih;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class Score extends AppCompatActivity {
    private Button bLogout, bTry;
    private ProgressBar progressBar;
    private TextView tvScore, tvScoreDetails, tvLocation;
    private LinearLayout scoreContent;
    private int score, total;
    
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_score);

        tvScore = findViewById(R.id.tvScore);
        tvScoreDetails = findViewById(R.id.tvScoreDetails);
        tvLocation = findViewById(R.id.tvLocation);
        progressBar = findViewById(R.id.progressBar);
        bLogout = findViewById(R.id.bLogout);
        bTry = findViewById(R.id.bTry);
        scoreContent = findViewById(R.id.scoreContent);

        // Animations
        Animation fadeIn = AnimationUtils.loadAnimation(this, R.anim.fade_in);
        scoreContent.startAnimation(fadeIn);

        Animation glow = AnimationUtils.loadAnimation(this, R.anim.button_glow);
        bTry.startAnimation(glow);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        Intent intent = getIntent();
        score = intent.getIntExtra("score", 0);
        total = intent.getIntExtra("total", 20);

        int percentage = (int) (((float) score / total) * 100);
        tvScore.setText(String.format(Locale.US, "%d%%", percentage));
        tvScoreDetails.setText(String.format(Locale.US, "XP GAINED: %d", (score * 100)));
        progressBar.setProgress(percentage);

        // Display Location Data
        if (LocationHelper.isLocationReady) {
            tvLocation.setText(String.format(Locale.US, "LOCATION: %s [%.2f, %.2f]", 
                LocationHelper.cityName.toUpperCase(), LocationHelper.latitude, LocationHelper.longitude));
        } else {
            tvLocation.setText("LOCATION: SIGNAL LOST");
        }

        saveResultToFirebase();

        bLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAuth.signOut();
                Toast.makeText(getApplicationContext(), "MISSION ABORTED. BYE!", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(Score.this, MainActivity.class));
                finish();
            }
        });

        bTry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Score.this, QuizActivity.class));
                finish();
            }
        });
    }

    private void saveResultToFirebase() {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user == null) return;

        Map<String, Object> result = new HashMap<>();
        result.put("userId", user.getUid());
        result.put("email", user.getEmail());
        result.put("score", score);
        result.put("total", total);
        result.put("percentage", (int) (((float) score / total) * 100));
        result.put("timestamp", com.google.firebase.Timestamp.now());
        result.put("latitude", LocationHelper.latitude);
        result.put("longitude", LocationHelper.longitude);
        result.put("city", LocationHelper.cityName);

        db.collection("quiz_results")
                .add(result)
                .addOnSuccessListener(documentReference -> {
                    // Sync success
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(Score.this, "DATA SYNC FAILED", Toast.LENGTH_SHORT).show();
                });
    }
}