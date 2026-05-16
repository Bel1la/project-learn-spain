package com.example.individualproject;

import android.content.Context;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random; // Добавил импорт

public class Dictionary {

    public static class WordPair {
        String rus;
        String esp;
        public WordPair(String rus, String esp) {
            this.rus = rus;
            this.esp = esp;
        }
    }

    public static List<Question> generateCourse(Context context, int size) {
        List<Question> questions = new ArrayList<>();
        List<WordPair> vocabulary = new ArrayList<>();

        String[] rawArray = context.getResources().getStringArray(R.array.spanish_vocab);

        for (String item : rawArray) {
            String[] parts = item.split("\\|");
            if (parts.length == 2) {
                vocabulary.add(new WordPair(parts[0], parts[1]));
            }
        }

        Collections.shuffle(vocabulary);
        Random random = new Random(); // Используем для случайного выбора типа

        for (int i = 0; i < Math.min(size, vocabulary.size()); i++) {
            WordPair pair = vocabulary.get(i);

            // РАНДОМ ТИПА ЗАДАНИЯ
            int randomType = random.nextInt(3);

            if (randomType == 0) {
                boolean isTrue = random.nextBoolean();
                if (isTrue) {
                    // Правильный вопрос: Текст совпадает с парой
                    questions.add(new Question(Question.Type.YES_NO,
                            "Слово '" + pair.esp + "' означает '" + pair.rus + "'?",
                            pair.esp + " — это " + pair.rus, // Подсказка для блока ошибок
                            true)); // Флаг: это ПРАВДА
                } else {
                    // Ложный вопрос: берем случайное слово
                    int randomIndex = random.nextInt(vocabulary.size());
                    if (randomIndex == i) randomIndex = (i + 1) % vocabulary.size();
                    WordPair wrongPair = vocabulary.get(randomIndex);

                    questions.add(new Question(Question.Type.YES_NO,
                            "Слово '" + wrongPair.esp + "' означает '" + pair.rus + "'?",
                            wrongPair.esp + " — это " + wrongPair.rus, // Подсказка
                            false)); // Флаг: это ЛОЖЬ
                }
            } else if (randomType == 1) {
                questions.add(new Question(Question.Type.SPELLING, pair.rus, pair.esp));
            } else {
                questions.add(new Question(Question.Type.MATCHING, "Сопоставьте пары", "Matched"));
            }
        }

        Collections.shuffle(questions); // Еще раз перемешиваем сами вопросы
        return questions;
    }

    // ОСТАВЛЯЕМ ТВОЙ МЕТОД БЕЗ ИЗМЕНЕНИЙ (чтобы MatchingFragment его видел)
    public static List<WordPair> getRandomPairs(Context context, int count) {
        List<WordPair> allWords = new ArrayList<>();
        String[] rawArray = context.getResources().getStringArray(R.array.spanish_vocab);

        for (String item : rawArray) {
            String[] parts = item.split("\\|");
            if (parts.length == 2) allWords.add(new WordPair(parts[0], parts[1]));
        }

        Collections.shuffle(allWords);
        return allWords.subList(0, Math.min(count, allWords.size()));
    }
}