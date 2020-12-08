package com.DigitalHealth.Intervention.model;

import java.util.List;

public class PeerResponse {
	private String peerGroupId;
	private List<String> peerIds;
	
	public PeerResponse(String peerGroupId, List<String> peerIds) {
		super();
		this.peerGroupId = peerGroupId;
		this.peerIds = peerIds;
	}
	public String getPeerGroupId() {
		return peerGroupId;
	}
	public void setPeerGroupId(String peerGroupId) {
		this.peerGroupId = peerGroupId;
	}
	public List<String> getPeerIds() {
		return peerIds;
	}
	public void setPeerIds(List<String> peerIds) {
		this.peerIds = peerIds;
	}
	
	
}
