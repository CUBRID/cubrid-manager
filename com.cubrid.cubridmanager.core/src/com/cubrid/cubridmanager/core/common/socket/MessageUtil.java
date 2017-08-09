/*
 * Copyright (C) 2013 Search Solution Corporation. All rights reserved by Search
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

import static com.cubrid.common.core.util.NoOp.noOp;

import java.util.HashMap;
import java.util.Map;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import com.cubrid.common.core.util.CipherUtils;

/**
 * <p>
 * The response message parser utility.
 * </p>
 *
 * @author moulinwang
 * @version 1.0 - 2009-6-4 created by moulinwang
 */
public final class MessageUtil {
	private MessageUtil() {
		noOp();
	}

	/**
	 * <p>
	 * Parse the response.
	 * </p>
	 *
	 * @param response String The response string
	 * @return {@link TreeNode}
	 */
	public static TreeNode parseResponse(String response) {
		return parseResponse(response, false);
	}

	/**
	 * <p>
	 * Parse response message into tree structure.
	 * </p>
	 *
	 * @param response String The response string
	 * @param bUsingSpecialDelimiter boolean Whether using the special delimiter
	 * @return {@link TreeNode}
	 */
	public static TreeNode parseResponse(String response, boolean bUsingSpecialDelimiter) {
		String[] toks = response.split("\n");

		TreeNode root = new TreeNode();
		TreeNode node = root;
		int maxIndex = toks.length;
		if (bUsingSpecialDelimiter && (toks[maxIndex - 1].equals("END__DIAGDATA"))) {
			// remove the last unneeded line
			maxIndex--;
		}
		for (int i = 0; i < maxIndex; i++) {
			StringBuilder tokBuffer = new StringBuilder();
			tokBuffer.append(toks[i]);
			// append next line if it does not contain char ':'
			while (i + 1 < maxIndex && -1 == toks[i + 1].indexOf(":")) {
				// failure's note  message and others
				tokBuffer.append(toks[i + 1]);
				i++;
			}
			// decrypt the message
			String tokStr = tokBuffer.toString();
			if (tokStr.startsWith("@")) {
				String[] entry = tokStr.split(":");
				if (entry != null && entry.length >= 2) {
					String key = entry[0];
					String value = tokStr.substring(tokStr.indexOf(":") + 1);
					tokStr = key.replace("@", "") + ":";
					tokStr += CipherUtils.decrypt(value);
				}
			}

			if (tokStr.startsWith("cas_mon:DIAG_DEL:start") || tokStr.startsWith("open:")
					|| tokStr.startsWith("start:")) {
				TreeNode newnode = new TreeNode();
				node.addChild(newnode);
				node = newnode;
				addMsgItem(node, tokStr, bUsingSpecialDelimiter);
			} else if (tokStr.startsWith("cas_mon:DIAG_DEL:end") || tokStr.startsWith("close:")
					|| tokStr.startsWith("end:")) {
				addMsgItem(node, tokStr, bUsingSpecialDelimiter);
				node = node.getParent();
			} else {
				addMsgItem(node, tokStr, bUsingSpecialDelimiter);
			}
		}
		return root;
	}

	/**
	 * <p>
	 * Parse JSON response message into tree structure.<br>
	 * Assemble it according to the previous format.
	 * </p>
	 *
	 * @param response String The response string
	 * @return {@link TreeNode}
	 */
	@SuppressWarnings("unchecked")
	public static TreeNode parseJsonResponse(String response) {
		JSONObject responseObj = (JSONObject) JSONValue.parse(response);

		// TOOLS-4132 CM can't make the broker_log_top result - fixed by cmserver https api bug
		if (responseObj.containsKey("result") && responseObj.containsKey("resultlist")
				&& responseObj.get("result") instanceof JSONArray) {
			JSONArray resultlist = new JSONArray();
			JSONArray results = (JSONArray) responseObj.get("result");
			responseObj.remove("result");
			responseObj.remove("resultlist");

			for (int i = 0; i < results.size(); i++) {
				JSONObject result = new JSONObject();
				result.put("result", results.get(i));
				resultlist.add(result);
			}

			responseObj.put("resultlist", resultlist);
		} else if (responseObj.containsKey("logstring") && responseObj.containsKey("logstringlist")) {
			JSONArray resultlist = new JSONArray();
			JSONArray results = new JSONArray(); //(JSONArray) responseObj.get("logstring");
			results.add(responseObj.get("logstring"));
			responseObj.remove("logstring");
			responseObj.remove("logstringlist");

			for (int i = 0; i < results.size(); i++) {
				JSONObject result = new JSONObject();
				result.put("logstring", results.get(i));
				resultlist.add(result);
			}

			responseObj.put("logstringlist", resultlist);
		}

		TreeNode root = new TreeNode();
		parseJson(null, responseObj, root);
		return root;
	}

