package com.cubrid.cubridmanager.core.mock;

import java.io.File;

import com.cubrid.cubridmanager.core.common.ServerManager;
import com.cubrid.cubridmanager.core.common.model.ServerInfo;
import com.cubrid.cubridmanager.core.common.socket.ClientSocket;
import com.cubrid.cubridmanager.core.common.task.MonitoringTask;

public class TestRawScript {

	public static final String CLIENT_VERSION = "8.2.0";
	public static final String SERVER_IP = "localhost";
	public static final int SERVER_AUTH_PORT = 8001;
	public static final int SERVER_TASK_PORT = 8002;
	public static final String MAN_USERID = "admin";
	public static final String MAN_PASSWD = "1111";

	private static ServerInfo serverInfo = null;

	public static void main(String[] args) {

		connect();

		execute("database/backupdb/001.req.txt");

	}

	public static void connect() {

		serverInfo = new ServerInfo();
		serverInfo.setHostAddress(SERVER_IP);
		serverInfo.setHostMonPort(SERVER_AUTH_PORT);
		serverInfo.setHostJSPort(SERVER_TASK_PORT);
		serverInfo.setUserName(MAN_USERID);
		serverInfo.setUserPassword(MAN_PASSWD);

		MonitoringTask monTask = new MonitoringTask(serverInfo);
		serverInfo = monTask.connectServer(CLIENT_VERSION, 1000);

		if (monTask.getErrorMsg() != null) {
			System.exit(-1);
		}

		ServerManager.getInstance().addServer(
				serverInfo.getHostAddress(), serverInfo.getHostMonPort(),
				serverInfo.getUserName(), serverInfo);
		ServerManager.getInstance().setConnected(serverInfo.getHostAddress(),
				serverInfo.getHostMonPort(), serverInfo.getUserName(), true);

	}

	public static void execute(String aFilename) {

		String basepath = TestRawScript.class.getResource(".").getPath()
				+ "scripts/";
		String filename = basepath + aFilename;

		String message = MockTaskServer.getFileText(new File(filename));

		ClientSocket clientSocket = new ClientSocket(
				serverInfo.getHostAddress(), serverInfo.getHostJSPort(),
				serverInfo.getUserName());

		clientSocket.setUsingSpecialDelimiter(false);
		clientSocket.sendRequest(message);
		clientSocket.tearDownConnection();

		String errorMsg = clientSocket.getErrorMsg();
		if (errorMsg != null)
			System.out.println("ERROR MSG:" + errorMsg);

		String warningMsg = clientSocket.getWarningMsg();
		if (warningMsg != null)
			System.out.println("WARN MSG:" + warningMsg);

	}

}
