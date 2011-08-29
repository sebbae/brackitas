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

import java.util.EnumSet;
import java.util.Random;

import javax.servlet.DispatcherType;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

import org.brackit.as.http.appOld.AppController;
import org.brackit.as.http.appOld.AppDispatcher;
import org.brackit.as.http.appOld.AppError;
import org.brackit.as.http.appOld.AppView;
import org.brackit.as.http.rpcOld.DBServlet;
import org.brackit.as.http.rpcOld.ProcedureServlet;
import org.brackit.as.http.rpcOld.XQueryServlet;
import org.brackit.as.http.uiOld.DispatcherServlet;
import org.brackit.as.http.uiOld.ErrorServlet;
import org.brackit.as.http.uiOld.FileDownloadServlet;
import org.brackit.as.http.uiOld.FormDownloadServlet;
import org.brackit.as.http.uiOld.FrontController;
import org.brackit.as.http.uiOld.LoginServlet;
import org.brackit.as.http.uiOld.QueryServlet;
import org.brackit.as.http.uiOld.UploadServlet;
import org.brackit.server.metadata.manager.MetaDataMgr;
import org.brackit.server.session.Session;
import org.brackit.server.session.SessionMgr;
import org.brackit.xquery.util.log.Logger;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.session.HashSessionIdManager;
import org.eclipse.jetty.servlet.FilterHolder;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.util.log.Log;

/**
 * 
 * @author Sebastian Baechle
 * 
 */
public class HttpConnectorOld {
	private static final Logger log = Logger.getLogger(HttpConnectorOld.class);

	public static final String LOGIN_PREFIX = "/rpc/login";
	public static final String GET_PREFIX = "/rpc/db/*";
	public static final String POST_PREFIX = "/rpc/db/*";
	public static final String DELETE_PREFIX = "/rpc/db/*";
	public static final String XQUERY_PREFIX = "/rpc/xquery";
	// Can have parameters attached
	public static final String XQUERY_PREFIX2 = "/rpc/xquery/*";
	public static final String PROC_PREFIX = "/rpc/execute/*";
	public static final String UI_PROCEDURE_PREFIX = "/ui/procedure/*";
	public static final String UI_FILE_DOWNLOAD_PREFIX = "/ui/fileDownload/*";
	public static final String UI_FORM_DOWNLOAD_PREFIX = "/ui/formDownload/*";
	public static final String UI_QUERY_PREFIX = "/ui/query/*";
	public static final String UI_UPLOAD_PREFIX = "/ui/upload/*";
	public static final String UI_DISPATCHER_PREFIX = "/ui/dispatcher/*";
	public static final String UI_ERROR_PREFIX = "/ui/error/*";
	public static final String UI_PREFIX = "/ui/*";
	// Path specifications for eCommerce platform
	public static final String APP_VIEW_PREFIX = "/app/view/*";
	public static final String APP_ERROR_PREFIX = "/app/error/*";
	public static final String APP_DISPATCHER_PREFIX = "/app/dispatcher/*";
	public static final String APP_CONTROLLER_PREFIX = "/app/*";

	private final Server server;
	
	private static class SessionEndListener implements HttpSessionListener {

		private final SessionMgr sessionMgr;
		
		SessionEndListener(SessionMgr sessionMgr) {
			this.sessionMgr = sessionMgr;
		}
		
		@Override
		public void sessionDestroyed(HttpSessionEvent event) {
			HttpSession httpSession = (HttpSession) event.getSession();
			Session session = (Session) httpSession.getAttribute(TXServlet.SESSION);
			if (session != null) {
				sessionMgr.logout(session.getSessionID());
			}
		}

		@Override
		public void sessionCreated(HttpSessionEvent arg0) {
		}
	}

	public HttpConnectorOld(final MetaDataMgr metaDataMgr,
			final SessionMgr sessionMgr, final int port) {
		// encapsulate jetty logging to our log4j
		Log.setLog(new JettyLogger());
		this.server = new Server(port);
		// hard init to speed up startup time
		server.setSessionIdManager(new HashSessionIdManager(new Random()));
		ServletContextHandler servletContextHandler = new ServletContextHandler(
				server, "/", true, false);
		servletContextHandler.setAttribute(MetaDataMgr.class.getName(),
				metaDataMgr);
		servletContextHandler.setAttribute(SessionMgr.class.getName(),
				sessionMgr);
		servletContextHandler.addEventListener(new SessionEndListener(sessionMgr));

		servletContextHandler.addServlet(LoginServlet.class, LOGIN_PREFIX);
		servletContextHandler.addServlet(DBServlet.class, GET_PREFIX);
		servletContextHandler.addServlet(XQueryServlet.class, XQUERY_PREFIX);
		servletContextHandler.addServlet(XQueryServlet.class, XQUERY_PREFIX2);
		servletContextHandler.addServlet(ProcedureServlet.class, PROC_PREFIX);

		EnumSet<DispatcherType> s = EnumSet.allOf(DispatcherType.class);
		servletContextHandler.addFilter(new FilterHolder(
				new org.brackit.as.http.uiOld.LoginFilter()), "/ui/*", s);
		servletContextHandler.addServlet(
				org.brackit.as.http.uiOld.ProcedureServlet.class,
				UI_PROCEDURE_PREFIX);
		servletContextHandler.addServlet(FileDownloadServlet.class,
				UI_FILE_DOWNLOAD_PREFIX);
		servletContextHandler.addServlet(FormDownloadServlet.class,
				UI_FORM_DOWNLOAD_PREFIX);
		servletContextHandler.addServlet(QueryServlet.class, UI_QUERY_PREFIX);
		servletContextHandler.addServlet(UploadServlet.class, UI_UPLOAD_PREFIX);
		servletContextHandler.addServlet(DispatcherServlet.class,
				UI_DISPATCHER_PREFIX);
		servletContextHandler.addServlet(ErrorServlet.class, UI_ERROR_PREFIX);
		servletContextHandler.addServlet(FrontController.class, UI_PREFIX);

		// applications platform servlets
		servletContextHandler.addServlet(AppDispatcher.class,
				APP_DISPATCHER_PREFIX);
		servletContextHandler.addServlet(AppController.class,
				APP_CONTROLLER_PREFIX);
		servletContextHandler.addServlet(AppError.class, APP_ERROR_PREFIX);

		/*
		 * Application testing servlets TODO: Erase them after testing
		 */
		servletContextHandler.addServlet(AppView.class, APP_VIEW_PREFIX);

		servletContextHandler.addEventListener(new HttpSessionListener() {
			@Override
			public void sessionDestroyed(HttpSessionEvent event) {
				Session session = (Session) event.getSession().getAttribute(
						"session");
				if (session != null) {
					sessionMgr.logout(session.getSessionID());
				}
			}

			@Override
			public void sessionCreated(HttpSessionEvent arg0) {
			}
		});
		
		server.setHandler(servletContextHandler);
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
}