	/**
	 * <p>
	 * Parse the JSON response message.
	 * </p>
	 *
	 * @param key
	 * @param value
	 * @param node
	 */
	private static void parseJson(String key, Object value, TreeNode node) {
		if (value == null && key != null) {
			// TODO TEMPORARILY did this
			// null --> empty array --> as follows
			TreeNode newnode = new TreeNode();
			node.addChild(newnode);
			node = newnode;
			addMsgItem(node, "open:" + key, false);
			addMsgItem(node, "close:" + key, false);
			node = node.getParent();
		} else if (String.class.isAssignableFrom(value.getClass()) && key != null) {
			if (key.startsWith("@")) {
				key = key.replace("@", "");
				// new interface didn't encrypt the @data.
				// value = CipherUtils.decrypt(value.toString());
			}
			addMsgItem(node, key + ":" + value, false);
		} else if (Long.class.isAssignableFrom(value.getClass()) && key != null) {
			if (key.startsWith("@")) {
				key = key.replace("@", "");
				// new interface didn't encrypt the @data.
				// value = CipherUtils.decrypt(value.toString());
			}
			addMsgItem(node, key + ":" + value, false);
		} else if (value != null && JSONObject.class.isAssignableFrom(value.getClass())) {
			// TOOLS-4132 CM can't make the broker_log_top result - fixed by cmserver https api bug
			// resultlist:start ~ result:start ~ result:end ~ resultlist:end
			if ("resultlist".equals(key) || "result".equals(key) || "logstringlist".equals(key)) {
				if (key != null) {
					addMsgItem(node, key + ":start", false);
				}
				for (Object responseField : ((JSONObject) value).entrySet()) {
					Map.Entry<?, ?> responseKV = (Map.Entry<?, ?>) responseField;
					Object innerKey = responseKV.getKey();
					Object innerValue = responseKV.getValue();
					parseJson(innerKey.toString(), innerValue, node);
				}
				if (key != null) {
					addMsgItem(node, key + ":end", false);
				}
			} else {
				// open:xxx ~ close:xxx
				if (key != null) {
					TreeNode newnode = new TreeNode();
					node.addChild(newnode);
					node = newnode;
					addMsgItem(newnode, "open:" + key, false);
				}
				for (Object responseField : ((JSONObject) value).entrySet()) {
					Map.Entry<?, ?> responseKV = (Map.Entry<?, ?>) responseField;
					Object innerKey = responseKV.getKey();
					Object innerValue = responseKV.getValue();
					parseJson(innerKey.toString(), innerValue, node);
				}
				if (key != null) {
					addMsgItem(node, "close:" + key, false);
					node = node.getParent();
				}
			}
		} else if (value != null && JSONArray.class.isAssignableFrom(value.getClass())) {
			for (Object obj : (JSONArray) value) {
				parseJson(key, obj, node);
			}
		}
	}

