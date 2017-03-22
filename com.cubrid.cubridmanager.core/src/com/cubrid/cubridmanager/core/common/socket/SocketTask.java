/*
 * Copyright (C) 2009 Search Solution Corporation. All rights reserved by Search
 * Solution.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met: -
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer. - Redistributions in binary
 * form must reproduce the above copyright notice, this list of conditions and
 * the following disclaimer in the documentation and/or other materials provided
 * with the distribution. - Neither the name of the <ORGANIZATION> nor the names
 * of its contributors may be used to endorse or promote products derived from
 * this software without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 * 
 */
package com.cubrid.cubridmanager.core.common.socket;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.slf4j.Logger;

import com.cubrid.common.core.task.AbstractTask;
import com.cubrid.common.core.util.LogUtil;
import com.cubrid.common.core.util.StringUtil;
import com.cubrid.common.core.util.ThreadUtil;
import com.cubrid.cubridmanager.core.Messages;
import com.cubrid.cubridmanager.core.common.ServerManager;
import com.cubrid.cubridmanager.core.common.model.ServerInfo;
import com.cubrid.cubridmanager.core.common.task.GetTaskStatusTask;

/**
 * 
 * This class is abstract,it provide base methods to communicate with CUBRID
 * Manager server,all concrete task must extend it and finish a lot of concrete
 * operations. Every instance of this class is used in a single thread at
 * best.when multiple thread use the same instance,may cause sent message
 * confusion.
 * Instead of using the socket only, may using HTTP protocol. So the class's name
 * should be changed.
 * 
 * @author moulinwang
 * @version 1.0 - 2009-6-4 created by moulinwang
 */
public abstract class SocketTask extends AbstractTask {
	protected static final Logger LOGGER = LogUtil.getLogger(SocketTask.class);
	public static final int SOCKET_IO_TIMEOUT_MSEC = 10000;
	public static final String CIPHER_CHARACTER = "@";
	protected MessageMap sendedMsgMap;
	protected ServerInfo serverInfo;
	protected boolean isCancel = false;
	protected boolean isUsingMonPort = false;
	protected IManagerClient clientService = null;
	// whether the responsed message used speical delimiter
	protected boolean isUsingSpecialDelimiter = false;
	// whether this task need to send message multi using the same socket
	protected boolean isNeedMultiSend = false;
	// Before send message,whether need server connected status
	protected boolean isNeedServerConnected = true;
	protected String appendSendMsg = null;
	//for unit test,add it
	private TreeNode responseNode = null;
	private GetTaskStatusTask getTaskStatusTask;

	/**
	 * 
	 * The constructor
	 * 
	 * @param taskName
	 * @param serverInfo
	 */
	protected SocketTask(String taskName, ServerInfo serverInfo) {
		this(taskName, serverInfo, null);
	}

	/**
	 * The constructor
	 * 
	 * @param taskName
	 * @param serverInfo
	 * @param sendedOrderMsgItems
	 */
	protected SocketTask(String taskName, ServerInfo serverInfo, String[] sendedOrderMsgItems) {
		this(taskName, serverInfo, sendedOrderMsgItems, "UTF-8", "UTF-8");
	}

	/**
	 * The constructor
	 * 
	 * @param taskName
	 * @param serverInfo
	 * @param sendedOrderMsgItems
	 * @param requestCharset
	 * @param responseCharset
	 */
	protected SocketTask(String taskName, ServerInfo serverInfo, String[] sendedOrderMsgItems,
			String requestCharset, String responseCharset) {
		this.taskName = taskName;
		this.serverInfo = serverInfo;
		this.sendedMsgMap = new MessageMap(sendedOrderMsgItems);
		if (serverInfo != null) {
			// no cache, prototype
			if (this.serverInfo.getInterfaceVersion() == ServerInfo.InterfaceVersion.V2) {
				clientService = new ClientHttp(serverInfo.getHostAddress(), serverInfo.getHostMonPort(),
						serverInfo.getUserName(), requestCharset, responseCharset);
			} else {
				clientService = new ClientSocket(serverInfo.getHostAddress(), serverInfo.getHostJSPort(),
						serverInfo.getUserName(), requestCharset, responseCharset);
			}
			serverInfo.addObserver(this);
		}
	}

