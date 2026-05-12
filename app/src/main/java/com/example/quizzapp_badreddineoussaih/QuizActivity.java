package com.example.quizzapp_badreddineoussaih;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.List;
import java.util.Locale;

public class QuizActivity extends AppCompatActivity {

    private TextView tvProgress, tvQuestion, tvScoreCounter;
    private ProgressBar progressBar;
    private RadioGroup rgOptions;
    private RadioButton rbOption1, rbOption2, rbOption3, rbOption4;
    private Button btnNext;
    private LinearLayout quizContainer;

    private List<Question> questionList;
    private int currentQuestionIndex = 0;
    private int score = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);

        tvProgress = findViewById(R.id.tvProgress);
        tvQuestion = findViewById(R.id.tvQuestion);
        tvScoreCounter = findViewById(R.id.tvScoreCounter);
        progressBar = findViewById(R.id.progressBar);
        rgOptions = findViewById(R.id.rgOptions);
        rbOption1 = findViewById(R.id.rbOption1);
        rbOption2 = findViewById(R.id.rbOption2);
        rbOption3 = findViewById(R.id.rbOption3);
        rbOption4 = findViewById(R.id.rbOption4);
        btnNext = findViewById(R.id.btnNext);
        quizContainer = findViewById(R.id.quizContainer);

        // Entrance Animation
        Animation fadeIn = AnimationUtils.loadAnimation(this, R.anim.fade_in);
        quizContainer.startAnimation(fadeIn);

        Animation glow = AnimationUtils.loadAnimation(this, R.anim.button_glow);
        btnNext.startAnimation(glow);

        questionList = QuestionRepository.getVideoGameQuestions();
        progressBar.setMax(questionList.size());

        loadQuestion();

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int selectedId = rgOptions.getCheckedRadioButtonId();
                if (selectedId == -1) {
                    Toast.makeText(QuizActivity.this, "MISSION PROTOCOL: Select an answer!", Toast.LENGTH_SHORT).show();
                    return;
                }

                RadioButton selectedRadioButton = findViewById(selectedId);
                int answerIndex = rgOptions.indexOfChild(selectedRadioButton);

                if (answerIndex == questionList.get(currentQuestionIndex).getCorrectAnswerIndex()) {
                    score++;
                }

                currentQuestionIndex++;

                if (currentQuestionIndex < questionList.size()) {
                    loadQuestion();
                    // Small flash animation for next question
                    quizContainer.startAnimation(AnimationUtils.loadAnimation(QuizActivity.this, android.R.anim.fade_in));
                } else {
                    Intent intent = new Intent(QuizActivity.this, Score.class);
                    intent.putExtra("score", score);
                    intent.putExtra("total", questionList.size());
                    startActivity(intent);
                    finish();
                }
            }
        });
    }

    private void loadQuestion() {
        rgOptions.clearCheck();
        Question currentQuestion = questionList.get(currentQuestionIndex);
        
        tvQuestion.setText(currentQuestion.getQuestion());
        String[] options = currentQuestion.getOptions();
        rbOption1.setText(options[0]);
        rbOption2.setText(options[1]);
        rbOption3.setText(options[2]);
        rbOption4.setText(options[3]);

        tvProgress.setText(String.format(Locale.US, "LEVEL: %02d/%d", (currentQuestionIndex + 1), questionList.size()));
        tvScoreCounter.setText(String.format(Locale.US, "XP: %03d", (score * 10)));
        progressBar.setProgress(currentQuestionIndex + 1);
    }
}