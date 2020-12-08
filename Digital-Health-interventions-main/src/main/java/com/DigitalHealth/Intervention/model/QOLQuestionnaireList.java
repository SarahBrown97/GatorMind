package com.DigitalHealth.Intervention.model;

import java.util.ArrayList;
import java.util.List;

public class QOLQuestionnaireList {
	private List<QOLQuestionnaireDescription> questionnaires;
	
	public QOLQuestionnaireList() {
		questionnaires = new ArrayList<QOLQuestionnaireDescription>();
	}

	public QOLQuestionnaireList(List<QOLQuestionnaireDescription> questionnaires) {
		this.questionnaires = questionnaires;
	}
	
	public List<QOLQuestionnaireDescription> getQuestions(){
		return questionnaires;
	}//
	
	public void setQuestions(List<QOLQuestionnaireDescription> questionnaires){
		this.questionnaires = questionnaires;
	}
}