	/**
	 * 
	 * Set time out
	 * 
	 * @param timeout int
	 */
	public void setTimeout(int timeout) {
		if (clientService != null) {
			clientService.setTimeout(timeout);
		}
	}

	/**
	 * Set sending message information, assign only single value to the key,task
	 * or token is not needed setting
	 * 
	 * @param key String The key string
	 * @param value String The value string
	 */
	protected void setMsgItem(String key, String value) {
		sendedMsgMap.addOrModifyValue(key, value);
	}

	/**
	 * Set sending message information, assign more than one values to the
	 * key,task or token is not needed setting
	 * 
	 * @param key String The key string
	 * @param values String[] The key string
	 */
	protected void setMsgItem(String key, String[] values) {
		sendedMsgMap.addOrModifyValues(key, values);
	}

	/**
	 * Send message to CBURID Manager server
	 */
	public void execute() {
		isCancel = false;
		errorMsg = null;
		warningMsg = null;
		if (clientService == null || serverInfo == null) {
			errorMsg = Messages.error_noInitSocket;
			return;
		}
		if (isNeedServerConnected
				&& !ServerManager.isConnected(
						serverInfo.getHostAddress(),
						serverInfo.getHostMonPort(), serverInfo.getUserName())) {
			errorMsg = Messages.error_disconnected;
			return;
		}
		String message = getMessage();

		clientService.setUsingSpecialDelimiter(isUsingSpecialDelimiter);
		clientService.sendRequest(message);
		if (!isNeedMultiSend) {
			clientService.tearDownConnection();
		}
		errorMsg = clientService.getErrorMsg();
		warningMsg = clientService.getWarningMsg();
		
		afterExecute();
	}

	/**
	 * If the response contains a key: taskKey, it shows that the operation is still in
	 * progress, we wait 300s. If it is still no result, the operation failed.
	 */
	private void afterExecute() {
		String taskKey = this.getTaskKey();
		if (null == taskKey || "".equals(taskKey.trim())) {
			return;
		}
		getTaskStatusTask = new GetTaskStatusTask(this.getServerInfo(), taskKey);
		int total = 60;
		int count = 0;
		getTaskStatusTask.setNeedMultiSend(true);
		do {
			getTaskStatusTask.execute();
			ThreadUtil.sleep(5000);
		} while (count++ < total && !getTaskStatusTask.isSuccess());
		String msgtmp = getTaskStatusTask.getErrorMsg();
		this.setErrorMsg(msgtmp);
	}

	private String getMessage() {
		String message = "";
		if (null == appendSendMsg || "".equals(appendSendMsg)) {
			message = getRequest();
		} else {
			if (taskName != null && taskName.length() > 0) {
				message += "task:" + taskName + "\n";
			}
			if (serverInfo.getHostToken() != null && serverInfo.getHostToken().length() > 0) {
				message += "token:" + serverInfo.getHostToken() + "\n";
			}
			message += appendSendMsg.trim() + "\n\n";
		}

		if (this.serverInfo.getInterfaceVersion() == ServerInfo.InterfaceVersion.V2) {
			// Change the message's format to JSON
			message = MessageUtil.parseRequestToJson(message);
		}
		return message;
	}

	/**
	 * 
	 * Return the request message
	 * 
	 * @return String The request string
	 */
	public String getRequest() {
		if (taskName != null && taskName.length() > 0) {
			sendedMsgMap.addOrModifyValue("task", taskName);
		}
		if (serverInfo.getHostToken() != null
				&& serverInfo.getHostToken().length() > 0) {
			sendedMsgMap.addOrModifyValue("token", serverInfo.getHostToken());
		}
		return sendedMsgMap.toString();
	}

