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
package com.cubrid.cubridmanager.app;

import org.eclipse.jface.preference.PreferenceManager;
import org.eclipse.ui.IWorkbenchPreferenceConstants;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.application.IWorkbenchConfigurer;
import org.eclipse.ui.application.IWorkbenchWindowConfigurer;
import org.eclipse.ui.application.WorkbenchAdvisor;
import org.eclipse.ui.application.WorkbenchWindowAdvisor;
import org.eclipse.ui.model.ContributionComparator;
import org.eclipse.ui.model.IContributionService;

import com.cubrid.common.ui.cubrid.table.preference.ImportPreferencePage;
import com.cubrid.cubridmanager.core.cubrid.table.model.DataType;

/**
 *
 * This workbench advisor creates the window advisor, and specifies the
 * perspective id for the initial window.
 *
 * @author pangqiren
 * @version 1.0 - 2009-12-23 created by pangqiren
 */
public class ApplicationWorkbenchAdvisor extends
		WorkbenchAdvisor {

	/**
	 * Creates a new workbench window advisor for configuring a new workbench
	 * window via the given workbench window configurer.
	 *
	 * @param configurer the workbench window configurer
	 * @return a new workbench window advisor
	 */
	public WorkbenchWindowAdvisor createWorkbenchWindowAdvisor(
			IWorkbenchWindowConfigurer configurer) {
		return new ApplicationWorkbenchWindowAdvisor(configurer);
	}

	/**
	 * Returns the id of the perspective to use for the initial workbench window
	 *
	 * @return the id of the perspective for the initial window, or
	 *         <code>null</code> if no initial perspective should be shown
	 */
	public String getInitialWindowPerspectiveId() {
		return Perspective.ID;
	}

	/**
	 * Performs arbitrary initialization before the workbench starts running.
	 *
	 * @param configurer an object for configuring the workbench
	 */
	public void initialize(IWorkbenchConfigurer configurer) {
		super.initialize(configurer);
		configurer.setSaveAndRestore(false);
		PlatformUI.getPreferenceStore().setValue(
				IWorkbenchPreferenceConstants.SHOW_TRADITIONAL_STYLE_TABS,
				false);
		PlatformUI.getPreferenceStore().setValue(
				IWorkbenchPreferenceConstants.SHOW_MEMORY_MONITOR, true);

		DataType.setNULLValuesForImport(ImportPreferencePage.getImportNULLValueList());
	}

	/**
	 * Return the contribution comparator for the particular type of
	 * contribution.or it can be a value defined by the user.
	 *
	 * @param contributionType the contribution type
	 * @return the comparator, must not return <code>null</code>
	 * @see IContributionService#getComparatorFor(String)
	 */
	public ContributionComparator getComparatorFor(String contributionType) {
		if (IContributionService.TYPE_PREFERENCE.equals(contributionType)) {
			return new CMPreferenceNodeComparator();
		}
		return super.getComparatorFor(contributionType);
	}

	@Override
	public void postStartup() {
		super.postStartup();
		/*Remove the help content in preference*/
	    PreferenceManager pm = PlatformUI.getWorkbench().getPreferenceManager( );
	    pm.remove( "org.eclipse.help.ui.browsersPreferencePage");
	}
}
