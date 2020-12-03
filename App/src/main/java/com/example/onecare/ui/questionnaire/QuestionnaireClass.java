package com.example.onecare.ui.questionnaire;

import java.util.List;

public class QuestionnaireClass {
    public int id;
    public boolean isCompleted;
    public List<QuestionClass> listQuestions;

   public QuestionnaireClass(int id, Boolean isCompleted, List<QuestionClass> listQuestions){
        this.id= id;
        this.isCompleted= isCompleted;
        this.listQuestions= listQuestions;
    }

    @Override
    public String toString() {
        return "Questionnaire "+ this.id ;

    }
}
