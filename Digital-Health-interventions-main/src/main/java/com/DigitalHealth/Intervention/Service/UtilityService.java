package com.DigitalHealth.Intervention.Service;

import java.util.Calendar;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

@Component
public class UtilityService {
	
	public long getTimestamp(int windowInHours) {
		Date currentDate = new Date();
		Calendar cal = Calendar.getInstance();
		cal.setTime(currentDate);
		cal.add(Calendar.HOUR, -windowInHours);
		return cal.getTimeInMillis();
	}

	@Autowired
	NamedParameterJdbcTemplate namedjdbcTemplate;

	public int getCallDuration(String userID, int windowInHours) {

		
		long windowMilli = getTimestamp(windowInHours);
		
		String sql = "SELECT SUM(JSON_EXTRACT(data, '$.call_duration')) \r\n"
				+ "AS 'call_duration' FROM mhdp.calls where device_id IN (SELECT device_id FROM mhdp.device_id where user_id = :userID) \r\n"
				+ "AND (JSON_EXTRACT(data, '$.timestamp')) > :windowMilli  group by device_id;";

		SqlParameterSource namedParams = new MapSqlParameterSource("userID", userID).addValue("windowMilli",
				windowMilli);
		int callDuration;
		try {
			callDuration = namedjdbcTemplate.queryForObject(sql, namedParams, Integer.class);
			return callDuration;

		} catch (Exception e) {
			System.out.println(e.getStackTrace());
			return -1;
		}
	}
	
	public double getDeviceUsage(String userID, int windowInHours) {

		
		long windowMilli = getTimestamp(windowInHours);
		
		String sql = "SELECT SUM(JSON_EXTRACT(data, '$.elapsed_device_on')) \r\n"
				+ "AS 'device_usage' FROM mhdp.plugin_device_usage where device_id IN (SELECT device_id FROM mhdp.device_id where user_id = :userID) \r\n"
				+ "AND (JSON_EXTRACT(data, '$.timestamp')) > :windowMilli  group by device_id;";

		SqlParameterSource namedParams = new MapSqlParameterSource("userID", userID).addValue("windowMilli",
				windowMilli);
		double deviceUsage;
		try {
			deviceUsage = namedjdbcTemplate.queryForObject(sql, namedParams, Integer.class);
			return deviceUsage;

		} catch (Exception e) {
			System.out.println(e.getStackTrace());
			return -1;
		}
	}
	
	public int getToneAnalysis(String userID, int windowInHours) {

		
		long windowMilli = getTimestamp(windowInHours);
		
		String sql = "SELECT SUM(JSON_EXTRACT(data, '$.tone')) \r\n"
				+ "AS 'tone_count' FROM mhdp.tone_analysis where device_id IN (SELECT device_id FROM mhdp.device_id where user_id = :userID) \r\n"
				+ "AND (JSON_EXTRACT(data, '$.timestamp')) > :windowMilli AND (JSON_EXTRACT(data, '$.tone')) IN ('ANGER','SADNESS', 'FEAR') group by device_id;";

		SqlParameterSource namedParams = new MapSqlParameterSource("userID", userID).addValue("windowMilli",
				windowMilli);
		int callDuration;
		try {
			callDuration = namedjdbcTemplate.queryForObject(sql, namedParams, Integer.class);
			return callDuration;

		} catch (Exception e) {
			System.out.println(e.getStackTrace());
			return -1;
		}
	}
	
	public int getAmbientNoise(String userID, int windowInHours) {

		
		long windowMilli = getTimestamp(windowInHours);
		
		String sql = "SELECT SUM(JSON_EXTRACT(data, '$.high_noise_time')) \r\n"
				+ "AS 'high_noise_time' FROM mhdp.plugin_noise_analysis where device_id IN (SELECT device_id FROM mhdp.device_id where user_id = :userID) \r\n"
				+ "AND (JSON_EXTRACT(data, '$.timestamp')) > :windowMilli"
				+ " group by device_id;";

		SqlParameterSource namedParams = new MapSqlParameterSource("userID", userID).addValue("windowMilli",
				windowMilli);
		int loudNoiseInstances;
		try {
			loudNoiseInstances = namedjdbcTemplate.queryForObject(sql, namedParams, Integer.class);
			return loudNoiseInstances;

		} catch (Exception e) {
			System.out.println(e.getStackTrace());
			return -1;
		}
	}
	
