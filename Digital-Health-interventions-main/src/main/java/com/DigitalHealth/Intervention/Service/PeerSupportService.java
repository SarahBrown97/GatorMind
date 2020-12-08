package com.DigitalHealth.Intervention.Service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Component;

import com.DigitalHealth.Intervention.model.PeerNotesFromApp;
import com.DigitalHealth.Intervention.model.PeerResponse;
import com.DigitalHealth.Intervention.model.QOLResponseFromApp;

@Component
public class PeerSupportService {

	@Autowired
	NamedParameterJdbcTemplate namedjdbcTemplate;
	
	public PeerResponse getPeerGroup(String userID) {
		String sql = "select peergroup_id from mhdp.peer_table where userId = :userId;";
		SqlParameterSource namedParams = new MapSqlParameterSource("userId",userID);		
		String sql1 = "select userId from mhdp.peer_table where peergroup_id in (select peergroup_id from mhdp.peer_table where userId = :userId);";
    	try {
    		String peerGroupId = namedjdbcTemplate.queryForObject(sql, namedParams, String.class);
			List<String> peerIds = namedjdbcTemplate.query(sql1, namedParams,  new RowMapper<String>(){
	            public String mapRow(ResultSet rs, int rowNum) 
	                    throws SQLException {
							return rs.getString(1);
						}
				});	
			
		    return new PeerResponse(peerGroupId, peerIds);
    	} catch (Exception e) {
			System.out.println(e.getStackTrace());
			return new PeerResponse("No Group Found", new ArrayList<String>());
		}
	}
	
	public void updatePeerNotesToTable(PeerNotesFromApp response) {
		java.util.Date date = new java.util.Date();
		Object timeStamp = new java.sql.Timestamp(date.getTime());
		String sql = "INSERT INTO peer_notes_table (timestamp, peerGroupId, peerId, Notes)\r\n" + 
				"VALUES (:timestamp, :peerGroupId, :peerId, :Notes);";
		SqlParameterSource namedParams = new MapSqlParameterSource("timestamp", timeStamp)
				.addValue("peerGroupId", response.getPeerGroupId())
				.addValue("peerId", response.getPeerId())
				.addValue("Notes", response.getNotes());
		namedjdbcTemplate.update(sql,namedParams);
	}
}
