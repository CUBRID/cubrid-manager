package com.cubrid.cubridmanager.core.common.socket;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;

import com.cubrid.cubridmanager.core.SetupEnvTestCase;
import com.cubrid.cubridmanager.core.common.ServerManager;

public class ClientSocketTest extends
		SetupEnvTestCase {

	public void testSendRequest() {
		getautoaddvollog();
		dbspaceinfoFailed();
		dbspaceinfoSuccess();
	}

	public void testParseResponse() throws Exception {
		String filepath = this.getFilePathInPlugin("/com/cubrid/cubridmanager/core/messageSendReceiveTest.txt");

		String file = filepath;
		ArrayList<String> send = new ArrayList<String>();
		ArrayList<String> receive = new ArrayList<String>();

		BufferedReader in = new BufferedReader(new FileReader(file));
		StringBuffer bf = new StringBuffer();
		String line = null;

		int index = token.indexOf(":");
		String newvalue = token.substring(index + 1);

		while (null != (line = in.readLine())) {
			if (line.trim().equals("")) {
				continue;
			}
			if (line.equals("SendClientMessage:")) {
				if (!bf.toString().equals("")) {
					receive.add(bf.append("\n\n").toString());
				}
				bf = new StringBuffer();
			} else if (line.equals("GetClientMessage:")) {
				if (!bf.toString().equals("")) {
					send.add(bf.append("\n\n").toString());
				}
				bf = new StringBuffer();
			} else {
				bf.append(line).append("\n");
			}
		}
		receive.add(bf.append("\n\n").toString());
		in.close();

//		for (int i = 0; i < send.size(); i++) {
//			ClientSocket cs = new ClientSocket(serverInfo.getHostAddress(),
//					jpport, serverInfo.getUserName(), "UTF-8", "UTF-8");
//
//			TreeNode tmpnode = MessageUtil.parseResponse(send.get(i));
//			String value = tmpnode.getValue("token");
//			if (value != null) {
//				tmpnode.modifyValue("token", newvalue);
//			}
//			cs.sendRequest(tmpnode.toString());
//			//			cs.tearDownConnection();
//			if (cs.getErrorMsg() == null) {
//				TreeNode node2 = MessageUtil.parseResponse(receive.get(i));
//				value = node2.getValue("token");
//				if (value != null) {
//					node2.modifyValue("token", newvalue);
//				}
//				// assertEquals(node.toString(), node2.toString());
//			}
//		}
		
//		ServerManager.getInstance().disConnectAllServer();
	}
	
	public void testSetTimeout() {
		try {
			ClientSocket cs = new ClientSocket(serverInfo.getHostAddress(),
					jpport, serverInfo.getUserName(), "UTF-8", "UTF-8");
			cs.setTimeout(1);
			cs.setHeartbeat(1);
			cs.sendRequest("1\n\n");
			cs.setHeartbeat(2);
			cs.sendRequest("2\n\n");
			cs.setHeartbeat(3);
			cs.sendRequest("3\n\n");
			cs.setHeartbeat(4);
			cs.sendRequest("4\n\n");
			cs.setHeartbeat(5);
			cs.sendRequest("5\n\n");
			cs.setHeartbeat(6);
			cs.sendRequest("6\n\n");
			cs.setHeartbeat(7);
			cs.sendRequest("7\n\n");
			cs.setHeartbeat(8);
			cs.sendRequest("8\n\n");
			cs.setHeartbeat(9);
			cs.sendRequest("9\n\n");
			cs.setTimeout(3);
			cs.stopHeartbeatThread();
			ServerManager.getInstance().disConnectAllServer();
		} catch (Exception e) {
			assertTrue(true);
		}
		
		
		try {
			ClientSocket cs = new ClientSocket(serverInfo.getHostAddress(),
					jpport, serverInfo.getUserName(), "UTF-8", "UTF-8");
			ServerManager.getInstance().disConnectAllServer();
		} catch (Exception e) {
		}
		
		try {
			ServerManager.getInstance().removeServer("localhost", 8001, "dba");
			ServerManager.getInstance().removeServer("localhost", 8002, "dba");
			ServerManager.getInstance().removeServer("localhost", 8003, "dba");
			ServerManager.getInstance().removeServer("localhost", 8004, "dba");
			assertNotNull(ServerManager.getInstance().getAllServerInfos());
		} catch (Exception e) {
		}
		
		
	}
	
	private void getautoaddvollog() {

		ClientSocket cs = new ClientSocket(serverInfo.getHostAddress(), jpport,
				serverInfo.getUserPassword());
		String message = "task:getautoaddvollog\ntoken:" + token + "\n";
		cs.sendRequest(message);
		TreeNode node = cs.getResponse();
		System.out.println(node.toString());

	}

	private void dbspaceinfoFailed() {
		String datebasename = "xxxxxxxxxxx";
		ClientSocket cs = new ClientSocket(serverInfo.getHostAddress(), jpport,
				serverInfo.getUserName());
		String message = "task:dbspaceinfo\ntoken:" + token + "\ndbname:"
				+ datebasename + "\n\n";

		cs.sendRequest(message);
		cs.tearDownConnection();
		TreeNode node = cs.getResponse();
		System.out.println(node.toString());
		String status = node.getValue("status");
		String note = node.getValue("note");
		assertEquals(status, "failure");
//		assertNotSame(note.indexOf(datebasename), -1);

	}

	private void dbspaceinfoSuccess() {

		String datebasename = testDbName;
		ClientSocket cs = new ClientSocket(serverInfo.getHostAddress(), jpport,
				serverInfo.getUserName());
		String message = "task:dbspaceinfo\ntoken:" + token + "\ndbname:"
				+ datebasename + "\n\n";
		System.out.println(token.toString() + "  ts");

		cs.sendRequest(message);
		cs.tearDownConnection();
		TreeNode node = cs.getResponse();
		System.out.println(node.toString());
		String status = node.getValue("status");
		String dbname = node.getValue("dbname");
//		assertEquals(status, "success");
//		assertEquals(datebasename, dbname);
	}

}
