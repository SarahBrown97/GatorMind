package com.DigitalHealth.Intervention.model;

public class QuestionnaireFromDashboard {
	private String patientId;
	private String status;
	
	public QuestionnaireFromDashboard(String patientId, String status) {
		super();
		this.patientId = patientId;
		this.status = status;
	}

	public String getPatientId() {
		return patientId;
	}

	public void setPatientId(String patientId) {
		this.patientId = patientId;
	}

	public String getNotes() {
		return status;
	}

	public void setNotes(String status) {
		this.status = status;
	}
	
	
}
