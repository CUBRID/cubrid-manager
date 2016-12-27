package com.cubrid.common.core;

import org.eclipse.osgi.util.NLS;

/**
 * This is message bundle classes and provide convenience methods for manipulating messages.
 * 
 * @author pangqiren 2009-3-2
 * 
 */
public class Messages extends NLS {
	static {
		NLS.initializeMessages(CubridCommonCorePlugin.PLUGIN_ID + ".Messages", Messages.class);
	}

	public static String keywordFilename;
	public static String sqlmapEmptyContent;
	public static String sqlmapInvalidFormat;
	public static String sqlmapNoMybatisFormat;
}