package com.example.individualproject;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

public class YesNoFragment extends Fragment implements TaskFragment {

    private Question question;
    private ImageButton btnYes, btnNo;
    private LinearLayout layoutErrorBlock;
    private TextView tvCorrectHint, tvStatusTitle;

    public static YesNoFragment newInstance(Question q) {
        YesNoFragment fragment = new YesNoFragment();
        Bundle args = new Bundle();
        args.putSerializable("question", q);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            question = (Question) getArguments().getSerializable("question");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.task_yes_no, container, false);

        TextView tvQuestion = v.findViewById(R.id.tvQuestion);
        btnYes = v.findViewById(R.id.btnYes);
        btnNo = v.findViewById(R.id.btnNo);
        layoutErrorBlock = v.findViewById(R.id.layoutErrorBlock);
        tvCorrectHint = v.findViewById(R.id.tvCorrectHint);

        // ВНИМАНИЕ: Нужно найти tvStatusTitle, иначе handleSelection вылетит с ошибкой!
        tvStatusTitle = v.findViewById(R.id.tvStatusTitle);

        if (question != null) {
            tvQuestion.setText(question.getQuestionText());
        }

        btnYes.setOnClickListener(view -> checkAnswer(true, btnYes));
        btnNo.setOnClickListener(view -> checkAnswer(false, btnNo));

        return v;
    }

    private void checkAnswer(boolean userClickYes, ImageButton selectedBtn) {
        if (question == null) return;

        boolean isCorrect = (userClickYes == question.isQuestionTrue());

        btnYes.setEnabled(false);
        btnNo.setEnabled(false);

        if (isCorrect) {
            selectedBtn.setColorFilter(ContextCompat.getColor(requireContext(), android.R.color.holo_green_dark));

            question.setUserAnswer(question.getCorrectAnswer());

            if (getActivity() instanceof MainActivity) {
                ((MainActivity) getActivity()).showFeedbackWithDelay(800);
            }
        } else {
            selectedBtn.setColorFilter(ContextCompat.getColor(requireContext(), android.R.color.holo_red_dark));

            question.addError();
            question.setUserAnswer("WRONG");
            tvStatusTitle.setText("Неправильно");
            tvStatusTitle.setTextColor(ContextCompat.getColor(getContext(), R.color.error_text));

            if (layoutErrorBlock != null) {
                layoutErrorBlock.setVisibility(View.VISIBLE);
                tvCorrectHint.setText(question.getCorrectAnswer());
            }

            if (getActivity() instanceof MainActivity) {
                ((MainActivity) getActivity()).activatePostErrorState();
            }
        }
    }

    @Override
    public String getUserAnswer() {
        return question != null ? question.getUserAnswer() : "";
    }
}