package com.DigitalHealth.Intervention.model;

import java.util.List;

public class QOLQuestionnaire {
	
	
	private List<Questions> questions;

	public QOLQuestionnaire(List<Questions> questions) {
		this.questions = questions;
	}
	
	public List<Questions> getQuestions(){
		return questions;
	}//
	
	public void setQuestions(List<Questions> questions){
		this.questions = questions;
	}
}
