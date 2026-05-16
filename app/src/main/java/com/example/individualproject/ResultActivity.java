package com.example.individualproject;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;

public class ResultActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        ArrayList<Question> allQuestions = (ArrayList<Question>) getIntent().getSerializableExtra("questions");
        if (allQuestions == null) return;

        int totalQuestions = allQuestions.size();
        int earnedPoints = 0;
        int totalPenalty = 0;

        for (Question q : allQuestions) {
            if (q.getType() == Question.Type.MATCHING) {
                // Мэтчинг дает базу в 10 баллов
                earnedPoints += 10;
                // Каждая ошибка — это минус 1 балл ИЗ ОБЩЕГО зачета
                totalPenalty += q.getErrorCount();
            } else {
                // Spelling и Yes/No дают 10 баллов только за чистый ответ
                if (q.isCorrect() && q.getErrorCount() == 0) {
                    earnedPoints += 10;
                }
            }
        }

        // Тот самый расчет процентов
        int maxPossiblePoints = totalQuestions * 10;
        int finalScore = earnedPoints - totalPenalty;

        // Математика: (Текущие / Максимум) * 100
        // Math.max(0, ...) — чтобы не уйти в минус, если ошибок больше, чем баллов
        int percent = Math.max(0, (finalScore * 100) / maxPossiblePoints);

        TextView tvScore = findViewById(R.id.tvScore);
        TextView tvFeedback = findViewById(R.id.tvErrorHeader);

        tvScore.setText(percent + "%"); // Вот здесь выведутся твои 98% или 88%
        tvFeedback.setText(getFeedbackMessage(percent));

        // Кнопки управления (исправленный переход на главную)
        findViewById(R.id.btnFinish).setOnClickListener(v -> goToMain());

        Button btnRetry = findViewById(R.id.btnRetry);
        if (percent < 70) {
            btnRetry.setVisibility(View.VISIBLE);
            btnRetry.setOnClickListener(v -> goToMain());
        }
    }

    private void goToMain() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }

    private String getFeedbackMessage(int p) {
        if (p == 100) return "0 ошибок, ты полиглот!";
        if (p >= 80) return "Хорошие знания";
        if (p >= 70) return "Есть к чему стремиться";
        return "Нужно хорошенько изучить тему";
    }
}