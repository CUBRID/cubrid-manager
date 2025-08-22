package com.cubrid.common.ui.spi.dialog;

import org.eclipse.jface.dialogs.IPageChangedListener;
import org.eclipse.jface.dialogs.IPageChangingListener;
import org.eclipse.jface.dialogs.PageChangedEvent;
import org.eclipse.jface.dialogs.PageChangingEvent;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.wizard.IWizard;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.widgets.Composite;

/**
 * The CMWizardPage Description
 *
 * @author Kevin.Wang
 * @version 1.0 - Aug 7, 2012 created by Kevin.Wang
 */
public class CMWizardPage extends WizardPage
        implements IPageChangedListener, IPageChangingListener {

    protected CMWizardPage(String pageName) {
        super(pageName);
    }

    public CMWizardPage(String pageName, String title, ImageDescriptor titleImage) {
        super(pageName, title, titleImage);
    }

    /**
     * Retrieves the migration wizard object.
     *
     * @return MigrationWizard
     */
    public IWizard getCMWizard() {
        return getWizard();
    }

    /**
     * Handle migration wizard page changed.
     *
     * @param event PageChangedEvent
     */
    public void pageChanged(PageChangedEvent event) {
        if (event.getSelectedPage() == this) {
            afterShowCurrentPage(event);
        }
    }

    /**
     * Handle migration wizard page changing.
     *
     * @param event PageChangingEvent
     */
    public void handlePageChanging(PageChangingEvent event) {
        if (!event.doit) {
            return;
        }
        if (event.getCurrentPage() == this) {
            handlePageLeaving(event);
        } else if (event.getTargetPage() == this) {
            handlePageShowing(event);
        }
    }

    /**
     * When migration wizard displayed current page.
     *
     * @param event PageChangedEvent
     */
    protected void afterShowCurrentPage(PageChangedEvent event) {
        // Default is doing nothing.
    }

    /**
     * When migration wizard will show next page or previous page.
     *
     * @param event PageChangingEvent
     */
    protected void handlePageLeaving(PageChangingEvent event) {
        // Default is doing nothing.
    }

    /**
     * When migration wizard will show this page.
     *
     * @param event PageChangingEvent
     */
    protected void handlePageShowing(PageChangingEvent event) {
        // Default is doing nothing.
    }

    /**
     * Retrieves that is in go to next page process.
     *
     * @param event the PageChangingEvent that is fired.
     * @return true:go to next page.false:go to previous page.
     */
    protected boolean isGotoNextPage(PageChangingEvent event) {
        return getWizard().getNextPage(this) == event.getTargetPage();
    }

    /**
     * Create the control of wizard page.
     *
     * @param parent Composite of control.
     */
    public void createControl(Composite parent) {
        // Do nothing.
    }
}
