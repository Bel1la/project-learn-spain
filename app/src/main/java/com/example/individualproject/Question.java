package com.example.individualproject;

import java.io.Serializable;
import java.text.Normalizer;

public class Question implements Serializable {
    public enum Type { YES_NO, SPELLING, MATCHING }

    private String questionText;
    private String correctAnswer;
    private String userAnswer;
    private Type type;
    private boolean isCorrect;
    private int errorCount = 0;
    private boolean isQuestionTrue;

    public Question(Type type, String questionText, String correctAnswer) {
        this(type, questionText, correctAnswer, true);
    }

    // Твой текущий конструктор (убедись, что он такой)
    public Question(Type type, String questionText, String correctAnswer, boolean isQuestionTrue) {
        this.type = type;
        this.questionText = questionText;
        this.correctAnswer = correctAnswer;
        this.isQuestionTrue = isQuestionTrue;
    }
    public boolean isQuestionTrue() { return isQuestionTrue; }
    public void addError() { this.errorCount++; }
    public int getErrorCount() { return errorCount; }

    public String getQuestionText() { return questionText; }
    public String getCorrectAnswer() { return correctAnswer; }
    public String getUserAnswer() { return userAnswer; }
    public Type getType() { return type; }
    public boolean isCorrect() { return isCorrect; }

    public void setUserAnswer(String userAnswer) {
        this.userAnswer = userAnswer;
        if (userAnswer == null) {
            this.isCorrect = false;
            return;
        }
        String cleanCorrect = removeAccents(correctAnswer).toLowerCase().trim();
        String cleanUser = removeAccents(userAnswer).toLowerCase().trim();
        this.isCorrect = cleanCorrect.equals(cleanUser);
    }

    private String removeAccents(String text) {
        if (text == null) return "";
        String normalized = Normalizer.normalize(text, Normalizer.Form.NFD);
        return normalized.replaceAll("\\p{M}", "");
    }
}