	/**
	 * <p>
	 * Parse request message to JSON format
	 * </p>
	 *
	 * @param response String The response string
	 * @param bUsingSpecialDelimiter boolean Whether using the special delimiter
	 * @return {@link TreeNode}
	 */
	public static String parseRequestToJson(String request) {
		StringBuilder sb = new StringBuilder("{");
		String[] toks = request.split("\n");

		int maxIndex = toks.length;
		boolean beginning = true;
		boolean confdataTag = false;
		boolean groupTag = false;
		boolean fileTag = false;
		boolean classTag = false;
		String arrayName = null;
		// [TOOLS-3586] escape backslash('\')
		// From RFC 4627:
		// All Unicode characters may be placed within the quotation marks except for the characters that must be escaped:
		// quotation mark, reverse solidus, and the control characters (U+0000 through U+001F).
		for (int i = 0; i < maxIndex; i++) {
			StringBuilder tokBuffer = new StringBuilder();
			tokBuffer.append(toks[i]);
			while (i + 1 < maxIndex && -1 == toks[i + 1].indexOf(":")) {
				tokBuffer.append(toks[i + 1]);
				i++;
			}
			String tokStr = tokBuffer.toString();

			char tag = sb.charAt(sb.length() - 1);
			beginning = tag == '{' || tag == '[';

			if (tokStr.startsWith("open:")) {
				int index = tokStr.indexOf(":");
				String key = tokStr.substring(index + 1);

				boolean tmp = key.equals(arrayName) && (sb.charAt(sb.length() - 1) == ']');
				if (tmp) {
					sb.replace(sb.length() - 1, sb.length(), "");
				}

				if (!beginning) {
					sb.append(",");
				}

				if (tmp) {
					sb.append("{");
				} else {
					arrayName = key;
					sb.append("\"").append(key).append("\":[{");
				}
			} else if (tokStr.startsWith("close:")) {
				sb.append("}]");
			} else if (tokStr.startsWith("confdata:")) { // Special circumstances
				confdataTag = generateKeyAndValue(
						"confdata", tokStr, sb, confdataTag, beginning);
			} else if (tokStr.startsWith("group:")) { // Special circumstances
				groupTag = generateKeyAndValue(
						"group", tokStr, sb, groupTag, beginning);
			} else if (tokStr.startsWith("file:")) { // Special circumstances
				fileTag = generateKeyAndValue(
						"file", tokStr, sb, fileTag, beginning);
			} else if (tokStr.startsWith("classname:")) {
				classTag = generateKeyAndValue(
						"class", tokStr, sb, classTag, beginning);
			} else if (tokStr.startsWith("interval:")) {
				//TODO: later check all type request param, to enable integer value in JSON.
				int index = tokStr.indexOf(":");
				String key = "interval";
				String value = tokStr.substring(index + 1);
				if (!beginning) {
					sb.append(",");
				}
				sb.append("\"").append(key).append("\"");
				sb.append(":").append(value);
			} else if (tokStr.contains(":")) {
				int index = tokStr.indexOf(":");
				String key = tokStr.substring(0, index);
				String value = tokStr.substring(index + 1);
				value = value.replace("\\", "\\\\");
				value = value.replace("\"", "\\\"");
				if (!beginning) {
					sb.append(",");
				}
				sb.append("\"").append(key).append("\":\"").append(value).append("\"");

			}
		}
		sb.append("}");
		// TOOLS-3478: deal with empty JSON object in JSON array: [{}], change it to empty JSON array: []
		int idx = sb.indexOf("[{}]");
		if (idx != -1) {
			sb.replace(idx, idx + 4, "[]");
		}
		return sb.toString();
	}

	private static boolean generateKeyAndValue(String key, String data,
			StringBuilder sb, boolean bool, boolean beginning) {
		int index = data.indexOf(":");
		String value = data.substring(index + 1);
		value = value.replace("\\", "\\\\");
		value = value.replace("\"", "\\\"");

		if (!bool) {
			if (!beginning) {
				sb.append(",");
			}
			sb.append("\"").append(key).append("\":[");
			bool = true;
		} else {
			sb.replace(sb.length() - 1, sb.length(), "");
			if (!beginning) {
				sb.append(",");
			}
		}

		if (key.equals("class")) {
			sb.append("{\"").append("classname").append("\":")
			.append("\"").append(value).append("\"}]");
		} else {
			sb.append("\"").append(value).append("\"").append("]");
		}
		return bool;
	}

	/**
	 * <p>
	 * Add a message item into TreeNode node assert each message item contains
	 * char ':'
	 * </p>
	 *
	 * @param node TreeNode The tree node
	 * @param msgitem String The message item
	 * @param bUsingSpecialDelimiter boolean Whether using the special delimiter
	 */
	private static void addMsgItem(TreeNode node, String msgitem, boolean bUsingSpecialDelimiter) {
		assert (msgitem.indexOf(":") != -1);
		if (bUsingSpecialDelimiter) {
			int index = msgitem.indexOf(":DIAG_DEL:");
			if (index >= 0) {
				String key = msgitem.substring(0, index);
				String value = msgitem.substring(index + 10);
				node.add(key, value);
			} else {
				node.add(msgitem);
			}
		} else {
			node.add(msgitem);
		}
	}

	/**
	 * <p>
	 * Generate result map.
	 * </p>
	 *
	 * @param tag
	 * @param message
	 * @return
	 */
	public static Map<String, String> generateResult(boolean tag, String message) {
		Map<String, String> result = new HashMap<String, String>();
		result.put("valid", String.valueOf(tag));
		result.put("message", message);
		return result;
	}

	/**
	 * <p>
	 * Get result tag.
	 * </p>
	 *
	 * @param result
	 * @return
	 */
	public static boolean getResultTag(Map<String, String> result) {
		return Boolean.valueOf(result.get("valid"));
	}

	/**
	 * <p>
	 * Get result message.
	 * </p>
	 *
	 * @param result
	 * @return
	 */
	public static String getResultMessage(Map<String, String> result) {
		return result.get("message");
	}
}
