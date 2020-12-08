package com.DigitalHealth.Intervention.model;

public class Questions{
	int id;
	String question;
	String type;
	String answer;
	
	
	
	public Questions(int id, String question, String type) {
		super();
		this.id = id;
		this.question = question;
		this.type = type;
		this.answer = "";
	}
	
	public Questions() {
	}
	
	public Questions(int id, String question, String type, String answer) {
		super();
		this.id = id;
		this.question = question;
		this.type = type;
		this.answer = answer;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getQuestion() {
		return question;
	}
	public void setQuestion(String question) {
		this.question = question;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getAnswer() {
		return answer;
	}
	public void setAnswer(String answer) {
		this.answer = answer;
	}
	
	
	
	
	
	
}
