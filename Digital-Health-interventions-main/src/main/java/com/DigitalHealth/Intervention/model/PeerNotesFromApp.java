package com.DigitalHealth.Intervention.model;

public class PeerNotesFromApp {
	private int peerGroupId;
	private String peerId;
	private String notes;
	
	public PeerNotesFromApp(int peerGroupId, String peerId, String notes) {
		super();
		this.peerGroupId = peerGroupId;
		this.peerId = peerId;
		this.notes = notes;
	}

	public int getPeerGroupId() {
		return peerGroupId;
	}

	public void setPeerGroupId(int peerGroupId) {
		this.peerGroupId = peerGroupId;
	}

	public String getPeerId() {
		return peerId;
	}

	public void setPeerId(String peerId) {
		this.peerId = peerId;
	}

	public String getNotes() {
		return notes;
	}

	public void setNotes(String notes) {
		this.notes = notes;
	}
	
	
}
