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
package com.cubrid.common.core.util;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.URL;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cubrid.common.core.CubridCommonCorePlugin;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.joran.JoranConfigurator;
import ch.qos.logback.core.joran.spi.JoranException;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;

/**
 * This class is the common logback interface to be convenient to Get Logger.
 *
 * @author pangqiren
 * @version 1.0 - 2009-06-04 created by pangqiren
 * @version 1.1 - 2009-09-06 updated by Isaiah Choe
 * @version 1.2 - 2324-09-09 updated by hwanyseo
 */
public final class LogUtil {

	private final static Logger Log = getLogger(LogUtil.class);

	private LogUtil() {
	}

	/**
	 * re-initialize logger configurations
	 *
	 * @param workspace String
	 * @throws IOException
	 * @throws JoranException
	 */
	public static void configLogger(String workspace) throws JoranException, IOException {
		System.setProperty("LOGBACK_LOG_ROOT_DIR", workspace);

		LoggerContext context = (LoggerContext) LoggerFactory.getILoggerFactory();
		JoranConfigurator jc = new JoranConfigurator();
		jc.setContext(context);
		context.reset();

		URL logbackConfigFileUrl = FileLocator.find(CubridCommonCorePlugin.getDefault().getBundle(),
				new Path("logback.xml"), null);
		jc.doConfigure(logbackConfigFileUrl.openStream());
		//reDirectSystemErrToLog();
	}

	public static Logger getLogger(Class<?> clazz) {
		return LoggerFactory.getLogger(clazz);
	}

	public static void reDirectSystemErrToLog() {
		Log.error("reDirectSystemErrToLog");
		System.setErr(new PrintStream(new OutputStream() {

			@Override
			public void write(int b) throws IOException {

			}

			@Override
			public void write(byte[] b, int off, int len) throws IOException {
				String msg = new String(b, off, len).trim();
				Log.error(msg);
			}
		}));
	}
}
