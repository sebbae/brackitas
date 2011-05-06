/*
 * [New BSD License]
 * Copyright (c) 2011, Brackit Project Team <info@brackit.org>  
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *     * Redistributions of source code must retain the above copyright
 *       notice, this list of conditions and the following disclaimer.
 *     * Redistributions in binary form must reproduce the above copyright
 *       notice, this list of conditions and the following disclaimer in the
 *       documentation and/or other materials provided with the distribution.
 *     * Neither the name of the <organization> nor the
 *       names of its contributors may be used to endorse or promote products
 *       derived from this software without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL <COPYRIGHT HOLDER> BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package org.brackit.as.http;

import org.apache.log4j.Level;
import org.eclipse.jetty.util.log.Logger;

/**
 * Simple l4j wrapper to encapsulate jetty internal logging.
 * 
 * @author Sebastian Baechle
 * 
 */
public class Log4jLogger implements Logger {
	private final org.apache.log4j.Logger l4j;

	Log4jLogger() {
		l4j = org.apache.log4j.Logger.getRootLogger();
		// System.out.println(String.format("Creating %s(%s)",
		// Log4jLogger.class.getSimpleName(), l4j.getName()));
	}

	private Log4jLogger(org.apache.log4j.Logger l4j) {
		super();
		this.l4j = l4j;
		// System.out.println(String.format("Creating %s(%s)",
		// Log4jLogger.class.getSimpleName(), l4j.getName()));
	}

	@Override
	public void debug(String s, Throwable throwable) {
		l4j.debug(s, throwable);
	}

	@Override
	public Logger getLogger(String s) {
		return new Log4jLogger(org.apache.log4j.Logger.getLogger(s));
	}

	@Override
	public String getName() {
		return l4j.getName();
	}

	@Override
	public boolean isDebugEnabled() {
		return l4j.isDebugEnabled();
	}

	@Override
	public void setDebugEnabled(boolean flag) {
		if (flag) {
			l4j.setLevel(Level.DEBUG);
		} else {
			l4j.setLevel(Level.INFO);
		}
	}

	@Override
	public void warn(String s, Throwable throwable) {
		l4j.warn(s, throwable);
	}

	@Override
	public void debug(Throwable arg0) {
		l4j.debug(null, arg0);
	}

	@Override
	public void debug(String arg0, Object... arg1) {
		l4j.debug(String.format(arg0, arg1));
	}

	@Override
	public void ignore(Throwable arg0) {
		l4j.debug(null, arg0);
	}

	@Override
	public void info(Throwable arg0) {
		l4j.info(null, arg0);
	}

	@Override
	public void info(String arg0, Object... arg1) {
		l4j.info(String.format(arg0, arg1));
	}

	@Override
	public void info(String arg0, Throwable arg1) {
		l4j.info(arg0, arg1);
	}

	@Override
	public void warn(Throwable arg0) {
		l4j.debug(null, arg0);
	}

	@Override
	public void warn(String arg0, Object... arg1) {
		l4j.warn(String.format(arg0, arg1));
	}
}
