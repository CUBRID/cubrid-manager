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

import java.io.BufferedInputStream;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;

import org.slf4j.Logger;

import com.cubrid.common.core.util.LogUtil;
import com.cubrid.cubridmanager.core.Messages;
import com.cubrid.cubridmanager.core.common.ServerManager;

/**
 * 
 * To provide socket service for upper layer, send message, receive message and
 * parse message for upper layer to use. <li>Create a socket connection to send
 * request and parse response <li>Handle with the exception <li>If multiple
 * threads access this concurrently,it must be synchronized externally
 * 
 * @author moulinwang
 * @version 1.0 - 2009-6-4 created by moulinwang
 */

public class ClientSocket extends AbstractManagerClient {

	private static final Logger LOGGER = LogUtil.getLogger(ClientSocket.class);

	/**
	 * to stop reading from socket
	 */
	private boolean isStopRead = false;

	/**
	 * parse a message with special delimiter
	 */
	private boolean isUsingSpecialDelimiter = false;

	private BufferedInputStream socketInputStream;
	private BufferedWriter socketWriter;

	private Socket socket;
	/**
	 * when sending formal messages, the status is busy, in this case, heart
	 * beat is not needed at the same time.
	 */
	private boolean isBusy = true;
	/**
	 * a thread to send out the heart beat
	 */
	private Thread heartbeatThread = null;
	private int connectionTimeout = 5000;
	private int readTimeout = 0;
	private long startTime;

	/**
	 * The constructor
	 * 
	 * @param hostAddress
	 * @param port
	 * @param userName
	 */
	public ClientSocket(String hostAddress, int port, String userName) {
		this.hostAddress = hostAddress;
		this.port = port;
		this.userName = userName;
	}

	/**
	 * The constructor
	 * 
	 * @param hostAddress
	 * @param port
	 * @param userName
	 * @param requestCharset
	 * @param responseCharset
	 */
	public ClientSocket(String hostAddress, int port, String userName, String requestCharset, String responseCharset) {
		this.hostAddress = hostAddress;
		this.port = port;
		this.userName = userName;
		this.requestCharsetName = requestCharset;
		this.responseCharsetName = responseCharset;
	}

