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
package com.cubrid.cubridmanager.ui.mondashboard.editor.model;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.draw2d.geometry.Point;

import com.cubrid.common.core.util.StringUtil;
import com.cubrid.cubridmanager.core.broker.model.ApplyServerInfo;
import com.cubrid.cubridmanager.core.broker.model.BrokerStatusInfos;
import com.cubrid.cubridmanager.ui.mondashboard.Messages;

/**
 *
 * Broker node mode class
 *
 * @author pangqiren
 * @version 1.0 - 2010-8-17 created by pangqiren
 */
public class ClientNode extends
		HANode implements
		PropertyChangeListener {

	public final static String PROP_CLIENT_LIST = "PROP_CLIENT_LIST";

	private List<String> clientList = new ArrayList<String>();

	private BrokerNode brokerNode;

	private boolean visible = true;

	public ClientNode() {
		size.height = 98;
		size.width = 125;
		setName(Messages.brokerClientList);
	}

	/**
	 * Get the client ip list.
	 *
	 * @return the clientList
	 */
	public List<String> getClientList() {
		return clientList;
	}

	/**
	 * Set the client ip list.
	 *
	 * @param clientList the clientList to set
	 */
	public void setClientList(List<String> clientList) {
		this.clientList = clientList;
		fireStructureChange(PROP_CLIENT_LIST, clientList);
	}

	/**
	 * Get the broker node to connect
	 *
	 * @return the brokerNode
	 */
	public BrokerNode getBrokerNode() {
		return brokerNode;
	}

	/**
	 * Set the broker node to connect
	 *
	 * @param brokerNode the brokerNode to set
	 */
	public void setBrokerNode(BrokerNode brokerNode) {
		this.brokerNode = brokerNode;
		setLocation(brokerNode.getClientsLocation());
		brokerNode.addPropertyChangeListener(this);
	}

	/**
	 * Litsen the broker's status changing.
	 *
	 * @param evt PropertyChangeEvent
	 */
	public void propertyChange(PropertyChangeEvent evt) {
		if (BrokerNode.PROP_BROKER_STATUS.equals(evt.getPropertyName())) {
			BrokerStatusInfos bsi = brokerNode.getBrokerStatusInfos();
			clientList.clear();
			if (bsi == null || bsi.getAsinfo() == null
					|| bsi.getAsinfo().isEmpty()) {
				fireStructureChange(PROP_CLIENT_LIST, clientList);
				return;
			}
			for (ApplyServerInfo asi : bsi.getAsinfo()) {
				if (StringUtil.isEmpty(asi.getAsClientIp())
						|| clientList.contains(asi.getAsClientIp())) {
					continue;
				}
				clientList.add(asi.getAsClientIp());
			}
			fireStructureChange(PROP_CLIENT_LIST, clientList);
		}
	}

	/**
	 * Set node location.
	 *
	 * @param point new location
	 */
	public void setLocation(Point point) {
		super.setLocation(point);
		brokerNode.setClientsLocation(point);
	}

	public boolean isVisible() {
		return visible;
	}

	public void setVisible(boolean visible) {
		this.visible = visible;
	}
}
