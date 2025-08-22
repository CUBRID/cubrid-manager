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
package com.cubrid.cubridmanager.core.cubrid.database.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * A java bean that stored info that user send object
 *
 * @author sq
 * @version 1.0 - 2009-12-24 created by sq
 */
public class UserSendObj {

    String dbname;

    String username;

    String userpass;

    List<String> groups;

    List<String> addmembers;

    List<String> removemembers;

    Map<String, String> authorization;

    public String getDbname() {
        return dbname;
    }

    public void setDbname(String dbname) {
        this.dbname = dbname;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUserpass() {
        return userpass;
    }

    public void setUserpass(String userpass) {
        this.userpass = userpass;
    }

    /**
     * Get the list of groups
     *
     * @return List<String> The list of groups
     */
    public List<String> getGroups() {
        if (groups == null) {
            return new ArrayList<String>();
        }
        return groups;
    }

    /**
     * Add a group to the list of groups
     *
     * @param group String
     */
    public void addGroups(String group) {

        if (groups == null) {
            groups = new ArrayList<String>();
        }
        this.groups.add(group);
    }

    /**
     * Get the list of addmembers
     *
     * @return List<String> the list of addmembers
     */
    public List<String> getAddmembers() {
        if (addmembers == null) {
            return new ArrayList<String>();
        }
        return addmembers;
    }

    /**
     * Add member to the list of addmembers
     *
     * @param member String a member
     */
    public void addAddmembers(String member) {
        if (addmembers == null) {
            addmembers = new ArrayList<String>();
        }
        this.addmembers.add(member);
    }

    /**
     * Get the instance of removemembers
     *
     * @return List<String> the instance of removemembers
     */
    public List<String> getRemovemembers() {
        if (removemembers == null) {
            return new ArrayList<String>();
        }
        return removemembers;
    }

    /**
     * Add a member to removemembers
     *
     * @param member String a member
     */
    public void addRemovemembers(String member) {

        if (removemembers == null) {
            removemembers = new ArrayList<String>();
        }
        this.removemembers.add(member);
    }

    /**
     * Get the authorization
     *
     * @return Map<String, String> The map of authorization
     */
    public Map<String, String> getAuthorization() {
        if (authorization == null) {
            return new TreeMap<String, String>();
        }
        return authorization;
    }

    /**
     * Add a pair of key and value to authorization
     *
     * @param key String
     * @param value String
     */
    public void addAuthorization(String key, String value) {
        if (authorization == null) {
            authorization = new TreeMap<String, String>();
        }
        this.authorization.put(key, value);
    }
}