	/**
	 * Set up a socket
	 * 
	 * @throws UnknownHostException
	 *             a possible exception
	 * @throws IOException
	 *             a possible exception
	 */
	private void setUpConnection() throws UnknownHostException, IOException {
		socket = new Socket();
		socket.connect(new InetSocketAddress(hostAddress, port), connectionTimeout);
		socket.setTcpNoDelay(false);
		socket.setKeepAlive(true);
		socket.setSoTimeout(readTimeout);
		socketInputStream = new BufferedInputStream(socket.getInputStream());
		if (requestCharsetName == null) {
			socketWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
		} else {
			socketWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), requestCharsetName));
		}

	}
	

	/**
	 * 
	 * Send request to CUBRID Manager server
	 * 
	 * @param message
	 *            String
	 */
	public void sendRequest(String message) {
		try {
			startTime = System.currentTimeMillis();
			// before sending a message, set busy=true
			isBusy = true;
			isStopRead = false;

			/*
			 * set up socket
			 */
			if (socket == null) {
				setUpConnection();
			}

			/*
			 * send a message
			 */
			LOGGER.debug("\n<sentMsg>\n{}\n</sentMsg>\n", message);

			if (socketWriter != null) {
				socketWriter.write(message);
				socketWriter.flush();
			}
			/*
			 * read the response and parse the message
			 */
			readResponse();
			// end parsing the response, set busy=false, so heart beat is OK
			// again
			isBusy = false;

		} catch (UnknownHostException e) {
			LOGGER.error(e.getMessage(), e);
			errorMsg = e.getMessage() == null || e.getMessage().trim().length() == 0 ? Messages.error_unknownHost : e
					.getMessage();
			// If user use unavailable address as '127.0.0.1000', it return
			// error with ip address.
			// For this case, if result message and address is same, it will be
			// replaced "No route to host connect" message.
			if (errorMsg != null && getHostAddress() != null
					&& errorMsg.trim().equalsIgnoreCase(getHostAddress().trim())) {
				errorMsg = "No route to host connect";
			}
			super.canConnect = false;
		} catch (IOException e) {
			LOGGER.error(e.getMessage(), e);
			if (e.getMessage() != null && (e.getMessage().startsWith("socket closed") || e.getMessage().startsWith("Read timed out")) ) {
				errorMsg = Messages.errCannotConnectToCmServer;
			} else {
				errorMsg = e.getMessage() == null || e.getMessage().trim().length() == 0 ? Messages.errCannotConnectToCmServer : e
					.getMessage();
			}
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
			errorMsg = e.getMessage();
		}

		TreeNode node = this.getResponse();
		if (node == null) {
			super.canConnect = false;
			ServerManager.getInstance().setConnected(hostAddress, port, userName, false);
		}

		if (errorMsg != null && errorMsg.trim().length() > 0) {
			LOGGER.debug("\n<errorMsg>\n{}\n</errorMsg>\n", errorMsg);
		}
	}

	/**
	 * Read the response, check and parse it
	 * 
	 * @throws IOException
	 *             The exception
	 * @throws UnsupportedEncodingException
	 *             The exception
	 */
	private void readResponse() throws UnsupportedEncodingException, IOException {
		// initial return result
		response = null;
		responsedMsg = null;
		errorMsg = null;
		warningMsg = null;
		StringBuffer strBuffer = new StringBuffer();
		int len;
		byte tmp[] = new byte[2048];
		while (!isStopRead && socketInputStream != null && (len = socketInputStream.read(tmp)) != -1) {
			if (responseCharsetName == null) {
				strBuffer.append(new String(tmp, 0, len));
			} else {
				strBuffer.append(new String(tmp, 0, len, responseCharsetName));
			}
			if (strBuffer.indexOf("\n\n") == strBuffer.length() - 2) {
				break;
			}
		}

		responsedMsg = strBuffer.toString();
		long endTime = System.currentTimeMillis();

		LOGGER.debug("\n<responsedMsg>\n{}\n</responsedMsg>\n\n<spendTime>{}ms</spendTime>\n", 
				responsedMsg,
				String.valueOf(endTime - startTime));

		checkParsedMsg(responsedMsg);
	}

	/**
	 * 
	 * Stop reading response message
	 * 
	 */
	public void stopRead() {
		this.isStopRead = true;
	}

	/**
	 * Set the heart beat time in milliseconds, this method takes effect only at
	 * the first time. this method is to monitor whether the socket connection
	 * status is OK
	 * 
	 * @param time
	 *            int The heart time in milliseconds
	 */
	public void setHeartbeat(final int time) {
		synchronized (this) {
			if (heartbeatThread == null) {
				heartbeatThread = new Thread("Monitoring " + hostAddress + ":" + port) {
					public void run() {
						while (ServerManager.getInstance().isConnected(hostAddress, port, userName)) {
							try {
								if (socket == null || socketInputStream == null) {
									ServerManager.getInstance().setConnected(hostAddress, port, userName, false);
									return;
								}
								if (!isBusy) {
									if (socketWriter != null) {
										socketWriter.write("keep_alive:1\n\n");
										socketWriter.flush();
									}
									int data = socketInputStream.read();
									if (data < 0) {
										ServerManager.getInstance().setConnected(hostAddress, port, userName, false);
										return;
									}
								}
								sleep(time);
							} catch (SocketException e) {
								LOGGER.debug(e.getMessage(), e);
								ServerManager.getInstance().setConnected(hostAddress, port, userName, false);
								break;
							} catch (IOException e) {
								LOGGER.debug(e.getMessage(), e);
								ServerManager.getInstance().setConnected(hostAddress, port, userName, false);
								break;
							} catch (InterruptedException e) {
								LOGGER.debug(e.getMessage(), e);
								ServerManager.getInstance().setConnected(hostAddress, port, userName, false);
								break;
							}
						}
						tearDownConnection();
						LOGGER.debug("The hearbeat thread {}:{} shutdown", hostAddress, port);
					}
				};
				heartbeatThread.start();
			}
		}
	}

	/**
	 * Check response message format, construct tree structure
	 * 
	 * @param buf
	 *            String The message to be check
	 */
	private void checkParsedMsg(String buf) {
		String newBuf = buf;
		if ((isUsingSpecialDelimiter && buf.indexOf("\nEND__DIAGDATA\n") >= 0)
				|| (!isUsingSpecialDelimiter && buf.indexOf("\n\n") >= 0)) {
			if (buf.length() <= 16) {
				errorMsg = Messages.error_messageFormat;
				return;
			}
			int idx = buf.indexOf("open:special");
			if (idx >= 0) {
				String spmsg = buf.substring(idx + 13);
				spmsg = spmsg.substring(0, spmsg.length() - 15);
				warningMsg = spmsg;
				newBuf = buf.substring(0, idx) + "\n";
			}

			response = MessageUtil.parseResponse(newBuf, isUsingSpecialDelimiter);

			String task = response.getValue("task");
			String status = response.getValue("status");
			String note = response.getValue("note");
			if (task == null || status == null || note == null) {
				errorMsg = Messages.error_messageFormat;
			} else if (status.trim().equals("failure")) { // fail
				errorMsg = note.replaceAll("<end>", "\n");
			} else if (status.trim().equals("warning")) { // warning
				warningMsg = note;
			} else if (status.trim().equals("success")) { // success
				errorMsg = null;
				warningMsg = null;
			}
		}
	}

	/**
	 * Tear down the socket connection
	 */
	public void tearDownConnection() {
		try {
			if (socketInputStream != null) {
				socketInputStream.close();
			}
		} catch (Exception e) {
		} finally {
			socketInputStream = null;
		}

		try {
			if (socketWriter != null) {
				socketWriter.close();
			}
		} catch (Exception e) {
		} finally {
			socketWriter = null;
		}

		try {
			if (socket != null) {
				socket.close();
			}
		} catch (Exception e) {
		} finally {
			socket = null;
		}
	}

	/**
	 * 
	 * Set whether using special delimiter
	 * 
	 * @param usingSpecialDelimiter
	 *            boolean Whether using the special delimiter
	 */
	public void setUsingSpecialDelimiter(boolean usingSpecialDelimiter) {
		this.isUsingSpecialDelimiter = usingSpecialDelimiter;
	}

	/**
	 * Get heart beat thread
	 * 
	 * @return Thread The heart beat thread
	 */
	public Thread getHeartbeatThread() {
		return heartbeatThread;
	}

	/**
	 * 
	 * Stop heart beat thread
	 * 
	 */
	public void stopHeartbeatThread() {
		if (heartbeatThread != null) {
			heartbeatThread.interrupt();
			heartbeatThread = null;
		}
	}

	/**
	 * 
	 * Set time out
	 * 
	 * @param timeout
	 *            int
	 */
	public void setTimeout(int timeout) {
		if (socket != null && socket.isConnected()) {
			try {
				socket.setSoTimeout(timeout);
			} catch (SocketException e) {
				LOGGER.error("", e);
			}
		}
		this.readTimeout = timeout;
		this.connectionTimeout = timeout;
	}

}
