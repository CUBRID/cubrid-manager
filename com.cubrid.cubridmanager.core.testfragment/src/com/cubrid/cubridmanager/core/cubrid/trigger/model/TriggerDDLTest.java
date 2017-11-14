/*
 * Copyright (C) 2013 Search Solution Corporation. All rights reserved by Search Solution. 
 *
 * Redistribution and use in source and binary forms, with or without modification, 
 * are permitted provided that the following conditions are met: 
 *
 * - Redistributions of source code must retain the above copyright notice, 
 *   this list of conditions and the following disclaimer. 
 *
 * - Redistributions in binary form must reproduce the above copyright notice, 
 *   this list of conditions and the following disclaimer in the documentation 
 *   and/or other materials provided with the distribution. 
 *
 * - Neither the name of the <ORGANIZATION> nor the names of its contributors 
 *   may be used to endorse or promote products derived from this software without 
 *   specific prior written permission. 
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND 
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED 
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. 
 * IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, 
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, 
 * BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, 
 * OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, 
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) 
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY 
 * OF SUCH DAMAGE. 
 *
 */
package com.cubrid.cubridmanager.core.cubrid.trigger.model;

import com.cubrid.common.core.common.model.Trigger;

import junit.framework.TestCase;

/**
 * test trigger model
 * 
 * @author wuyingshi
 * @version 1.0 - 2009-12-30 created by wuyingshi
 */
public class TriggerDDLTest extends
		TestCase {
	Trigger trigger;
	String name;
	String conditionTime;
	String eventType;
	String targetClass;
	String targetAttribute;
	String condition;
	String actionTime;
	String actionType;
	String action;
	String status;
	String priority;

	/**
	 * Sets up the fixture, for example, open a network connection. This method
	 * is called before a test is executed.
	 */
	protected void setUp() throws Exception {
		super.setUp();
		trigger = new Trigger
			.Builder()
			.build();
		name = "name";
		conditionTime = "conditionTime";
		eventType = "eventType";
		targetClass = "targetClass";
		targetAttribute = "targetAttribute";
		condition = "condition";
		actionTime = "actionTime";
		actionType = "actionType";
		action = "action";
		status = "status";
		priority = "0.00123";
	}

	/**
	 * Test method for
	 * {@link com.cubrid.cubridmanager.core.cubrid.trigger.model.TriggerDDL#getDDL(com.cubrid.common.core.common.model.Trigger)}
	 * .
	 */
	public void testGetDDL() {
		trigger = new Trigger
			.Builder()
			.name(null)
			.conditionTime(conditionTime)
			.eventType(eventType)
			.targetClass("")
			.targetAttribute(targetAttribute)
			.condition("")
			.actionTime("")
			.actionType("REJECT")
			.action("REJECT")
			.status(status)
			.priority(priority)
			.build();
		TriggerDDL.getDDL(trigger);
		
		trigger = new Trigger
			.Builder()
			.name(name)
			.conditionTime(conditionTime)
			.eventType(eventType)
			.targetClass(targetClass)
			.targetAttribute(targetAttribute)
			.condition(condition)
			.actionTime(actionTime)
			.actionType(actionType)
			.action(action)
			.status(status)
			.priority(priority)
			.build();
		TriggerDDL.getDDL(trigger);
		
		trigger = new Trigger
			.Builder()
			.name(null)
			.conditionTime(conditionTime)
			.eventType(eventType)
			.targetClass("")
			.targetAttribute(targetAttribute)
			.condition("")
			.actionTime("AFTER")
			.actionType("REJECT")
			.action("REJECT")
			.status(status)
			.priority(priority)
			.build();
		TriggerDDL.getDDL(trigger);

		trigger = new Trigger
			.Builder()
			.name(name)
			.conditionTime(conditionTime)
			.eventType(eventType)
			.targetClass(targetClass)
			.targetAttribute(targetAttribute)
			.condition(condition)
			.actionTime(actionTime)
			.actionType("PRINT")
			.action("PRINT_action")
			.status(status)
			.priority(priority)
			.build();
		TriggerDDL.getDDL(trigger);

		trigger = new Trigger
			.Builder()
			.name(name)
			.conditionTime(conditionTime)
			.eventType(eventType)
			.targetClass(targetClass)
			.targetAttribute(targetAttribute)
			.condition(condition)
			.actionTime(actionTime)
			.actionType("PRINT")
			.action("PRINT_action")
			.status(status)
			.priority("100")
			.build();
		TriggerDDL.getDDL(trigger);

		trigger = new Trigger
			.Builder()
			.name(name)
			.conditionTime(conditionTime)
			.eventType(eventType)
			.targetClass(targetClass)
			.targetAttribute(targetAttribute)
			.condition(condition)
			.actionTime(actionTime)
			.actionType(actionType)
			.action(action)
			.status(status)
			.priority("null")
			.build();
		TriggerDDL.getDDL(trigger);

	}

	/**
	 * Test method for
	 * {@link com.cubrid.cubridmanager.core.cubrid.trigger.model.TriggerDDL#getAlterDDL(com.cubrid.common.core.common.model.Trigger, com.cubrid.common.core.common.model.Trigger)}
	 * .
	 */
	public void testGetAlterDDL() {
		trigger = new Trigger
			.Builder()
			.name(name)
			.conditionTime(conditionTime)
			.eventType(eventType)
			.targetClass(targetClass)
			.targetAttribute(targetAttribute)
			.condition(condition)
			.actionTime(actionTime)
			.actionType(actionType)
			.action(action)
			.status(status)
			.priority(priority)
			.build();
		Trigger newTrigger = new Trigger
			.Builder()
			.name(null)
			.conditionTime(conditionTime)
			.eventType(eventType)
			.targetClass(targetClass)
			.targetAttribute(targetAttribute)
			.condition("")
			.actionTime("aaa")
			.actionType("PRINT")
			.action(action)
			.status(status)
			.priority(priority)
			.build();
		TriggerDDL.getAlterDDL(trigger, newTrigger);
		
		trigger = new Trigger
			.Builder()
			.name(name)
			.conditionTime(conditionTime)
			.eventType(eventType)
			.targetClass(targetClass)
			.targetAttribute(targetAttribute)
			.condition(condition)
			.actionTime(actionTime)
			.actionType(actionType)
			.action(action)
			.status(status)
			.priority(priority)
			.build();
		newTrigger = new Trigger
			.Builder()
			.name(null)
			.conditionTime(conditionTime)
			.eventType(eventType)
			.targetClass(targetClass)
			.targetAttribute(targetAttribute)
			.condition("")
			.actionTime("aaa")
			.actionType("PRINT")
			.action(action)
			.status("no")
			.priority("99")
			.build();
		TriggerDDL.getAlterDDL(trigger, newTrigger);
	}

}
