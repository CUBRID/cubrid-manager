package com.cubrid.cubridmanager.core.common.model;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
		String regex = "\\d*\\.\\d*\\.\\d*\\.[\\d-a-zA-Z]*";
		Matcher matcher = Pattern.compile(regex)
				.matcher(fullVersion);
		
		if (matcher.find()) {
			String[] versions = matcher.group().split("\\.");
			majorVersion = Integer.parseInt(versions[0]);
			minorVersion = Integer.parseInt(versions[1]);
		}
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
