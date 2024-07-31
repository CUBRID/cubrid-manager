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
package com.cubrid.cubridmanager.core.shard.model;

import com.cubrid.common.core.util.StringUtil;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import junit.framework.TestCase;

/**
 * Test shards models
 *
 * @author Tobi
 * @version 1.0
 * @date 2012-12-21
 */
public class ShardModelTest extends TestCase {

    private static final List<Class<?>> modelList = new ArrayList<Class<?>>();

    static {
        modelList.add(Shards.class);
        modelList.add(Shard.class);
        modelList.add(ShardConnection.class);
        modelList.add(ShardKeys.class);
        modelList.add(ShardKey.class);
    }

    public void testPublicMethod() {
        // Initialize
        Shards shards = new Shards();
        Shard shard = new Shard();
        ShardConnection shardConnection = new ShardConnection();
        ShardKeys shardKeys = new ShardKeys();
        ShardKey shardKey = new ShardKey();

        // integrated
        shards.addShard(shard);
        shard.setShardConnectionFile(shardConnection);
        shard.setShardKeysFile(shardKeys);
        shardKeys.addKey(shardKey);

        // set value
        shards.setProperties(new HashMap<String, String>());
        shards.setRunning(true);
        shards.setValue("shards", "shardsTest");

        shard.setName("shardTest");
        shard.setProperties(new HashMap<String, String>());
        shard.setRunning(false);
        shard.setValue("shard", "shardTest");

        shardConnection.setFieName("connectionTest.txt");
        shardConnection.setConnections(new ArrayList<String>());
        shardConnection.addConnection("0    shard_db     localhost");

        shardKeys.setFieName("keysTest.txt");
        List<ShardKey> keys = new ArrayList<ShardKey>();
        keys.add(shardKey);
        shardKeys.setKeys(keys);

        shardKey.setName("keyTest");
        shardKey.setSections(new ArrayList<String>());
        shardKey.addSection("0    255    0");

        // assert get value
        assertEquals(shardKey.getName(), "keyTest");
        assertTrue(shardKey.getSections().contains("0    255    0"));

        assertEquals(shardKeys.getFieName(), "keysTest.txt");
        assertEquals(shardKeys.getKey("keyTest"), shardKey);
        assertTrue(shardKeys.getKeys().contains(shardKey));

        assertEquals(shardConnection.getFieName(), "connectionTest.txt");
        assertTrue(shardConnection.getConnections().contains("0    shard_db     localhost"));

        assertEquals(shard.getName(), "shardTest");
        assertFalse(shard.isRunning());
        assertEquals(shard.getShardConnectionFile(), shardConnection);
        assertEquals(shard.getShardKeysFile(), shardKeys);
        assertEquals(shard.getValue("shard"), "shardTest");
        assertTrue(shard.getProperties().containsKey("shard"));
        assertTrue(shard.getProperties().containsValue("shardTest"));

        assertTrue(shards.isRunning());
        assertTrue(shards.getShardList().contains(shard));
        assertEquals(shards.getValue("shards"), "shardsTest");
        assertTrue(shards.getProperties().containsKey("shards"));
        assertTrue(shards.getProperties().containsValue("shardsTest"));
        assertTrue(shards.toGeneralString().contains("Copyright (C) 2008"));
        assertTrue(shards.toString().contains("Copyright (C) 2008"));

        shards.removeShard(shard);
        shard = new Shard();
        shard.setName("shard");
        shards.addShard(shard);
        assertNotNull(shards.getShard("shard"));
        assertFalse(shards.checkShardNameConflicts(shard, "shard"));

        shards.removeShard("shard");

        shards.checkShmIdConflicts(null, "");
        shards.checkShmIdConflicts(shard, "");
        shards.checkPortConflicts(shards, "255");
        assertNotNull(shards.getFileName());

        String line = shards.toGeneralString();
        shards.parse(line.split(StringUtil.NEWLINE));
    }
}
