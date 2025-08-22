package com.cubrid.common.core.util;

import java.util.Collection;

public class ArrayUtil {
    private static final String[] EMPTY_STRING_ARRAY = new String[0];

    @SuppressWarnings("rawtypes")
    public static final boolean isEmpty(Collection collection) {
        return collection == null || collection.isEmpty();
    }

    /**
     * Transform collection to comma split string.
     *
     * @param <E>
     * @param col
     * @return
     */
    public static <E> String collectionToCSString(Collection<E> col) {
        if (col == null) {
            return null;
        }
        StringBuilder sb = new StringBuilder();
        for (E e : col) {
            sb.append(e).append(",");
        }
        if (sb.length() > 0) {
            sb.delete(sb.length() - 1, sb.length());
        }

        return sb.toString();
    }

    public static String[] getEmptyStringArray() {
        return EMPTY_STRING_ARRAY;
    }
}
