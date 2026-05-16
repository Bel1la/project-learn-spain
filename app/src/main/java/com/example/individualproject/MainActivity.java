package com.example.individualproject;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private View layoutStart, layoutQuiz;
    private ProgressBar progressBar;
    private Button btnNext;
    private List<Question> questions;
    private int currentQuestionIndex = 0;
    private boolean isHintVisible = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        com.google.android.material.color.DynamicColors.applyToActivitiesIfAvailable(getApplication());

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        layoutStart = findViewById(R.id.layoutStart);
        layoutQuiz = findViewById(R.id.layoutQuiz);
        progressBar = findViewById(R.id.courseProgressBar);
        btnNext = findViewById(R.id.btnNext);

        findViewById(R.id.btnStartCourse).setOnClickListener(v -> startLearning());
        btnNext.setOnClickListener(v -> handleNextClick());
    }

    private void startLearning() {
        questions = Dictionary.generateCourse(this, 10);
        layoutStart.setVisibility(View.GONE);
        layoutQuiz.setVisibility(View.VISIBLE);
        displayQuestion();
    }

    private void displayQuestion() {
        isHintVisible = false;
        // Просто скрываем кнопку. Теперь ФРАГМЕНТ скажет, когда её показать.
        btnNext.setVisibility(View.GONE);

        if (currentQuestionIndex < questions.size()) {
            Question q = questions.get(currentQuestionIndex);
            progressBar.setProgress((int) (((float) currentQuestionIndex / questions.size()) * 100));

            Fragment fragment;
            switch (q.getType()) {
                case YES_NO: fragment = YesNoFragment.newInstance(q); break;
                case SPELLING: fragment = SpellingFragment.newInstance(q); break;
                default: fragment = MatchingFragment.newInstance(q); break;
            }

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.task_container, fragment)
                    .commit();
        } else {
            finishCourse();
        }
    }
    public void activatePostErrorState() {
        isHintVisible = true;
        btnNext.setVisibility(View.VISIBLE);
        btnNext.setEnabled(true);
        btnNext.setText("Понятно");
    }
    // Внутри MainActivity.java

    private void handleNextClick() {
        Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.task_container);
        if (!(currentFragment instanceof TaskFragment)) return;

        Question currentQuestion = questions.get(currentQuestionIndex);
        if (isHintVisible) {
            showFeedbackWithDelay(0); // Если это экран ошибки, задержка не нужна
            return;
        }

        String answer = ((TaskFragment) currentFragment).getUserAnswer();
        currentQuestion.setUserAnswer(answer);

        if (currentQuestion.isCorrect()) {
            if (currentFragment instanceof SpellingFragment) {
                ((SpellingFragment) currentFragment).showSuccess();
            }
            // Запускаем задержку для всех успешных ответов
            showFeedbackWithDelay(1000);
        } else {
            currentQuestion.addError();
            isHintVisible = true;
            if (currentFragment instanceof SpellingFragment) {
                ((SpellingFragment) currentFragment).showError(currentQuestion.getCorrectAnswer());
            }
            btnNext.setText("Понятно");
        }
    }

    // 2. Создаем новый метод для управления паузой
    public void showFeedbackWithDelay(int delayMillis) {
        btnNext.setEnabled(false); // Чтобы не кликали во время паузы
        new android.os.Handler(android.os.Looper.getMainLooper()).postDelayed(() -> {
            if (!isFinishing()) {
                showFeedback();
                btnNext.setEnabled(true);
            }
        }, delayMillis);
    }

    // Оставляем обычный showFeedback для мгновенной смены фрагмента
    public void showFeedback() {
        btnNext.setVisibility(View.GONE);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.task_container, new FeedbackFragment())
                .commit();
    }

    public void onNextPressed() {
        currentQuestionIndex++;
        displayQuestion();
    }

    private void finishCourse() {
        Intent intent = new Intent(this, ResultActivity.class);
        intent.putExtra("questions", (ArrayList<Question>) questions);
        startActivity(intent);
        finish();
    }

    public void setNextButtonEnabled(boolean enabled) {
        if (btnNext != null) {
            btnNext.setEnabled(enabled);
            btnNext.setTextColor(android.graphics.Color.parseColor(enabled ? "#FFFFFF" : "#80FFFFFF"));
        }
    }
}