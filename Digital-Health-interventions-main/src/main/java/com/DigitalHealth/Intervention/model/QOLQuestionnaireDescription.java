package com.DigitalHealth.Intervention.model;

public class QOLQuestionnaireDescription {
	int id;
	String status;
	
	public QOLQuestionnaireDescription( int id, String status) {
		super();
		this.id = id;
		this.status = status;
	}
	
	public QOLQuestionnaireDescription() {}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}
	
	
	
}