	/**
	 * Invoke the given method and parameter in order to set value
	 * 
	 * @param method Method The given method
	 * @param parameters The given method
	 * @param targetObject Object The given target object
	 * @param value Object The given value
	 */
	private static void invokeMethod4SetValue(Method method,
			Class<?> parameters, Object targetObject, Object value) {
		try {

			if (parameters == String.class) {
				method.invoke(targetObject, value);
			} else if (parameters == int.class) {
				method.invoke(targetObject, StringUtil.str2Int((String) value));
			} else if (parameters == boolean.class) {
				method.invoke(targetObject,
						StringUtil.strYN2Boolean((String) value));
			} else if (parameters == double.class) {
				method.invoke(targetObject,
						StringUtil.str2Double((String) value));
			} else if (parameters == byte.class) {
				method.invoke(targetObject, StringUtil.str2Int((String) value));
			} else if (parameters == String[].class) {
				method.invoke(targetObject, value);
			}

			// Just record below log for debug
		} catch (IllegalArgumentException e) { // error argument
			// type
			LOGGER.error(e.getMessage(), e);
		} catch (IllegalAccessException e) { // no access right
			LOGGER.error(e.getMessage(), e);
		} catch (InvocationTargetException e) { // error invoke
			// method
			// exception
			LOGGER.error(e.getTargetException().getMessage(), e);
		}
	}

	/**
	 * Set a target object's fields' value by a Tree node object
	 * 
	 * @param node TreeNode The tree node
	 * @param targetObject Object The target object,generally it is a plain Java
	 *        bean
	 */
	public static void setFieldValue(TreeNode node, final Object targetObject) {
		if (node == null || targetObject == null) {
			return;
		}
		Method[] methods = targetObject.getClass().getMethods();
		for (Method m : methods) {
			String methodname = m.getName();
			Class<?>[] parameters = m.getParameterTypes();
			String field = methodname.substring(3).toString();
			if (methodname.startsWith("set") && parameters != null
					&& parameters.length == 1) {
				String value = node.getValue(field.toLowerCase(Locale.getDefault()));
				String[] values = node.getValues(field.toLowerCase(Locale.getDefault()));
				if (value != null && values.length == 1) {
					invokeMethod4SetValue(m, parameters[0], targetObject, value);
				}
				if (values != null && values.length > 1) {
					invokeMethod4SetValue(m, parameters[0], targetObject,
							values);
				}
			} else if (methodname.startsWith("add") && parameters != null
					&& parameters.length == 1) {
				String[] values = node.getValues(field.toLowerCase(Locale.getDefault()));
				if (values == null) {
					List<TreeNode> children = node.getChildren();
					if (null != children) {
						for (TreeNode n : children) {
							String nodeName = n.getValue("open");
							if (nodeName == null
									|| nodeName.trim().length() <= 0) {
								nodeName = n.getValue("start");
							}
							if (field.equalsIgnoreCase(nodeName)) {
								try {
									Class<?> clazz = parameters[0];
									Object o;
									if (clazz == Map.class) {
										o = n.getValueByMap();
									} else {
										o = clazz.newInstance();
										setFieldValue(n, o);
									}
									m.invoke(targetObject, o);
								} catch (InstantiationException e) {
									LOGGER.error(e.getMessage(), e);
								} catch (IllegalAccessException e) {
									LOGGER.error(e.getMessage(), e);
								} catch (IllegalArgumentException e) {
									LOGGER.error(e.getMessage(), e);
								} catch (InvocationTargetException e) {
									LOGGER.error(e.getMessage(), e);
								}

							}
						}
					}
				} else {
					for (String value : values) {
						invokeMethod4SetValue(m, parameters[0], targetObject,
								value);
					}
				}
			}

		}
	}

	/**
	 * Add a array of String to a list of String
	 * 
	 * @param list List<String>
	 * @param values String[]
	 */
	public static void fillSet(List<String> list, String[] values) {
		if (values != null && values.length > 0) {
			for (String value : values) {
				list.add(value);
			}
		}
	}

