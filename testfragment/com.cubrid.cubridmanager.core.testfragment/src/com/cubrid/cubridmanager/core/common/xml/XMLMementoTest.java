package com.cubrid.cubridmanager.core.common.xml;

import java.io.IOException;
import junit.framework.TestCase;

public class XMLMementoTest extends TestCase {

    public void testXMLMemento() {
        try {
            // save xml
            XMLMemento memento = XMLMemento.createWriteRoot("hosts");
            assertTrue(memento.getChild("host") == null);
            assertTrue(memento.getChildren("host").length == 0);

            IXMLMemento child = memento.createChild("host");
            assertTrue(memento.getChild("host") != null);
            child.putString("id", "localhost");
            child.putString("name", "localhost");
            child.putInteger("port", 8001);
            child.putBoolean("isLocal", true);
            child.putString("address", "192.168.0.1");
            child.putString("user", "admin");
            child.putTextData("127.0.1");
            String xmlString = memento.saveToString();
            assertTrue(xmlString.indexOf("localhost") > 0);
            assertTrue(memento.getContents().length > 0);

            try {
                IXMLMemento loadMemento = XMLMemento.loadMemento(memento.getInputStream());
                IXMLMemento host1 = loadMemento.getChild("host");
                assertEquals("localhost", host1.getString("id"));
                IXMLMemento[] children = loadMemento.getChildren("host");
                for (int i = 0; i < children.length; i++) {
                    String id = children[i].getString("id");
                    String nullStr = children[i].getString("id1");
                    String name = children[i].getString("name");
                    String address = children[i].getString("address");
                    int port = children[i].getInteger("port");
                    Integer nullInt = children[i].getInteger("port1");
                    float floatPort = ((XMLMemento) children[i]).getFloat("port");
                    Float nullFloat = ((XMLMemento) children[i]).getFloat("port1");
                    boolean isLocal = children[i].getBoolean("isLocal");
                    Boolean nullB = children[i].getBoolean("isLocal1");
                    String user = children[i].getString("user");
                    String textData = children[i].getTextData();
                    assertEquals("localhost", id);
                    assertEquals("localhost", name);
                    assertEquals("8001", port + "");
                    assertTrue(floatPort > 0);
                    assertEquals("192.168.0.1", address);
                    assertTrue(isLocal);
                    assertEquals("127.0.1", textData);
                    assertEquals("admin", user);
                    assertTrue(nullFloat == null);
                    assertTrue(nullStr == null);
                    assertTrue(nullInt == null);
                    assertFalse(nullB);
                    assertTrue(nullFloat == null);
                    assertTrue(children[i].getAttributeNames().size() > 0);

                    /*Errors*/
                    assertNull(((XMLMemento) children[i]).getFloat("name"));
                    assertNull(((XMLMemento) children[i]).getInteger("name"));
                    assertFalse(((XMLMemento) children[i]).getBoolean("name"));
                    assertNull(((XMLMemento) children[i]).getChild("name"));
                    ((XMLMemento) children[i]).putString(null, null);
                    ((XMLMemento) children[i]).putTextData("Text data");
                }
                ((XMLMemento) loadMemento).saveToFile("testxml.xml");
                /*Errors*/
                ((XMLMemento) loadMemento).saveToFile("");
            } catch (Exception e) {
            }
        } catch (Exception e) {
        }
    }

    public void testLoadMemento() {
        try {
            IXMLMemento loadMemento = XMLMemento.loadMemento("testxml.xml");
            /*Errors*/
            loadMemento = XMLMemento.loadMemento("");
        } catch (IOException e) {
        }
    }
}
