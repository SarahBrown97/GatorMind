package com.DigitalHealth.Intervention.Service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;


import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Component;

import com.DigitalHealth.Intervention.model.QOLAnswers;
import com.DigitalHealth.Intervention.model.QOLQuestionnaire;
import com.DigitalHealth.Intervention.model.QOLQuestionnaireDescription;
import com.DigitalHealth.Intervention.model.QOLQuestionnaireList;
import com.DigitalHealth.Intervention.model.QOLResponseFromApp;
import com.DigitalHealth.Intervention.model.QuestionnaireFromDashboard;
import com.DigitalHealth.Intervention.model.Questions;

@Component
public class QOLService {
	
	public class getQuestionnaireRowMapper implements RowMapper {
		@Override
		public QOLQuestionnaireDescription  mapRow(ResultSet rs, int rowNum) throws SQLException {
			// TODO Auto-generated method stub
			QOLQuestionnaireDescription obj = new QOLQuestionnaireDescription();
			obj.setId(rs.getInt("questionnaire_id"));
			obj.setStatus(rs.getString("status"));
			return obj;
		}
	}
	
	public class getQuestionsRowMapper implements RowMapper {
		@Override
		public Questions  mapRow(ResultSet rs, int rowNum) throws SQLException {
			// TODO Auto-generated method stub
			Questions obj = new Questions();
			obj.setId(rs.getInt("question_id"));
			obj.setQuestion(rs.getString("question"));
			obj.setAnswer(rs.getString("answer"));
			obj.setType(rs.getString("type"));
			return obj;
		}
	}
	
	@Autowired
	NamedParameterJdbcTemplate namedjdbcTemplate;
	
	@Autowired
	UtilityService us;
	
	public void insertQuestionnaire(QuestionnaireFromDashboard response) {

			String sql = "INSERT INTO Questionnaire_table (patient_id, status)\r\n" + 
					"VALUES (:patient_id, :status);";
			SqlParameterSource namedParams = new MapSqlParameterSource("patient_id", response.getPatientId())
					.addValue("status", response.getNotes());
			namedjdbcTemplate.update(sql,namedParams);
	}
	
	public QOLQuestionnaireList getQuestionnaireList(String userID) {
		String sql = "select questionnaire_id, status from Questionnaire_table where patient_id = :patientID";
		SqlParameterSource namedParams = new MapSqlParameterSource("patientID",userID);
		List<QOLQuestionnaireDescription> questionnaireList = namedjdbcTemplate.query(sql, namedParams, new getQuestionnaireRowMapper());		
	    return new QOLQuestionnaireList(questionnaireList);
	}
	
	public QOLQuestionnaire getQuestionnaire(String userID, int questionnaireID) {
		
		String sql = "select COUNT(questionnaire_id) from Questions_table where questionnaire_id = :questionnaireID";
		SqlParameterSource namedParams = new MapSqlParameterSource("questionnaireID",questionnaireID);
		Integer count = namedjdbcTemplate.queryForObject(sql, namedParams, Integer.class);
		QOLQuestionnaire response;
		if(count == 0) {
			
			response = getQuestionnairePending(userID,questionnaireID);
		}
		else {
		    response = getQuestionnaireCompleted(userID,questionnaireID);
		}
		return response;
	}
	
