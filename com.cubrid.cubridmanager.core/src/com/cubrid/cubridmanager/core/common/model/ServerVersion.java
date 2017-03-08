package com.cubrid.cubridmanager.core.common.model;

import java.util.StringTokenizer;

public class ServerVersion {
	private int majorVersion, minorVersion;
	private String serverVersion;
	
	public ServerVersion() {
		this(-1, -1);
	}
	

	public ServerVersion(int majorVersion, int minorVersion) {
		this.majorVersion = majorVersion;
		this.minorVersion = minorVersion;
	}
	
	public void setVersion(String fullVersion) {
		serverVersion = fullVersion;
		StringTokenizer st = new StringTokenizer(fullVersion);
		st.nextToken();
		String versionNo = st.nextToken();
		
		majorVersion = Integer.parseInt(versionNo.substring(0, versionNo.indexOf('.')));
		minorVersion = Integer.parseInt(versionNo.substring(versionNo.indexOf('.')+1));
	}
	
	public boolean isSmallerThan(int majorVersion, int minorVersion) {
		return (majorVersion == this.majorVersion) ? (this.minorVersion < minorVersion) :
													 (this.majorVersion < majorVersion);
						
	}
	
	public boolean isSmallerThan(ServerVersion serverVersion) {
		return isSmallerThan (serverVersion.getMajorVersion(), serverVersion.getMinorVersion());
	}
	
	public String getServerVersion() {
		return serverVersion;
	}
	
	public int getMajorVersion() {
		return majorVersion;
	}
	
	public int getMinorVersion() {
		return minorVersion;
	}
}