	/**
	 * 
	 * Stop running this task
	 * 
	 */
	public void cancel() {
		isCancel = true;
		serverInfo.removeObserver(this);
		if (clientService != null) {
			clientService.stopRead();
			clientService.tearDownConnection();
		}
		if (getTaskStatusTask != null) {
			getTaskStatusTask.cancel();
		}
	}

	/**
	 * 
	 * Set sent message order
	 * 
	 * @param orders String[] The orders to be set
	 */
	protected void setOrders(String[] orders) {
		sendedMsgMap.setOrders(orders);
	}

	/**
	 * 
	 * Get the result after this task execute
	 * 
	 * @return TreeNode The tree node
	 */
	protected TreeNode getResponse() {
		if (responseNode != null) {
			return responseNode;
		}
		TreeNode treeNode;
		if (getTaskStatusTask != null) {
			treeNode = getTaskStatusTask.getResponse();
		} else {
			treeNode = clientService == null ? null : clientService.getResponse();
		}
		return treeNode;
	}

	/**
	 * 
	 * Set response node. note:this method is added for unit test
	 * 
	 * @param responseNode the TreeNode
	 */
	public void setResponse(TreeNode responseNode) {
		this.responseNode = responseNode;
	}

	/**
	 * 
	 * Get whether this task use special delimiter
	 * 
	 * @return boolean Whether usting special delimiter
	 */
	public boolean isUsingSpecialDelimiter() {
		return isUsingSpecialDelimiter;
	}

	/**
	 * 
	 * Set whether this task use special delimiter
	 * 
	 * @param usingSpecialDelimiter boolean Whether using specialDelimiter
	 */
	public void setUsingSpecialDelimiter(boolean usingSpecialDelimiter) {
		this.isUsingSpecialDelimiter = usingSpecialDelimiter;
	}

	/**
	 * 
	 * Get CUBRID Manager server information
	 * 
	 * @return ServerInfo The server info
	 */
	public ServerInfo getServerInfo() {
		return serverInfo;
	}

	/**
	 * 
	 * Set CUBRID Manager server information
	 * 
	 * @param serverInfo ServerInfo The server info
	 */
	public void setServerInfo(ServerInfo serverInfo) {
		this.serverInfo = serverInfo;
		if (serverInfo != null) {
			if (clientService != null) {
				clientService.tearDownConnection();
			}
			if (this.serverInfo.getInterfaceVersion() == ServerInfo.InterfaceVersion.V2) {
				clientService = new ClientHttp(serverInfo.getHostAddress(), serverInfo.getHostMonPort(),
						serverInfo.getUserName());
			} else {
				clientService = new ClientSocket(serverInfo.getHostAddress(), serverInfo.getHostJSPort(),
						serverInfo.getUserName());
			}
		}
	}

	/**
	 * 
	 * Get whether this task is for monitoring connection continuously
	 * 
	 * @return boolean Whether using monitor connection permanently
	 */
	public boolean isUsingMonPort() {
		return isUsingMonPort;
	}

	/**
	 * 
	 * Set whether this task use monitoring port
	 * 
	 * @param isUsingMonPort boolean Whether using monitor port
	 */
	public void setUsingMonPort(boolean isUsingMonPort) {
		this.isUsingMonPort = isUsingMonPort;
		if (clientService != null && ServerInfo.InterfaceVersion.V1 == this.serverInfo.getInterfaceVersion()) {
			clientService.setPort(isUsingMonPort ? serverInfo.getHostMonPort() : serverInfo.getHostJSPort());
		}
	}

	/**
	 * 
	 * Get whether this task use monitoring port
	 * 
	 * @return ClientSocket
	 */
	protected IManagerClient getClientService() {
		return clientService;
	}

	/**
	 * 
	 * Set the executed socket of this task
	 * 
	 * @param clientService ClientSocket The client socket
	 */
	protected void setClientService(IManagerClient clientService) {
		this.clientService = clientService;
	}

