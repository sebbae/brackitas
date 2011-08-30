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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Random;

import javax.activation.MimetypesFileTypeMap;

import org.brackit.as.http.app.ErrorServlet;
import org.brackit.as.http.app.FrontController;
import org.brackit.as.http.app.ResourceServlet;
import org.brackit.server.metadata.manager.MetaDataMgr;
import org.brackit.server.session.SessionMgr;
import org.brackit.xquery.util.log.Logger;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.session.HashSessionIdManager;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.util.log.Log;

/**
 * 
 * @author Henrique Valer
 * 
 */
public class HttpConnector {

	public static final String APP_MIME_TYPES = "mimeTypes";

	public static final String APP_ERROR_DISP_TARGET = "/app/error/";
	public static final String APP_RESOURCE_DISP_TARGET = "/app/resource/";

	private static final String APP_ERROR_PREFIX = APP_ERROR_DISP_TARGET + "*";
	private static final String APP_RESOURCE_PREFIX = APP_RESOURCE_DISP_TARGET
			+ "*";
	private static final String APP_CONTROLLER_PREFIX = "/app/*";

	private static final Logger log = Logger.getLogger(HttpConnector.class);
	private final Server server;

	public HttpConnector(final MetaDataMgr metaDataMgr,
			final SessionMgr sessionMgr, final int port) {
		Log.setLog(new JettyLogger());
		this.server = new Server(port);
		server.setSessionIdManager(new HashSessionIdManager(new Random()));
		ServletContextHandler servletContextHandler = new ServletContextHandler(
				server, "/", true, false);
		servletContextHandler.setAttribute(MetaDataMgr.class.getName(),
				metaDataMgr);
		servletContextHandler.setAttribute(SessionMgr.class.getName(),
				sessionMgr);
		servletContextHandler
				.setAttribute(APP_MIME_TYPES, this.loadMimeTypes());
		// Load applications
		// Add applications to ServletContext

		servletContextHandler.addServlet(FrontController.class,
				APP_CONTROLLER_PREFIX);
		servletContextHandler.addServlet(ResourceServlet.class,
				APP_RESOURCE_PREFIX);
		servletContextHandler.addServlet(ErrorServlet.class, APP_ERROR_PREFIX);
	}

	public void start() throws Exception {
		try {
			server.start();
			server.join();
		} catch (Exception e) {
			log.error(e);
		}
		Runtime.getRuntime().addShutdownHook(new Thread() {
			@Override
			public void run() {
				try {
					server.stop();
				} catch (Exception e) {
					log.error(e);
				}
			}
		});
	}

	public void stop() throws Exception {
		server.stop();
	}

	private MimetypesFileTypeMap loadMimeTypes() {
		MimetypesFileTypeMap mimeMap = new MimetypesFileTypeMap();
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(
					getClass().getClassLoader().getResourceAsStream(
							"mime.types")));
			String strLine = null;
			while ((strLine = br.readLine()) != null) {
				mimeMap.addMimeTypes(strLine);
			}
			br.close();
		} catch (IOException e) {
			log.error("Could not load mime types", e);
		}
		return mimeMap;
	}
}