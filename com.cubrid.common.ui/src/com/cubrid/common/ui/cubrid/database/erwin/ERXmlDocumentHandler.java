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
package com.cubrid.common.ui.cubrid.database.erwin;

import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * ERXmlDocumentHandler Description
 *
 * @author Jason You
 * @version 1.0 - 2012-11-21 created by Jason You
 */
public class ERXmlDocumentHandler {

    private Map<String, SoftReference<Node>> nodeCache = new HashMap<String, SoftReference<Node>>();

    private final Document doc;

    public ERXmlDocumentHandler(Document doc) {
        this.doc = doc;
    }

    private Node handleNodeCache(String nodeType, String id) {
        SoftReference<Node> mainNode = null;
        if (nodeCache.containsKey(id)) {
            mainNode = nodeCache.get(id);
        } else {
            NodeList nodeList = doc.getElementsByTagName(nodeType);
            for (int i = 0; i < nodeList.getLength(); i++) {
                Node node = nodeList.item(i);
                Node idAttr = node.getAttributes().getNamedItem("id");
                if (idAttr != null && id.equals(idAttr.getNodeValue())) {
                    mainNode = new SoftReference<Node>(node);
                    nodeCache.put(id, mainNode);
                    break;
                }
            }
        }
        if (mainNode == null) return null;
        return mainNode.get();
    }

    public String getChildValueByProperty(Node node, String property) {
        if (property.contains(".")) {

            List<String> propertyArray = new ArrayList<String>();
            for (String s : property.split("\\.")) {
                propertyArray.add(s);
            }
            return findNodeValue(node, propertyArray);

        } else {
            Node child = node.getFirstChild();
            while (child != null) {
                if (child.getNodeName().equals(property)) {
                    if (child.getFirstChild().getNodeValue() != null) {
                        return child.getFirstChild().getNodeValue().trim();
                    }
                    return "";
                }
                child = child.getNextSibling();
            }
        }

        return null;
    }

    public String getNodeChildValueById(String nodeType, String id, String property) {
        Node node = handleNodeCache(nodeType, id);
        return getChildValueByProperty(node, property);
    }

    public NodeList getChildNodeList(Node node, String properties) {
        if (properties == null) {
            return null;
        }
        String prop = "";
        if (properties.contains(".")) {
            prop = properties.substring(0, properties.indexOf("."));
            properties = properties.substring(properties.indexOf(".") + 1);
        } else {
            prop = properties;
            properties = "";
        }

        Node child = getChildNodeByName(node, prop);
        if (child == null) return null;

        if (properties.length() > 0) {
            return getChildNodeList(child, properties);
        } else {
            return child.getChildNodes();
        }
    }

    public Node getChildNodeByName(Node node, String name) {
        if (node == null || name == null || name.equals("")) {
            return null;
        }
        Node fnode = node.getFirstChild();
        while (fnode != null) {
            if (fnode.getNodeName().equals(name)) {
                return fnode;
            }
            fnode = fnode.getNextSibling();
        }

        return null;
    }

    private String findNodeValue(Node node, List<String> propertyArray) {
        // TODO Are there no way to implement without the recursive call
        if (propertyArray.size() == 0) {
            return null;
        }

        String propertyString = propertyArray.remove(0);

        Node nodeChild = node.getFirstChild();
        while (nodeChild != null) {
            if (nodeChild.getNodeName().equals(propertyString)) {
                if (propertyArray.size() == 0) {
                    if (nodeChild.getFirstChild() == null) {
                        return "";
                    }

                    if (nodeChild.getFirstChild().getNodeValue() == null) {
                        return "";
                    }

                    return nodeChild.getFirstChild().getNodeValue().trim();
                } else {
                    return findNodeValue(nodeChild, propertyArray);
                }
            }

            nodeChild = nodeChild.getNextSibling();
        }
        return null;
    }

    public Node getNodeById(String tagName, String id) {
        Node node = handleNodeCache(tagName, id);

        return node;
    }

    public void initAttributeCache(NodeList attributes) {
        if (attributes == null) return;

        for (int i = 0; i < attributes.getLength(); i++) {
            Node attr = attributes.item(i);
            String id = attr.getAttributes().getNamedItem("id").getNodeValue().trim();
            nodeCache.put(id, new SoftReference<Node>(attr));
        }
    }
}
