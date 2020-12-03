package com.example.onecare.ui.questionnaire;

public class QuestionClass {
   public enum Type{
        RATING,
        RADIO,
        TEXT
    }
    public int id;
    public String question;
    public Type type;
    public String answer;

    public QuestionClass(int id, String question, Type type, String answer){
        this.id= id;
        this.question= question;
        this.type= type;
        this.answer= answer;
    }

}
