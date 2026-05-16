package com.example.individualproject;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MatchingFragment extends Fragment implements TaskFragment {

    private LinearLayout containerLeft, containerRight;
    private List<Dictionary.WordPair> pairs = new ArrayList<>();
    private Button selectedLeft = null, selectedRight = null;
    private int matchedCount = 0;

    // 1. ОБЪЯВЛЯЕМ ПЕРЕМЕННУЮ ЗДЕСЬ
    private Question question;

    public static MatchingFragment newInstance(Question q) {
        MatchingFragment fragment = new MatchingFragment();
        Bundle args = new Bundle();
        args.putSerializable("q", q);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.task_matching, container, false);
        containerLeft = v.findViewById(R.id.containerLeft);
        containerRight = v.findViewById(R.id.containerRight);

        // 2. ИНИЦИАЛИЗИРУЕМ ПЕРЕМЕННУЮ ИЗ АРГУМЕНТОВ
        if (getArguments() != null) {
            question = (Question) getArguments().getSerializable("q");
        }

        setupGame();
        return v;
    }

    private void setupGame() {
        pairs = Dictionary.getRandomPairs(getContext(), 5);
        List<String> leftWords = new ArrayList<>();
        List<String> rightWords = new ArrayList<>();

        for (Dictionary.WordPair p : pairs) {
            leftWords.add(p.esp);
            rightWords.add(p.rus);
        }

        Collections.shuffle(leftWords);
        Collections.shuffle(rightWords);

        for (String s : leftWords) containerLeft.addView(createWordButton(s, true));
        for (String s : rightWords) containerRight.addView(createWordButton(s, false));
    }

    private Button createWordButton(String word, boolean isLeft) {
        androidx.appcompat.widget.AppCompatButton btn = new androidx.appcompat.widget.AppCompatButton(getContext());
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.setMargins(0, 8, 0, 8);
        btn.setLayoutParams(params);
        btn.setText(word);
        btn.setBackgroundResource(R.drawable.button_matching_default);
        btn.setTextColor(android.graphics.Color.BLACK);

        btn.setOnClickListener(v -> {
            if (isLeft) {
                if (selectedLeft != null) selectedLeft.setBackgroundResource(R.drawable.button_matching_default);
                selectedLeft = btn;
                btn.setBackgroundResource(R.drawable.button_matching_selected);
            } else {
                if (selectedRight != null) selectedRight.setBackgroundResource(R.drawable.button_matching_default);
                selectedRight = btn;
                btn.setBackgroundResource(R.drawable.button_matching_selected);
            }
            checkMatch();
        });
        return btn;
    }

    private void checkMatch() {
        if (selectedLeft != null && selectedRight != null) {
            String leftWord = selectedLeft.getText().toString();
            String rightWord = selectedRight.getText().toString();

            boolean isMatch = false;
            for (Dictionary.WordPair p : pairs) {
                if (p.esp.equals(leftWord) && p.rus.equals(rightWord)) {
                    isMatch = true;
                    break;
                }
            }

            if (isMatch) {
                selectedLeft.setTextColor(ContextCompat.getColor(getContext(), R.color.correct_text));
                selectedRight.setTextColor(ContextCompat.getColor(getContext(), R.color.correct_text));
                selectedLeft.setBackgroundResource(R.drawable.button_matching_correct);
                selectedRight.setBackgroundResource(R.drawable.button_matching_correct);
                selectedLeft.setEnabled(false);
                selectedRight.setEnabled(false);

                matchedCount++;
                if (matchedCount == 5) {
                    if (getActivity() instanceof MainActivity) {

                        ((MainActivity) getActivity()).showFeedbackWithDelay(800);
                    }
                }
            } else {
                if (question != null) {
                    question.addError();
                }
                selectedLeft.setTextColor(ContextCompat.getColor(getContext(), R.color.error_text));
                selectedRight.setTextColor(ContextCompat.getColor(getContext(), R.color.error_text));
                selectedLeft.setBackgroundResource(R.drawable.button_matching_error);
                selectedRight.setBackgroundResource(R.drawable.button_matching_error);


                final Button b1 = selectedLeft;
                final Button b2 = selectedRight;
                b1.postDelayed(() -> {
                    b1.setBackgroundResource(R.drawable.button_matching_default);
                    b2.setBackgroundResource(R.drawable.button_matching_default);
                    b1.setTextColor(android.graphics.Color.BLACK);
                    b2.setTextColor(android.graphics.Color.BLACK);
                }, 500);
            }
            selectedLeft = null;
            selectedRight = null;
        }
    }

    @Override
    public String getUserAnswer() { return matchedCount >= 5 ? "Matched" : ""; }
}