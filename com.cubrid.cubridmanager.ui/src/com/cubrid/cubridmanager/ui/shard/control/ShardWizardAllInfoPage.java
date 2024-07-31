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
package com.cubrid.cubridmanager.ui.shard.control;

import com.cubrid.common.ui.spi.dialog.CMWizardPage;
import com.cubrid.cubridmanager.core.shard.model.Shard;
import com.cubrid.cubridmanager.core.shard.model.Shards;
import com.cubrid.cubridmanager.ui.cubrid.database.Messages;
import org.eclipse.jface.dialogs.PageChangedEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Text;

/**
 * Shard wizard all information page.
 *
 * @author Tobi
 * @version 1.0
 * @date 2012-12-10
 */
public class ShardWizardAllInfoPage extends CMWizardPage {

    public static final String PAGENAME = "AddShardWizard/ShardWizardAllInfoPage";
    private final Shards shards;
    private final Shard shard;

    private Text shardGeneralInfoText;
    private Text shardBrokerInfoText;
    private Text shardConnectionInfoText;
    private Text shardKeyInfoText;

    public ShardWizardAllInfoPage(Shards shards, Shard shard) {
        super(PAGENAME);
        this.shards = shards;
        this.shard = shard;
        setPageComplete(true);
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

        Label tipLabel = new Label(composite, SWT.LEFT | SWT.WRAP);
        tipLabel.setText(Messages.msgDbInfoList);
        gridData = new GridData(GridData.FILL_HORIZONTAL);
        tipLabel.setLayoutData(gridData);

        gridData = new GridData(GridData.FILL_BOTH);
        TabFolder tabFolder = new TabFolder(composite, SWT.NONE);
        tabFolder.setLayoutData(gridData);
        tabFolder.setLayout(new GridLayout());

        gridData = new GridData(GridData.FILL_BOTH);
        shardGeneralInfoText = new Text(tabFolder, SWT.LEFT | SWT.WRAP | SWT.BORDER | SWT.V_SCROLL);
        shardGeneralInfoText.setEditable(false);
        shardGeneralInfoText.setLayoutData(gridData);

        gridData = new GridData(GridData.FILL_BOTH);
        shardBrokerInfoText = new Text(tabFolder, SWT.LEFT | SWT.WRAP | SWT.BORDER | SWT.V_SCROLL);
        shardBrokerInfoText.setEditable(false);
        shardBrokerInfoText.setLayoutData(gridData);

        gridData = new GridData(GridData.FILL_BOTH);
        shardConnectionInfoText =
                new Text(tabFolder, SWT.LEFT | SWT.WRAP | SWT.BORDER | SWT.V_SCROLL);
        shardConnectionInfoText.setEditable(false);
        shardConnectionInfoText.setLayoutData(gridData);

        gridData = new GridData(GridData.FILL_BOTH);
        shardKeyInfoText = new Text(tabFolder, SWT.LEFT | SWT.WRAP | SWT.BORDER | SWT.V_SCROLL);
        shardKeyInfoText.setEditable(false);
        shardKeyInfoText.setLayoutData(gridData);

        TabItem item = new TabItem(tabFolder, SWT.NONE);
        item.setText("General");
        item.setControl(shardGeneralInfoText);

        item = new TabItem(tabFolder, SWT.NONE);
        item.setText("Broker");
        item.setControl(shardBrokerInfoText);

        item = new TabItem(tabFolder, SWT.NONE);
        item.setText("Connection");
        item.setControl(shardConnectionInfoText);

        item = new TabItem(tabFolder, SWT.NONE);
        item.setText("Key");
        item.setControl(shardKeyInfoText);

        initialize();
        setTitle("All Information");
        setMessage("Add a new shard broker.");
        setControl(composite);

        initialize(); // TODO why this method called again?
    }

    private void initialize() {
        shardGeneralInfoText.setText(shards.toGeneralString());
        shardBrokerInfoText.setText(shard.toString());
        shardConnectionInfoText.setText(shard.getShardConnectionFile().toString());
        shardKeyInfoText.setText(shard.getShardKeysFile().toString());
    }

    /**
     * When migration wizard displayed current page.
     *
     * @param event PageChangedEvent
     */
    protected void afterShowCurrentPage(PageChangedEvent event) {
        initialize();
    }
}
