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
package com.cubrid.cubridmanager.core.shard.model;

import java.util.ArrayList;
import java.util.List;
import junit.framework.TestCase;

/**
 * ShardKeysTest Description
 *
 * @author Kevin.Wang
 * @version 1.0 - 2013-6-7 created by Kevin.Wang
 */
public class ShardKeysTest extends TestCase {

    public void testShardKeys() {
        ShardKeys shardKeys = new ShardKeys();
        Shard shard = new Shard();

        shardKeys.setShard(shard);
        shardKeys.setFieName("shard.conf");

        List<ShardKey> shardKeyList = new ArrayList<ShardKey>();

        ShardKey shardKey = new ShardKey();
        shardKey.setName("shardKey");
        List<String> sectionList = new ArrayList<String>();
        sectionList.add("s1");
        shardKey.setSections(sectionList);
        shardKey.addSection("s2");

        assertNotNull(shardKey.getName());
        assertNotNull(shardKey.getSections());
        assertNotNull(shardKey.toString());

        shardKeyList.add(shardKey);
        shardKeys.setKeys(shardKeyList);
        shardKeys.removeKey(shardKey);
        shardKeys.addKey(shardKey);
        shardKeys.removeKey(shardKey.getName());
        assertNull(shardKeys.getKey(shardKey.getName()));

        shardKeys.addKey(shardKey);

        assertNotNull(shardKeys.getFieName());
        assertNotNull(shardKeys.getFileName());
        assertNotNull(shardKeys.getKeys());
        assertNotNull(shardKeys.getKey("shardKey"));
        assertNotNull(shardKeys.toString());

        String[] content = new String[3];

        content[0] = "";
        content[1] = "key=1";
        content[2] = "a+b =c";
        shardKeys.parse(content);
    }
}
