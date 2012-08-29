package com.untzuntz.ustackserver.peer;

public class PeerDelivery {

	private String targetName;
	
	public PeerDelivery(String tgt) {
		targetName = tgt;
	}
	
	public String getTarget() {
		return targetName;
	}
	
}
