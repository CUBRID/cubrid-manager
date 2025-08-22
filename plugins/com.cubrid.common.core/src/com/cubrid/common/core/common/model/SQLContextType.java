package com.cubrid.common.core.common.model;

/**
 * SQL context type
 *
 * @author pangqiren
 * @version 1.0 - 2012-7-5 created by pangqiren
 */
public enum SQLContextType {
    NONE,
    CREATE_TABLE,
    SELECT,
    INSERT,
    INSERT_VALUES,
    UPDATE,
    CREATE,
    DELETE,
    DROP,
    ALTER
}
