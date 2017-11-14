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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.UnknownHostException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.slf4j.Logger;

import com.cubrid.common.core.util.LogUtil;
import com.cubrid.common.core.util.StringUtil;
import com.cubrid.cubridmanager.core.Messages;
import com.cubrid.cubridmanager.core.common.ServerManager;

/**
 * To provide HTTP service for upper layer, send message, receive message and
 * parse message for upper layer to use. <li>Create a HTTP connection to send
 * request and parse response <li>Handle with the exception <li>If multiple
 * threads access this concurrently,it must be synchronized externally
 *
 * @author Tobi
 * @version 1.0
 * @date 2012-10-17
 *
 */
public class ClientHttp extends AbstractManagerClient {
	private static final Logger LOGGER = LogUtil.getLogger(ClientHttp.class);
	private static final String METHOD = "/cm_api";
	private HttpsURLConnection conn;
	private String requestUrl;
	private int timeout = 300000;

	/**
	 * Constructor
	 *
	 * @param hostAddress
	 * @param port
	 * @param userName
	 */
	public ClientHttp(String hostAddress, int port, String userName) {
		this(hostAddress, port, userName, "UTF-8", "UTF-8");
	}

	/**
	 * Constructor
	 *
	 * @param hostAddress
	 * @param port
	 * @param userName
	 * @param password
	 * @param requestCharset
	 * @param responseCharset
	 */
	public ClientHttp(String hostAddress, int port, String userName, String requestCharset, String responseCharset) {
		this.hostAddress = hostAddress;
		this.port = port;
		this.userName = userName;
		this.requestCharsetName = requestCharset;
		this.responseCharsetName = responseCharset;

	}

	/**
	 * Set up a http client
	 *
	 * @throws UnknownHostException a possible exception
	 * @throws IOException a possible exception
	 */
	private void setUpConnection() {
		tearDownConnection();

		this.requestUrl = "https://" + hostAddress + ":" + port + METHOD;

		// support https
		try {
			X509TrustManager tm = new X509TrustManager() {
				public void checkClientTrusted(X509Certificate[] xcs, String string) throws CertificateException {
				}

				public void checkServerTrusted(X509Certificate[] xcs, String string) throws CertificateException {
				}

				public X509Certificate[] getAcceptedIssuers() {
					return new X509Certificate[0];
				}
			};
			SSLContext ctx = SSLContext.getInstance("TLS");
			ctx.init(null, new TrustManager[]{tm}, new SecureRandom());

			HttpsURLConnection.setDefaultSSLSocketFactory(ctx.getSocketFactory());
			HttpsURLConnection.setDefaultHostnameVerifier(new HostnameVerifier() {
				public boolean verify(String hostname, SSLSession session) {
					return true;
				}
			});

			URL url = new URL(requestUrl);
			conn = (HttpsURLConnection) url.openConnection();
			conn.setRequestMethod("POST");
			conn.setConnectTimeout(timeout);
			conn.setDoInput(true);
			conn.setDoOutput(true);
			conn.setRequestProperty("Content-Type", "application/json");
		} catch (Exception e) {
			LOGGER.error("Make to support HTTPS failed.", e);
		}

	}

	/**
	 * Send request (JSON data) to CUBRID Manager server
	 *
	 * @param message json data
	 */
	public void sendRequest(String message) {
		long startTime = System.currentTimeMillis();

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("\n<sentMsg>\n" + message + "\n</sentMsg>\n");
		}

		this.setUpConnection();

		OutputStreamWriter out = null;
		BufferedReader br = null;
		try {
			// New CMS does not support concurrent.
			// TODO Separated by db
			synchronized (ClientHttp.class) {
				conn.connect();

				/*TOOLS-3562*/
				if (StringUtil.isEmpty(requestCharsetName)) {
					out = new OutputStreamWriter(conn.getOutputStream());
				} else {
					out = new OutputStreamWriter(conn.getOutputStream(),
							requestCharsetName);
				}
				out.write(message);
				out.flush();
				out.close();

				int responseCode = conn.getResponseCode();
				if (responseCode >= 400) {
					/*TOOLS-3562*/
					if (StringUtil.isEmpty(responseCharsetName)) {
						br = new BufferedReader(new InputStreamReader(
								conn.getErrorStream()));
					} else {
						br = new BufferedReader(new InputStreamReader(
								conn.getErrorStream(), responseCharsetName));
					}
				} else {
					/*TOOLS-3562*/
					if (StringUtil.isEmpty(responseCharsetName)) {
						br = new BufferedReader(new InputStreamReader(
								conn.getInputStream()));
					} else {
						br = new BufferedReader(new InputStreamReader(
								conn.getInputStream(), responseCharsetName));
					}
				}
				StringBuilder sb = new StringBuilder();
				String line;
				while ((line = br.readLine()) != null) {
					sb.append(line + "\n");
				}
				br.close();
				//CMS will return 200, when build connection successfully
				if (responseCode == 200) {
					responsedMsg = sb.toString();
					checkParsedMsg(responsedMsg);
				} else {
					errorMsg = sb.toString();
				}
			}
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
			if (e.getMessage() != null && (e.getMessage().startsWith("peer not authenticated")
					|| e.getMessage().startsWith("Connection to ")
					&& e.getMessage().endsWith(" refused"))) {
				errorMsg = Messages.errCannotConnectToCmServer;
			} else {
				errorMsg = e.getMessage();
			}
			super.canConnect = false;
			ServerManager.getInstance().setConnected(hostAddress, port, userName, false);
		}
		// TODO Add identifiable error message in the above code.

		long endTime = System.currentTimeMillis();

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("\n<responsedMsg>\n" + responsedMsg + "\n</responsedMsg>\n" + "\n<spendTime>"
					+ (endTime - startTime) + "ms</spendTime>\n");
		}

		if (LOGGER.isDebugEnabled() && errorMsg != null && errorMsg.trim().length() > 0) {
			LOGGER.debug("\n<errorMsg>\n" + errorMsg + "\n</errorMsg>\n");
		}
	}

	/**
	 * Construct tree structure from the result string
	 *
	 * @param buf The message to be check
	 */
	private void checkParsedMsg(String buf) {
		// parse response from JSON data
		response = MessageUtil.parseJsonResponse(buf);
		String task = response.getValue("task");
		String status = response.getValue("status");
		String note = response.getValue("note");
		if (task == null || status == null || note == null) {
			errorMsg = Messages.error_messageFormat;
		} else if (status.trim().equals("failure")) { // fail
			errorMsg = note;
		} else if (status.trim().equals("success")) { // success
			errorMsg = null;
			warningMsg = null;
		}
	}

	/**
	 * Tear down the socket connection
	 */
	public void tearDownConnection() {
		if (null != conn) {
			this.conn.disconnect();
			this.conn = null;
		}
	}

	/**
	 * Set time out
	 *
	 * @param timeout
	 */
	public void setTimeout(int timeout) {
		if (null != conn) {
			conn.setConnectTimeout(timeout);
		}
		this.timeout = timeout;
	}
}