	public int getPhysicalActivity(String userID, int windowInHours) {

		
		long windowMilli = getTimestamp(windowInHours);
		
		String sql = "SELECT SUM((JSON_EXTRACT(data, '$.high_activity_time')*2)+(JSON_EXTRACT(data, '$.moderate_activity_time'))) \r\n"
				+ "AS 'physical_activity' FROM mhdp.plugin_activity_analysis where device_id IN(SELECT device_id FROM mhdp.device_id where user_id = :userID) \r\n"
				+ "AND (JSON_EXTRACT(data, '$.timestamp')) > :windowMilli \r\n"
				+ "group by device_id;";

		SqlParameterSource namedParams = new MapSqlParameterSource("userID", userID).addValue("windowMilli",
				windowMilli);
		int activity;
		try {
			activity = namedjdbcTemplate.queryForObject(sql, namedParams, Integer.class);
			return activity;

		} catch (Exception e) {
			System.out.println(e.getStackTrace());
			return -1;
		}
	}
	

	
	public int getAlarmMap(String userID, int windowInHours) {

		
		long windowMilli = getTimestamp(windowInHours);
		
		String sql = "SELECT SUM(JSON_EXTRACT(data, '$.no_of_visits')) \r\n"
				+ "AS 'frequency_of_visit' FROM mhdp.plugin_alarm_map where device_id IN (SELECT device_id FROM mhdp.device_id where user_id = :userID) \r\n"
				+ "AND (JSON_EXTRACT(data, '$.timestamp')) > :windowMilli AND (JSON_EXTRACT(data, '$.type_of_place')) IN ('LIQUOR_STORE','BAR')"
				+ " group by device_id;";

		SqlParameterSource namedParams = new MapSqlParameterSource("userID", userID).addValue("windowMilli",
				windowMilli);
		int alarmCount;
		try {
			alarmCount = namedjdbcTemplate.queryForObject(sql, namedParams, Integer.class);
			return alarmCount;

		} catch (Exception e) {
			System.out.println(e.getStackTrace());
			return -1;
		}
	}

	public double getCallScore(String userID,  int windowInHours){
		double callDuration = getCallDuration(userID, windowInHours);
		double score = 50;
		if(callDuration > 35*60) {
			score = 0;
		} else if(callDuration >= 0 & callDuration <= 3.5*60){
			score = (double)callDuration / (double) (0.035*60);
		} else if(callDuration > 3.5*60) {
			score = 100 - ((((double)callDuration - (3.5*60)) / ((35-3.5)*60))*100);
		}
		return Math.round(score*100)/(double)100;
	}
	
	public double getPhysicalActivityScore(String userID,  int windowInHours){
		double physicalActivity = getPhysicalActivity(userID, windowInHours);
		double score = 50;
		if(physicalActivity > 150) {
			score = 100;
		} else if(physicalActivity != -1){
			score = (double)physicalActivity / (double) (1.5);
		}
		return Math.round(score*100)/(double)100;
	}
	
	public double getAmbientNoiseScore(String userID,  int windowInHours){
		double ambientNoise = getAmbientNoise(userID, windowInHours);
		double score = 50;
		if(ambientNoise > 60) {
			score = 0;
		} else if(ambientNoise != -1){
			score = 100 - ((double)ambientNoise / (double) (0.6));
		}
		return Math.round(score*100)/(double)100;
	}
	
	public double getDeviceUsageScore(String userID,  int windowInHours){
		double callDuration = getCallDuration(userID, windowInHours);
		double score = 50;
		if(callDuration > 70*60) {
			score = 0;
		} else if(callDuration >= 7*60 & callDuration <= 14*60){
			score = 75;
		} else if(callDuration >= 0 & callDuration <= 7*60){
			score = 100 - ((double)callDuration / (double) (0.07*60*4) );
		} else if(callDuration > 14*60) {
			score = 75 - ((((double)callDuration - (14*60)) / ((70-14)*60))*75);
		}
		return Math.round(score*100)/(double)100;
	}
	
	public double getToneScore(String userID,  int windowInHours){
		double toneAnalysis = getToneAnalysis(userID, windowInHours);
		double score = 50;
		if(toneAnalysis > 42) {
			score = 0;
		} else if(toneAnalysis != -1){
			score = 100 - ((double)toneAnalysis / (double) (0.42));
		}
		return Math.round(score*100)/(double)100;
	}
	
	public double getAlarmScore(String userID,  int windowInHours){
		double alarmCount = getAlarmMap(userID, windowInHours);
		double score = 50;
		if(alarmCount > 5) {
			score = 0;
		} else if(alarmCount != -1){
			score = 100 - ((double)alarmCount * 20);
		}
		return Math.round(score*100)/(double)100;
	}
}
