/*
 * Copyright (C) 2009 Search Solution Corporation. All rights reserved by Search Solution.
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
package com.cubrid.common.core.queryplan.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Plan Term model class
 *
 * PlanTerm Description
 *
 * @author pcraft
 * @version 1.0 - 2009. 06. 06 created by pcraft
 */
public class PlanTerm {
	/**
	 * The plan term type
	 *
	 * @author pcraft
	 * @version 1.0 - 2009-12-29 created by pangqiren
	 */
	public enum PlanTermType {
		SARGS, EDGE, INDEX, FILTER
	}

	private PlanTermType type = null;
	private String name = null;
	private final List<PlanTermItem> termItem = new ArrayList<PlanTermItem>();

	public String toString() { // FIXME use ToStringBuilder
		StringBuilder out = new StringBuilder();
		out.append("PlanTerm[");
		out.append("\n\tname=").append(name);
		out.append(", \n\ttype=").append(type);
		out.append(", \n\titems=").append(termItem);
		out.append("\n]\n");
		return out.toString();
	}

	/**
	 * For debugging to display for more pretty
	 */
	public static String toString(PlanTerm planTerm, int depth) {
		if (planTerm == null) {
			return null;
		}
		StringBuilder tabs = new StringBuilder();
		for (int i = 0; i < depth; i++) {
			tabs.append("\t");
		}
		StringBuilder out = new StringBuilder();
		out.append("PlanTerm[");
		out.append("\n\t").append(tabs).append("name=").append(planTerm.getName());
		out.append(", \n\t").append(tabs).append("type=").append(planTerm.getTypeString());
		out.append(", \n\t").append(tabs).append("items=").append(PlanTermItem.toString(planTerm.getTermItems(), depth + 1));
		out.append("\n").append(tabs).append("]");

		return out.toString();
	}

	/**
	 * Get term string
	 *
	 * @return the string
	 */
	public String getTermString() {
		int len = termItem.size();
		if (len == 0) {
			return null;
		}

		StringBuilder out = new StringBuilder();

		for (PlanTermItem item : termItem) {
			if (out.length() > 0) {
				out.append("\n");
			}
			out.append(item.getCondition());
		}

		return out.toString();
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public PlanTermType getType() {
		return type;
	}

	/**
	 * Get type string
	 *
	 * @return the string
	 */
	public String getTypeString() {
		if (getType() == null) {
			return "";
		}

		switch (getType()) {
		case EDGE:
			return "edge";
		case FILTER:
			return "filter";
		case INDEX:
			return "index";
		case SARGS:
			return "sargs";
		default:
			return "";
		}
	}

	public void setType(PlanTermType type) {
		this.type = type;
	}

	/**
	 * Get term items
	 *
	 * @return the PlanTermItem array
	 */
	public PlanTermItem[] getTermItems() {
		if (termItem.isEmpty()) {
			return null;
		}
		PlanTermItem[] arr = new PlanTermItem[termItem.size()];
		return termItem.toArray(arr);
	}

	/**
	 * Add plan term item
	 *
	 * @param item the PlanTermItem
	 */
	public void addTermItem(PlanTermItem item) {
		this.termItem.add(item);
	}

	/**
	 * Has a single term?
	 */
	public boolean hasSingleTerm() {
		return getTermItems() != null && getTermItems().length == 1;
	}
}