	/**
	 * 
	 * Clear sent message
	 * 
	 */
	public void clearMsgItems() {
		if (sendedMsgMap != null) {
			sendedMsgMap.clear();
		}
	}

	/**
	 * Get response message of this task
	 * 
	 * @return String
	 */
	protected String getResponsedMsg() {
		String msg;
		if (getTaskStatusTask != null) {
			msg = getTaskStatusTask.getResponsedMsg();
		} else {
			msg = clientService == null ? Messages.error_noInitSocket : clientService.getResponsedMsg();
		}
		return msg;
	}

	/**
	 * Return whether to need to send message multiple times using the same
	 * socket
	 * 
	 * @return boolean Whether need multiple send
	 */
	public boolean isNeedMultiSend() {
		return isNeedMultiSend;
	}

	/**
	 * Set whether to need to send message multiple times using the same socket
	 * 
	 * @param isNeedMultiSend whether need multiple send
	 */
	public void setNeedMultiSend(boolean isNeedMultiSend) {
		this.isNeedMultiSend = isNeedMultiSend;
	}

	/**
	 * When need to send message multiple times using the same socket,at last
	 * need to call this method to close this socket
	 */
	public void finish() {
		serverInfo.removeObserver(this);
		if (clientService != null) {
			clientService.tearDownConnection();
		}
		if (getTaskStatusTask != null) {
			getTaskStatusTask.finish();
		}
	}

	/**
	 * When this task send message,return whether to need that server is
	 * connected.
	 * 
	 * @return boolean Whether need server connected
	 */
	public boolean isNeedServerConnected() {
		return isNeedServerConnected;
	}

	/**
	 * When this task send message,set whether to need that server is connected.
	 * 
	 * @param isNeedServerConnected boolean Whether need server connected
	 */
	public void setNeedServerConnected(boolean isNeedServerConnected) {
		this.isNeedServerConnected = isNeedServerConnected;
	}

	public String getAppendSendMsg() {
		return appendSendMsg;
	}

	public void setAppendSendMsg(String appendSendMsg) {
		this.appendSendMsg = appendSendMsg;
	}

	public boolean isCancel() {
		return isCancel;
	}

	/**
	 * Whether is success
	 * 
	 * @return boolean Whether is success
	 */
	public boolean isSuccess() {
		TreeNode node = this.getResponse();
		if (node == null) {
			return false;
		}
		return StringUtil.isEqual(node.getValue("status"), "success");
	}

	/**
	 * 
	 * Get error message after this task execute.if it is null,this task is
	 * ok,or it has error
	 * 
	 * @return String
	 */
	public String getErrorMsg() {
		if (getTaskStatusTask != null) {
			errorMsg = getTaskStatusTask.getErrorMsg();
		}
		if (errorMsg == null && clientService != null) {
			errorMsg = clientService.getErrorMsg();
		}
		return errorMsg;
	}

	/**
	 * 
	 * Get warning message after this task execute
	 * 
	 * @return String
	 */
	public String getWarningMsg() {
		if (getTaskStatusTask != null) {
			warningMsg = getTaskStatusTask.getWarningMsg();
		}
		if (warningMsg == null && clientService != null) {
			warningMsg = clientService.getWarningMsg();
		}
		return warningMsg;
	}
	
	/**
	 * get the response status code
	 * 
	 * @return response status code
	 */
	public int getStatusCode() {
		int status;
		if (getTaskStatusTask != null) {
			status = getTaskStatusTask.getStatusCode();
		} else {
			status = clientService.getStatusCode();
		}
		return status;
	}

	// TODO memo
	public String getTaskKey() {
		TreeNode node = this.getResponse();
		if (node == null) {
			return null;
		}
		return node.getValue("uuid");
	}

	// TODO memo
	public void setTaskAsync() {
		this.setMsgItem("taskType", "async");
	}
}
