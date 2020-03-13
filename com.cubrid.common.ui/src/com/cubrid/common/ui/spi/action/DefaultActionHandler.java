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
package com.cubrid.common.ui.spi.action;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.util.Util;

/**
 * 
 * Default action handler execute action when key binding active,this handler is
 * binded to parameter command(com.cubrid.navigator.command) in plugin.xml.
 * 
 * <p>
 * All actions can use this handler to execute, provide the access key by
 * parameter command and key binding for action
 * </p>
 * 
 * @author pangqiren
 * @version 1.0 - 2010-12-14 created by pangqiren
 */
public class DefaultActionHandler extends
		AbstractHandler {

	private static final String QUERY_NEW_ACTION_ID = "com.cubrid.cubridmanager.ui.common.action.QueryNewAction";
	
	/**
	 * Execute the handler action
	 * 
	 * @param event ExecutionEvent
	 * @return Object
	 * @throws ExecutionException The exception
	 */
	public Object execute(ExecutionEvent event) throws ExecutionException {
		String actionId = event.getParameter("com.cubrid.navigator.action.id");
		
		if (actionId == null || actionId.trim().length() == 0) {
			return null;
		}

		if (Util.isWindows()
				&& QUERY_NEW_ACTION_ID.equals(actionId)) {
			return null;
		}
		
		IAction action = ActionManager.getInstance().getAction(actionId);
		if (action != null && action.isEnabled()) {
			action.run();
		}
		
		return null;
	}

}
