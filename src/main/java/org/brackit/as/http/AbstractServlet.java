/*
 * [New BSD License]
 * Copyright (c) 2011-2012, Brackit Project Team <info@brackit.org>  
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *     * Redistributions of source code must retain the above copyright
 *       notice, this list of conditions and the following disclaimer.
 *     * Redistributions in binary form must reproduce the above copyright
 *       notice, this list of conditions and the following disclaimer in the
 *       documentation and/or other materials provided with the distribution.
 *     * Neither the name of the Brackit Project Team nor the
 *       names of its contributors may be used to endorse or promote products
 *       derived from this software without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package org.brackit.as.http;

import java.io.IOException;

import javax.activation.MimetypesFileTypeMap;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.brackit.server.metadata.manager.MetaDataMgr;
import org.brackit.server.session.SessionMgr;
import org.brackit.xquery.util.log.Logger;

/**
 * 
 * @author Sebastian Baechle
 * @author Henrique Valer
 * 
 */
public abstract class AbstractServlet extends HttpServlet {

	private static final long serialVersionUID = -5535807586200349215L;

	protected static final Logger log = Logger.getLogger(AbstractServlet.class);

	public static MimetypesFileTypeMap mimeMap;

	protected SessionMgr sessionMgr;

	protected MetaDataMgr metaDataMgr;

	@Override
	public void init() throws ServletException {
		ServletContext servletContext = getServletContext();
		sessionMgr = (SessionMgr) servletContext.getAttribute(SessionMgr.class
				.getName());
		metaDataMgr = (MetaDataMgr) servletContext
				.getAttribute(MetaDataMgr.class.getName());
		mimeMap = (MimetypesFileTypeMap) servletContext
				.getAttribute(HttpConnector.APP_MIME_TYPES);
	}

	protected String getMimeType(String filename) {
		return mimeMap.getContentType(filename);
	}

	/**
	 * doDispatch receives the servlet to which the request and response should
	 * be dispatched. Each servlet is then responsible for the dispatched
	 * request.
	 */
	public void doDispatch(HttpServletRequest req, HttpServletResponse resp,
			String target) throws ServletException, IOException {
		RequestDispatcher dispatcher = getServletContext()
				.getRequestDispatcher(target);
		dispatcher.forward(req, resp);
	}
}
