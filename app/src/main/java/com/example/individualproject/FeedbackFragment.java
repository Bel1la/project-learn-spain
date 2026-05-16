package com.example.individualproject;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.fragment.app.Fragment;

public class FeedbackFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_feedback, container, false);
        TextView tv = v.findViewById(R.id.tvFeedback);

        // Рандомим надпись
        String[] phrases = {"Так держать!", "Хорошо!", "¡Muy bien!"};
        tv.setText(phrases[(int) (Math.random() * phrases.length)]);

        // Через 1.5 секунды переключаем на следующий вопрос
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            if (isAdded() && getActivity() != null) {
                // Вызываем метод в MainActivity, который обычно привязан к кнопке "Далее"
                ((MainActivity) getActivity()).onNextPressed();
            }
        }, 1500);

        return v;
    }
}