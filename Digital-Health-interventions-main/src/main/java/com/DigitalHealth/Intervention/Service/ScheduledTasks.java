package com.DigitalHealth.Intervention.Service;

import java.io.StringWriter;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

import org.apache.commons.math3.ml.clustering.CentroidCluster;
import org.apache.commons.math3.ml.clustering.Cluster;
import org.apache.commons.math3.ml.clustering.Clusterable;
import org.apache.commons.math3.ml.clustering.Clusterer;
import org.apache.commons.math3.ml.clustering.DoublePoint;
import org.apache.commons.math3.ml.clustering.KMeansPlusPlusClusterer;
import org.apache.commons.math3.ml.clustering.evaluation.ClusterEvaluator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.DigitalHealth.Intervention.model.PeerResponse;


@Component
public class ScheduledTasks {

	@Autowired
	NamedParameterJdbcTemplate namedjdbcTemplate;
	@Autowired
	JdbcTemplate jdbcTemplate;
	@Autowired
	UtilityService us;
	
	int count = 0;
	
	@Scheduled(fixedRate = 1000*60*60*24)
	public void pollAllUsers() {
		List<String> userList;
		String sql = "SELECT DISTINCT user_id FROM mhdp.device_id;";
		try {
			userList =  namedjdbcTemplate.queryForList(sql,new HashMap<String,String>(),String.class);
			System.out.println(userList);
			getIntervention(userList);
			if(count % 30 == 0) {
				clustering(userList);
			}
			triaging(userList);
			count ++;
			//System.out.println(count);
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		
	}
	
	
	public void getIntervention(List<String> userList) {
		
		
		for(String user : userList) {
			
			String interventionValue = "";
			
			if(us.getCallScore(user,24*7) < 25 ) { // threshold value selected after research
				interventionValue = interventionValue + "High Calls,";
			}
			if(us.getAmbientNoiseScore(user,24*7) < 25) { 
				interventionValue = interventionValue + "Loud Noise,";
			}
			if(us.getPhysicalActivityScore(user,24*7) < 25) { 
				interventionValue = interventionValue + "Low Physical Activity,";
			}
			if(us.getAlarmScore(user, 24*7) < 25) { 
				interventionValue = interventionValue + "User frequently near Bars and Liquor Stores,";
			}
			if(us.getToneScore(user, 24*7) < 25) { 
				interventionValue = interventionValue + "User is Depressed and Sad,";
			}
			if(us.getDeviceUsageScore(user, 24*7) < 25) { 
				interventionValue = interventionValue + "High Device Usage,";
			}
			
			if(!interventionValue.equals("")) {
				interventionValue = interventionValue.substring(0, interventionValue.length() - 1);
				
				System.out.println(user+" HAS INTERVENTIONS");
				
				String sql = "INSERT INTO mhdp.worklist_Table (userId, message, status, timestamp)\r\n" + 
						"SELECT :userID, :message, 'Pending', now() \r\n" + 
						"WHERE NOT EXISTS (\r\n" + 
						"    SELECT userId FROM mhdp.worklist_Table WHERE message = :message and userId = :userID and timestamp  >= NOW() - INTERVAL 3 DAY  \r\n" + 
						") LIMIT 1;";

				SqlParameterSource namedParams = new MapSqlParameterSource("userID", user)
						.addValue("message", interventionValue);
		       
				try {
				namedjdbcTemplate.update(sql,namedParams);
				}
				catch(Exception e) {
					System.out.println("JDBC insertion error");
					e.printStackTrace();
					
				}
			}
		}
		
	}
	
public void triaging(List<String> userList) {
		
		int k = (int) Math.ceil(3); // 10 patients in 1 grpoup
	
		Clusterer<DoublePoint> clusterer = new KMeansPlusPlusClusterer<DoublePoint>(k);
		Map<DoublePoint, Queue<String>> map = new HashMap();
		List<DoublePoint> Data = new ArrayList<>();
		
		for(String user : userList){
			
			double dimensions[] = new double[6];
			
			dimensions[0] = (double)us.getCallScore(user, 7*24);
			dimensions[1] = (double)us.getAmbientNoiseScore(user, 7*24);
			dimensions[2] = (double)us.getPhysicalActivityScore(user, 7*24);
			dimensions[3] = (double)us.getDeviceUsageScore(user, 7*24);
			dimensions[4] = (double)us.getToneScore(user, 7*24);
			dimensions[5] = (double)us.getAlarmScore(user, 7*24);
			
			DoublePoint vector = new DoublePoint(dimensions);
			Data.add(vector);
			
			Queue<String> temp;
			if(map.containsKey(vector)) {
				temp = map.get(vector);
			}
			else {
				temp = new LinkedList<String>();
			}
			temp.add(user);
			map.put(vector,temp);
		}
		
		List<? extends Cluster<DoublePoint>> res = clusterer.cluster(Data);
		for (Cluster<DoublePoint> re : res) {
	        System.out.println(re.getPoints());
	    }
		try {
			String sql0 = "DELETE FROM mhdp.Triaging_Table;";
			jdbcTemplate.update(sql0);
			
			int idCounter = 1;
			
			double distance = Double.MAX_VALUE;
			
			Cluster<DoublePoint> lowest = null;
			
			for (Cluster<DoublePoint> re : res) {
				double dis = 0;
				DoublePoint c = (DoublePoint) ((CentroidCluster) re).getCenter();
				double[] n = c.getPoint();
				for(double d : n) {
					dis += d*d;
				}
				System.out.println(dis + ":::" + re.getPoints());
				if(dis < distance) {
					distance = dis;
					lowest = re;
				}
		    }
			
			List<DoublePoint> points = lowest.getPoints();
	         for(DoublePoint DP : points) {
	        	 Queue<String> q = map.get(DP);
	        	 String PatientName = (String) q.poll();
	        	 String sql = "INSERT INTO Triaging_Table (userId)\r\n" + 
	 					"VALUES ( :userID);";
	        	 SqlParameterSource namedParams = new MapSqlParameterSource("userID", PatientName);
	        	 namedjdbcTemplate.update(sql,namedParams);
	         }
		}	
		catch(Exception e) {
			System.out.println(e);
		}

	}
	
	public void clustering(List<String> userList) {
		
		int k = (int) Math.ceil(3); // 10 patients in 1 grpoup
	
		Clusterer<DoublePoint> clusterer = new KMeansPlusPlusClusterer<DoublePoint>(k);
		Map<DoublePoint, Queue<String>> map = new HashMap();
		List<DoublePoint> Data = new ArrayList<>();
		
		for(String user : userList){
			
			double dimensions[] = new double[6];
			
			dimensions[0] = (double)us.getCallScore(user, 7*24);
			dimensions[1] = (double)us.getAmbientNoiseScore(user, 7*24);
			dimensions[2] = (double)us.getPhysicalActivityScore(user, 7*24);
			dimensions[3] = (double)us.getDeviceUsageScore(user, 7*24);
			dimensions[4] = (double)us.getToneScore(user, 7*24);
			dimensions[5] = (double)us.getAlarmScore(user, 7*24);
			
			DoublePoint vector = new DoublePoint(dimensions);
			Data.add(vector);
			
			Queue<String> temp;
			if(map.containsKey(vector)) {
				temp = map.get(vector);
			}
			else {
				temp = new LinkedList<String>();
			}
			temp.add(user);
			map.put(vector,temp);
		}
		
		List<? extends Cluster<DoublePoint>> res = clusterer.cluster(Data);
		for (Cluster<DoublePoint> re : res) {
	        System.out.println(re.getPoints());
	    }
		try {
			String sql0 = "DELETE FROM mhdp.peer_table;";
			jdbcTemplate.update(sql0);
			
			int idCounter = 1;
			int c = 0;
			
			for (Cluster<DoublePoint> re : res) {
		         List<DoublePoint> points = re.getPoints();
		         for(DoublePoint DP : points) {
		        	 if(c == 10) {
		        		 c = 0;
		        		 idCounter++;
		        	 }
		        	 Queue<String> q = map.get(DP);
		        	 String PatientName = (String) q.poll();
		        	 String sql = "INSERT INTO peer_table (peergroup_id, userId)\r\n" + 
		 					"VALUES (:peergroupID, :userID);";
		        	 SqlParameterSource namedParams = new MapSqlParameterSource("peergroupID", idCounter)
		 					.addValue("userID", PatientName);
		        	 namedjdbcTemplate.update(sql,namedParams);
		        	 c++;
		         }
		        idCounter++;
		    }
		}	
		catch(Exception e) {
			System.out.println(e);
		}

	}
}
