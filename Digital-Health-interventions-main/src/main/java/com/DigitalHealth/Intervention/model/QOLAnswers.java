package com.DigitalHealth.Intervention.model;

public class QOLAnswers {
	
	private int id;
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}

	private String answer;
	public QOLAnswers() {
		
	}
	public String getAnswer() {
		return answer;
	}
	public void setAnswer(String answer) {
		this.answer = answer;
	}
	
	@Override
	public String toString() {
		return String.format("question: "+id +" answer: "+ answer);
	}
}
