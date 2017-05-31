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

package com.cubrid.common.core.common.model;

import java.text.DecimalFormat;
import java.util.Locale;

/**
 *
 * Trigger is a system provided object, it is raised by database events,
 * before/after the events are done or at the end of a transaction, some
 * condition is evaluated, only if true, some trigger action is took.
 *
 * <li>database events: insert,update,delete(statement
 * insert,update,delete),commit, roll back <li>condition time: before/after the
 * database event or deferred to the end of transaction <li>trigger action:
 * print some information, call statements or reject the database events
 *
 * 3 types actions are evaluated(evaluating trigger condition, trigger action,
 * database event), the possible orders of them are as below:
 *
 * <li>condition-->trigger action-->database event (configuration:
 * conditionTime=before actionTime=before) eg: create trigger new_cost_hotel
 * before update on hotel(cost) if new.cost > $200 execute reject;
 *
 * <li>condition-->database event-->trigger action (configuration:
 * conditionTime=before actionTime=after) eg: create trigger limit_pools before
 * update on resort(number_of_pools) if new.number_of_pools > 0 execute after
 * update resort set number_of_pools = new.number_of_pools -1 where name =
 * obj.name;
 *
 * <li>database event-->condition-->trigger action (configuration:
 * conditionTime=after actionTime=after) eg: create trigger
 * deferred_check_on_cost deferred update on accommodations if obj.cost < 0
 * execute invalidate transaction;
 *
 * @author moulinwang 2009-3-30
 */
public class Trigger implements
		Comparable<Trigger> { // FIXME use javadoc style comment such as /** ~ */ for all methods
	// trigger name
	private String name;
	// the time to evaluate trigger condition: before,after,deferred
	private String conditionTime;
	// 8 types: insert,update,delete(statement
	// insert,update,delete),commit,rollback
	private String eventType;
	// the class or class attribute on which the trigger operates
	private String target_class;
	private String target_attribute;
	// the condition
	private String condition;
	// the time to take action
	private String actionTime;
	// action type: print,reject, invalidate transaction, call statements
	private String actionType;
	// action string
	private String action;
	// whether the trigger is active: ACTIVE or INACTIVE
	private String status;
	// the trigger order
	private String priority;
	// the trigger comment
	private String description;

	/**
	 * compare to the argument obj
	 *
	 * @param obj Trigger
	 * @return int
	 */
	public int compareTo(Trigger obj) {
		return name.compareTo(obj.name);
	}

	/**
	 * @param obj the reference object with which to compare.
	 * @return true if this object is the same as the obj argument; false
	 *         otherwise.
	 */
	@Override
	public boolean equals(Object obj) {
		return obj instanceof Trigger && name.equals(((Trigger) obj).name);
	}

	/**
	 * @return a hash code value for this object.
	 */
	@Override
	public int hashCode() {
		return name.hashCode();
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getConditionTime() {
		return conditionTime;
	}

	public void setConditionTime(String conditionTime) {
		this.conditionTime = conditionTime;
	}

	public String getEventType() {
		return eventType;
	}

	public void setEventType(String eventType) {
		this.eventType = eventType;
	}

	public String getTarget_class() {
		return target_class;
	}

	public void setTarget_class(String targetClass) {
		this.target_class = targetClass;
	}

	public String getTarget_att() {
		return target_attribute;
	}

	public void setTarget_att(String targetAttribute) {
		this.target_attribute = targetAttribute;
	}

	public String getCondition() {
		return condition;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getDescription() {
		return description;
	}

	/**
	 * set condition
	 *
	 * @param condition String
	 */
	public void setCondition(String condition) {
		if (condition == null) {
			return;
		}
		String triggerCondition = condition.trim();
		if (triggerCondition.length() > 0
				&& triggerCondition.toLowerCase(Locale.getDefault()).startsWith(
						"if ")) {
			triggerCondition = triggerCondition.substring(3);
		}
		this.condition = triggerCondition;
	}

	public String getActionTime() {
		return actionTime;
	}

	public void setActionTime(String actionTime) {
		this.actionTime = actionTime;
	}

	public String getActionType() {
		return actionType;
	}

	public void setActionType(String actionType) {
		this.actionType = actionType;
	}

	public String getAction() {
		return action;
	}

	/**
	 * set action
	 *
	 * @param action String
	 */
	public void setAction(String action) {
		boolean found = false;
		if (action != null) {
			if (action.startsWith(TriggerAction.REJECT.getText())) {
				this.actionType = TriggerAction.REJECT.getText();
				this.action = null;
				found = true;
			} else if (action.startsWith(TriggerAction.INVALIDATE_TRANSACTION.getText())) {
				this.actionType = TriggerAction.INVALIDATE_TRANSACTION.getText();
				this.action = null;
				found = true;
			} else if (action.startsWith(TriggerAction.PRINT.getText())) {
				this.actionType = TriggerAction.PRINT.getText();
				String message = action.replace(TriggerAction.PRINT.getText(),
						"").trim();
				message = message.substring(1, message.length() - 1);
				this.action = message;
				found = true;
			}
		}

		if (!found) {
			this.actionType = TriggerAction.OTHER_STATEMENT.getText();
			this.action = action;
		}
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getPriority() {
		return priority;
	}

	/**
	 * set priority
	 *
	 * @param priority String
	 */
	public void setPriority(String priority) {
		try {
			Double.parseDouble(priority);
			this.priority = Trigger.formatPriority(priority);
		} catch (NumberFormatException e) {
			this.priority = priority;
		}
	}

	/**
	 * format priority
	 *
	 * @param priority String
	 * @return String
	 */
	public static String formatPriority(String priority) {
		DecimalFormat formatter = new DecimalFormat("##00.00");
		return formatter.format(Double.parseDouble(priority));
	}

	/**
	 * This enum indicates the four actions of trigger
	 *
	 * @author sq
	 * @version 1.0 - 2009-12-29 created by sq
	 */
	public enum TriggerAction {
		PRINT("PRINT"), REJECT("REJECT"), INVALIDATE_TRANSACTION(
				"INVALIDATE TRANSACTION"), OTHER_STATEMENT("OTHER STATEMENT");

		String text;

		TriggerAction(String text) {
			this.text = text;
		}

		public String getText() {
			return text;
		}

		/**
		 * Get the instance of TriggerAction whose text is equals the given text
		 *
		 * @param text String
		 * @return TriggerEvent
		 */
		public static TriggerAction eval(String text) {
			TriggerAction[] array = TriggerAction.values();
			for (TriggerAction a : array) {
				if (a.getText().equals(text)) {
					return a;
				}
			}
			return null;
		}
	}
}
