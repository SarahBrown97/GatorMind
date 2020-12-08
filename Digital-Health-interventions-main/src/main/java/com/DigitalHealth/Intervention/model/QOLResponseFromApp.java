package com.DigitalHealth.Intervention.model;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;
@Component
public class QOLResponseFromApp {

	private List<QOLAnswers> userResponse = new ArrayList();
	private String userID;
	private String questionnaireID;

	public List<QOLAnswers> getUserResponse() {
		return userResponse;
	}

	public void setUserResponse(List<QOLAnswers> userResponse) {
		this.userResponse = userResponse;
	}

	public String getQuestionnaireID() {
		return questionnaireID;
	}

	public void setQuestionnaireID(String questionnaireID) {
		this.questionnaireID = questionnaireID;
	}

	public String getUserID() {
		return userID;
	}

	public void setUserID(String userID) {
		this.userID = userID;
	}

	@Override
	public String toString() {
		return String.format("userID: "+userID.toString() +" questionnaireID: " + questionnaireID + " answers: "+ userResponse.toString());
	}

}
