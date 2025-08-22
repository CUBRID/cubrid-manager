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
package com.cubrid.common.ui.cubrid.database.erwin.export;

import static com.cubrid.common.core.util.NoOp.noOp;

import com.cubrid.common.core.common.model.SchemaInfo;
import com.cubrid.common.core.util.LogUtil;
import java.io.File;
import java.util.Map;
import java.util.UUID;
import org.slf4j.Logger;

/**
 * ERXmlEntityFactory Description
 *
 * @author Jason You
 * @version 1.0 - 2012-12-5 created by Jason You
 */
public class ERXmlDocumentUtils {
    private static final Logger LOGGER = LogUtil.getLogger(ERXmlDocumentUtils.class);

    public ERXmlDocumentUtils(SchemaInfo info) {
        createDocument();
    }

    private void createDocument() {
        noOp();
    }

    public static String getUUID() {
        return UUID.randomUUID().toString();
    }

    @SuppressWarnings("rawtypes")
    public Object createNode(Class clz) {
        Object obj = null;
        try {
            obj = clz.newInstance();
        } catch (InstantiationException e) {
            LOGGER.error(e.getMessage(), e);
        } catch (IllegalAccessException e) {
            LOGGER.error(e.getMessage(), e);
        }

        return obj;
    }

    public void createEntity(String entity) {
        noOp();
    }

    public void appendTo(String entity, String child) {
        noOp();
    }

    public void addAfter(String entity, String next) {
        noOp();
    }

    public void addAttribute(String entity, Map<String, String> attr) {
        noOp();
    }

    public void addAttribute(String entity, String attrName, String attrValue) {
        noOp();
    }

    public File exportToFile() {
        return null;
    }

    public enum NodeType {
        NODE_ENTITY("Entity"),
        NODE_ATTRIBUTE("Attribute"),
        NODE_ENTITYPROPS("EntityProps"),
        NODE_TYPE("Type"),
        NODE_ATTRIBUTEGROUPS("Attribute_Groups"),
        NODE_ATTRIBUTEPROPS("AttributeProps"),
        NODE_DATATYPE("Datatype"),
        NODE_NULLOPTION("Null_Option"),
        NODE_ORDER("Order"),
        NODE_PARENTDOMAIN("Parent_domain"),
        NODE_KEYGROUPGROUPS("Key_Group_Groups"),
        NODE_KEYGROUP("Key_Group"),
        NODE_KEYGROUPPROPS("Key_GroupProps"),
        KEYGROUPTYPE("Key_Group_Type"),
        INDEXGENERATE("Index_Generate"),
        INDEXCLUSTERED("Index_Clustered"),
        KEYGROUPMEMBERGROUPS("Key_Group_Member_Groups"),
        KEYGROUPMEMBER("Key_Group_Member"),
        KEYGROUPMEMBERPROPS("Key_Group_MemberProps"),
        KEYGROUPMEMBERCOLUMN("Key_Group_Member_Column"),
        KEYGROUPSORTORDER("Key_Group_Sort_Order"),
        KEYGROUPPOSITION("Key_Group_Position"),
        RELATIONSHIPGROUPS("Relationship_Groups"),
        RELATIONSHIP("Relationship"),
        RELATIONSHIPPROPS("RelationshipProps"),
        NAME("Name"),
        RELATIONSHIPNONULLS("Relationship_No_Nulls"),
        RELATIONSHIPSEQUENCE("Relationship_Sequence"),
        RELATIONSHIPPARENTDELETERULE("Relationship_Parent_Delete_Rule"),
        RELATIONSHIPPARENTINSERTRULE("Relationship_Parent_Insert_Rule"),
        RELATIONSHIPPARENTUPDATERULE("Relationship_Parent_Update_Rule"),
        RELATIONSHIPCHILDDELETERULE("Relationship_Child_Delete_Rule"),
        RELATIONSHIPCHILDINSERTRULE("Relationship_Child_Insert_Rule"),
        RELATIONSHIPCHILDUPDATERULE("Relationship_Child_Update_Rule"),
        RELATIONSHIPPARENTENTITY("Relationship_Parent_Entity"),
        RELATIONSHIPCHILDENTITY("Relationship_Child_Entity"),
        DEFAULTVALUEGROUPS("Default_Value_groups"),
        DEFAULTVALUE("Default_Value"),
        DEFAULTVALUEPROPS("Default_ValueProps"),
        USAGECOUNT("Usage_Count");

        private String value;

        private NodeType(String value) {
            this.value = value;
        }

        public String getText() {
            return this.value;
        }
    }
}
