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
package com.cubrid.cubridmanager.ui.shard.control;

import com.cubrid.common.ui.spi.dialog.CMWizardPage;
import com.cubrid.cubridmanager.core.common.socket.MessageUtil;
import com.cubrid.cubridmanager.core.shard.model.Shard;
import com.cubrid.cubridmanager.ui.shard.common.ShardKeysPanel;
import java.util.Map;
import org.eclipse.jface.dialogs.PageChangedEvent;
import org.eclipse.jface.dialogs.PageChangingEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;

/**
 * Shard wizard keys information page.
 *
 * @author Tobi
 * @version 1.0
 * @date 2012-12-7
 */
public class ShardWizardKeysPage extends CMWizardPage implements ModifyListener {

    public static final String PAGENAME = "AddShardWizard/ShardWizardKeysPage";
    private final Shard shard;

    private ShardKeysPanel shardKeysPanel;

    public ShardWizardKeysPage(Shard shard) {
        super(PAGENAME);
        this.shard = shard;
        shardKeysPanel = new ShardKeysPanel(this, this.shard);
        setPageComplete(false);
    }

    /**
     * Create the control for this page
     *
     * @param parent the parent composite
     */
    public void createControl(Composite parent) {
        Composite composite = new Composite(parent, SWT.NONE);
        GridLayout layout = new GridLayout();
        layout.marginHeight = 10;
        layout.marginWidth = 10;
        composite.setLayout(layout);
        GridData gridData = new GridData(GridData.FILL_BOTH);
        composite.setLayoutData(gridData);

        shardKeysPanel.build(composite);

        initial();
        setTitle("Keys Information");
        setMessage("Edit the keys infoamation.");

        setControl(composite);
    }

    /**
     * Call this method when modify text
     *
     * @param event the modify event
     */
    public void modifyText(ModifyEvent event) {
        Map<String, String> result = shardKeysPanel.valid();

        if (!MessageUtil.getResultTag(result)) {
            setErrorMessage(MessageUtil.getResultMessage(result));
            setPageComplete(false);
            return;
        }

        setErrorMessage(null);
        setPageComplete(true);
    }

    private void initial() {}

    /**
     * When migration wizard displayed current page.
     *
     * @param event PageChangedEvent
     */
    protected void afterShowCurrentPage(PageChangedEvent event) {
        shardKeysPanel.reloadFileName();
        shardKeysPanel.reloadShardIdList();
        modifyText(null);
    }

    /**
     * When migration wizard will show next page or previous page.
     *
     * @param event PageChangingEvent
     */
    protected void handlePageLeaving(PageChangingEvent event) {
        shardKeysPanel.save();
    }
}
