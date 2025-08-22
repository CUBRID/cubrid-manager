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
package com.cubrid.cubridmanager.ui.shard.common;

import com.cubrid.cubridmanager.core.common.model.ServerInfo;
import com.cubrid.cubridmanager.core.common.socket.MessageUtil;
import com.cubrid.cubridmanager.core.shard.model.Shard;
import com.cubrid.cubridmanager.core.shard.model.Shards;
import com.cubrid.cubridmanager.ui.shard.Messages;
import java.util.Map;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;

/**
 * A panel that shows shard parameter info.
 *
 * @author Tobi
 * @version 1.0
 * @date 2013-3-21
 */
public class ShardParameterPanel extends AbstractModulePanel<Shards> {

    private ServerInfo serverInfo;
    private final Shard shard;

    private ShardBrokerPropertiesPanel shardBrokerPropertiesPanel;
    private ShardConnectionPanel shardConnectionPanel;
    private ShardKeysPanel shardKeysPanel;

    public ShardParameterPanel(
            ModifyListener listener, ServerInfo serverInfo, Shards module, Shard shard) {
        super(listener, module);
        this.serverInfo = serverInfo;
        this.shard = shard;
        this.shardBrokerPropertiesPanel =
                new ShardBrokerPropertiesPanel(modifyListener, this.shard, this.serverInfo);
        this.shardConnectionPanel =
                new ShardConnectionPanel(modifyListener, shard.getShardConnectionFile());
        this.shardKeysPanel = new ShardKeysPanel(modifyListener, this.shard);
    }

    /**
     * build panel
     *
     * @param parent the parent composite
     */
    public void build(Composite parent) {
        Composite composite = new Composite(parent, SWT.NONE);
        GridLayout layout = new GridLayout();
        composite.setLayout(layout);
        GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
        gridData.heightHint = 470;
        gridData.widthHint = 650;
        composite.setLayoutData(gridData);

        TabFolder tabFolder = new TabFolder(composite, SWT.NONE);
        tabFolder.setLayoutData(new GridData(GridData.FILL_BOTH));
        tabFolder.setLayout(new GridLayout());

        TabItem item = new TabItem(tabFolder, SWT.NONE);
        item.setText(Messages.shardBroker);
        item.setControl(createBrokerComp(tabFolder));

        item = new TabItem(tabFolder, SWT.NONE);
        item.setText(Messages.shardConnection);
        item.setControl(createConnectionComp(tabFolder));

        item = new TabItem(tabFolder, SWT.NONE);
        item.setText(Messages.shardKey);
        item.setControl(createKeyComp(tabFolder));

        shardKeysPanel.reloadShardIdList();

        load();
        initial();
    }

    private Control createBrokerComp(Composite parent) {
        Composite composite = new Composite(parent, SWT.NONE);
        composite.setLayout(new GridLayout());
        composite.setLayoutData(new GridData(GridData.FILL_BOTH));
        shardBrokerPropertiesPanel.build(composite);
        composite.addListener(
                SWT.Show,
                new Listener() {
                    public void handleEvent(Event event) {
                        shardConnectionPanel.save();
                        shardKeysPanel.save();
                    }
                });
        return composite;
    }

    private Control createConnectionComp(Composite parent) {
        Composite composite = new Composite(parent, SWT.NONE);
        composite.setLayout(new GridLayout());
        composite.setLayoutData(new GridData(GridData.FILL_BOTH));
        shardConnectionPanel.build(composite);
        composite.addListener(
                SWT.Show,
                new Listener() {
                    public void handleEvent(Event event) {
                        shardKeysPanel.save();
                        shardBrokerPropertiesPanel.save();

                        shardConnectionPanel.reloadFileName();
                    }
                });
        return composite;
    }

    private Control createKeyComp(Composite parent) {
        Composite composite = new Composite(parent, SWT.NONE);
        composite.setLayout(new GridLayout());
        composite.setLayoutData(new GridData(GridData.FILL_BOTH));
        shardKeysPanel.build(composite);
        composite.addListener(
                SWT.Show,
                new Listener() {
                    public void handleEvent(Event event) {
                        shardConnectionPanel.save();
                        shardBrokerPropertiesPanel.save();

                        shardKeysPanel.reloadFileName();
                        shardKeysPanel.reloadShardIdList();
                    }
                });
        return composite;
    }

    public void load() {}

    private void initial() {}

    public Map<String, String> valid() {
        Map<String, String> result = this.shardBrokerPropertiesPanel.valid();
        if (!MessageUtil.getResultTag(result)) {
            return result;
        }
        result = this.shardConnectionPanel.valid();
        if (!MessageUtil.getResultTag(result)) {
            return result;
        }
        result = this.shardKeysPanel.valid();
        return result;
    }

    public void save() {
        this.shardConnectionPanel.save();
        this.shardKeysPanel.save();
        this.shardBrokerPropertiesPanel.save();
    }
}