	public void insertPendingQuestions(List<Questions> questions, int questionnaireID) {
		int count = 1;
		for(Questions q: questions) {
			String sql = "INSERT INTO Questions_table (questionnaire_id, question_id, question, type, answer)\r\n" + 
					"VALUES (:questionnaireID, :questionID, :Q, :T, :A);";
			SqlParameterSource namedParams = new MapSqlParameterSource("questionnaireID", questionnaireID)
					.addValue("questionID", count)
					.addValue("Q", q.getQuestion())
					.addValue("T", q.getType())
					.addValue("A", q.getAnswer());
			namedjdbcTemplate.update(sql,namedParams);
			count++;
		}
		
	}
	
	
	public QOLQuestionnaire getQuestionnairePending(String userID, int questionnaireID) {
		double calling = us.getCallScore(userID, 7*24);
		double tone = us.getToneScore(userID, 7*24);
		double deviceUsage = us.getDeviceUsage(userID, 7*24);
		double physicalActivity = us.getPhysicalActivityScore(userID, 7*24);
		double ambientNoise = us.getAmbientNoiseScore(userID, 7*24);
		double alarmMap = us.getAlarmScore(userID, 7*24);
		
		
		int count = 1;
		
		List<Questions> questions = new ArrayList<>();
		
		
		if(calling >= 50) {
			questions.add(new Questions(count, "Explain on what topics do you spend the most time on the phone", "Text"));
			count++;
		} else {
			questions.add(new Questions(count, "Are you trying to reduce time spent on phone calls?","Yes?No"));
			count++;
			questions.add(new Questions(count, "How connected do you think you are with your friends and family?","Rating"));
			count++;
		}
		
		if(tone >= 50) {
			questions.add(new Questions(count, "While messaging, how well aware are you of your surroundings","Rating"));
			count++;
		} else {
			questions.add(new Questions(count, "Do you feel sad, lonely or depressed?","Yes/No"));
			count++;
		}
		
		if(deviceUsage >= 50) {
			questions.add(new Questions(count, "Is your social media interactions affecting your daily tasks?","Rating"));
			count++;
		} else {
			questions.add(new Questions(count, "Are you in touch with your friends?","Yes/No"));
			count++;
		}//
		
		if(physicalActivity < 50) {
			questions.add(new Questions(count, "Does your body shape affect you mentally and hinders with your productivity?","Yes/No"));
			count++;
			questions.add(new Questions(count, "Are you facing any hinderence which might be preventing you from excercising?","Text"));
			count++;
			questions.add(new Questions(count, "Would you say you live a sedentary lifestyle?","Yes/No"));
			count++;
		} else {
			questions.add(new Questions(count, "Do you think you are physically healthy?","Yes/No"));
			count++;
		}
		
		if(ambientNoise >= 50) {
			questions.add(new Questions(count, "How stressful do you feel in your daily life?","Rating"));
			count++;
		} else {
			questions.add(new Questions(count, "Is the constant noise affecting your mental peace","Yes/No"));
			count++;
			questions.add(new Questions(count, "Have you faced any loss of hearing","Yes/No"));
			count++;
		}
		
		if(alarmMap < 50) {
			questions.add(new Questions(count, "What role does alcohol play in your daily life","Text"));
			count++;
			questions.add(new Questions(count, "Do you think you have become addicted to alcohol?","Yes/No"));
			count++;
			questions.add(new Questions(count, "Do you feel less stress when you consume alcohol?","Yes/No"));
			count++;
			
		}
		insertPendingQuestions(questions,questionnaireID);
		return new QOLQuestionnaire(questions);
	}
	
	public QOLQuestionnaire getQuestionnaireCompleted(String userID, int questionnaireID) {
		
        String sql = "select question_id, question, type, answer from Questions_table where questionnaire_id = :id";
        SqlParameterSource namedParams = new MapSqlParameterSource("id",questionnaireID);
        List<Questions> questions = namedjdbcTemplate.query(sql, namedParams, new getQuestionsRowMapper());
		return new QOLQuestionnaire(questions);
	}
	
	public void updateAnswersToTable(QOLResponseFromApp response) {
		SqlParameterSource namedParams;
		// UPDATE QUESTIONS TABLE
		for(QOLAnswers ans : response.getUserResponse()) {
			String sql = "UPDATE Questions_table\r\n" + 
					"SET answer = :ANS " + 
					"WHERE questionnaire_id  = :questionnaireID AND question_id = :questionID ;";
			namedParams = new MapSqlParameterSource("ANS",ans.getAnswer())
					.addValue("questionnaireID", response.getQuestionnaireID())
					.addValue("questionID", ans.getId());
			namedjdbcTemplate.update(sql,namedParams);
					
		}
		
		// UPDATE QUESTIONNAIRE TABLE
		String sql_updateQuestionnaire = "UPDATE Questionnaire_table \r\n" + 
									"SET status = 'Completed' " + 
									"WHERE questionnaire_id  = :questionnaireID ;";
		namedParams = new MapSqlParameterSource("questionnaireID",response.getQuestionnaireID());
		namedjdbcTemplate.update(sql_updateQuestionnaire,namedParams);
		
		// UPDATE WORK LIST TABLE
		String sql_updateWorkList = "INSERT INTO worklist_Table (userId, message, status, timestamp)\r\n" + 
				"VALUES (:userID, :message, :status, :timestp);";
		java.util.Date date = new java.util.Date();
		Object timeStamp = new java.sql.Timestamp(date.getTime());
		
		namedParams = new MapSqlParameterSource("userID", response.getUserID())
				.addValue("message", "Completed QOL "+response.getQuestionnaireID())
				.addValue("status", "Pending")
				.addValue("timestp", timeStamp);
       
		namedjdbcTemplate.update(sql_updateWorkList,namedParams);
		
		
		
	}
	
}
