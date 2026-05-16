package com.example.individualproject;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

public class SpellingFragment extends Fragment implements TaskFragment {

    private EditText etAnswer;
    private Question question;
    private LinearLayout layoutErrorBlock;
    private TextView tvCorrectHint;
    private TextView tvStatusTitle;

    public static SpellingFragment newInstance(Question q) {
        SpellingFragment fragment = new SpellingFragment();
        Bundle args = new Bundle();
        args.putSerializable("q", q);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Используем твой task_spelling.xml (убедись, что добавил туда layoutErrorBlock как в прошлом сообщении)
        View v = inflater.inflate(R.layout.task_spelling, container, false);

        etAnswer = v.findViewById(R.id.etAnswer);
        TextView tvPrompt = v.findViewById(R.id.tvQuestionText);
        layoutErrorBlock = v.findViewById(R.id.layoutErrorBlock);
        tvCorrectHint = v.findViewById(R.id.tvCorrectHint);
        tvStatusTitle = v.findViewById(R.id.tvStatusTitle);
        if (getArguments() != null) {
            question = (Question) getArguments().getSerializable("q");
            if (question != null) {
                tvPrompt.setText("Как будет по-испански:\n\"" + question.getQuestionText() + "\"?");
            }
        }
        return v;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (getActivity() instanceof MainActivity) {
            MainActivity main = (MainActivity) getActivity();
            Button btnNext = main.findViewById(R.id.btnNext);
            if (btnNext != null) {
                btnNext.setVisibility(View.VISIBLE);
                btnNext.setEnabled(false);
                btnNext.setText("Далее");
            }
        }

        etAnswer.addTextChangedListener(new android.text.TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                boolean isNotEmpty = s.toString().trim().length() > 0;
                if (getActivity() instanceof MainActivity) {
                    ((MainActivity) getActivity()).setNextButtonEnabled(isNotEmpty);
                }
            }
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void afterTextChanged(android.text.Editable s) {}
        });

        // Клавиатура
        etAnswer.requestFocus();
        etAnswer.postDelayed(() -> {
            if (isAdded() && getContext() != null) {
                InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm != null) imm.showSoftInput(etAnswer, InputMethodManager.SHOW_IMPLICIT);
            }
        }, 50);
    }

    public void showError(String correctAnswer) {
        if (layoutErrorBlock != null) {
            layoutErrorBlock.setVisibility(View.VISIBLE);
            layoutErrorBlock.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.error_bg)); // Красный фон

            tvStatusTitle.setText("Неправильно");
            tvStatusTitle.setTextColor(ContextCompat.getColor(getContext(), R.color.error_text));

            tvCorrectHint.setText("Правильный ответ: " + correctAnswer);
            tvCorrectHint.setTextColor(ContextCompat.getColor(getContext(), R.color.text_black)); // Обычный текст

            etAnswer.setEnabled(false);
            etAnswer.setAlpha(0.6f);
        }
    }

    public void showSuccess() {
        if (layoutErrorBlock != null) {
            layoutErrorBlock.setVisibility(View.VISIBLE);
            layoutErrorBlock.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.correct_bg)); // Зеленый фон

            tvStatusTitle.setText("Правильно!");
            tvStatusTitle.setTextColor(ContextCompat.getColor(getContext(), R.color.correct_text));

            tvCorrectHint.setText("Отличная работа"); // Или просто скрыть: tvCorrectHint.setVisibility(View.GONE);
            tvCorrectHint.setTextColor(android.graphics.Color.parseColor("#2E7D32"));

            etAnswer.setEnabled(false);
        }
    }
    @Override
    public String getUserAnswer() {
        return etAnswer.getText().toString().trim();
    }
}