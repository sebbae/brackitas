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
package org.brackit.as.http.app;

import java.io.BufferedOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.io.StreamCorruptedException;
import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.brackit.as.context.BaseAppContext;
import org.brackit.as.xquery.ASXQuery;
import org.brackit.as.xquery.ASQueryContext;
import org.brackit.server.session.Session;
import org.brackit.server.session.SessionException;
import org.brackit.server.tx.Tx;
import org.brackit.xquery.QueryException;
import org.brackit.xquery.atomic.Atomic;
import org.brackit.xquery.atomic.Str;
import org.brackit.xquery.atomic.Una;
import org.brackit.xquery.expr.Cast;
import org.brackit.xquery.expr.ExtVariable;
import org.brackit.xquery.function.Function;
import org.brackit.xquery.sequence.type.AtomicType;
import org.brackit.xquery.sequence.type.SequenceType;
import org.brackit.xquery.xdm.Item;
import org.brackit.xquery.xdm.Sequence;
import org.brackit.xquery.xdm.Type;

/**
 * 
 * @author Henrique Valer
 * 
 */
public class FrontController extends BaseServlet {

	private static final long serialVersionUID = 4156829801096332265L;

	public static final String APP_SESSION_ATT = "appName";

	public static final String PAGE_SESSION_ATT = "pageName";

	public static final String HTTP_URI_REQ = "httpUriReq";

	public static final String HTTP_RESOURCE_NAME = "httpResourceName";

	public static final String UNKNOWN_MIMETYPE = "application/octet-stream";

	private static String URI;

	private String APP;

	private String RESOURCE;

	private Tx tx;

	private ASXQuery x;

	private ASQueryContext ctx;

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp,
			Session session) throws Exception {
		process(req, resp, session);
	};

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp,
			Session session) throws Exception {
		process(req, resp, session);
	}

	private void process(HttpServletRequest req, HttpServletResponse resp,
			Session session) throws StreamCorruptedException,
			FileNotFoundException, SessionException, Exception, QueryException,
			IOException {
		resolveApplication(req, resp);
		if (!UNKNOWN_MIMETYPE.equals(getMimeType(URI))) {
			processResourceRequest(APP, URI, resp);
			return;
		} else {
			prepareExecution(req, session);
			if (RESOURCE.endsWith(".xq")) {
				processXQueryFileRequest(req, resp);
				return;
			} else {
				processMVCRequest(req, resp);
				return;
			}
		}
	}

	private void processMVCRequest(HttpServletRequest req,
			HttpServletResponse resp) throws Exception {
		Object o = getServletContext().getAttribute(APP);
		BaseAppContext bac;
		if (o != null) {
			bac = (BaseAppContext) o;
			String s = String.format("%s.xq", URI.substring(0, URI
					.lastIndexOf("/")));
			x = bac.get(s);
			bindExternalVariables(req);
			List<Function[]> l = x.getModule().getFunctions()
					.getDeclaredFunctions();
			Iterator<Function[]> i = l.iterator();
			while (i.hasNext()) {
				Function[] f = (Function[]) i.next();
				for (int j = 0; j < f.length; j++) {
					if (f[j].getName().stringValue().equals(RESOURCE)) {
						x.setPrettyPrint(true);
						x.serializeSequence(ctx, new PrintStream(resp
								.getOutputStream()), f[j].execute(ctx,
								new Sequence[] {}));
						return;
					}
				}
			}
		} else {
			throw new Exception(String.format("Application %s does not exist",
					APP));
		}
	}

	private void processXQueryFileRequest(HttpServletRequest req,
			HttpServletResponse resp) throws Exception, QueryException,
			IOException {
		Object o = getServletContext().getAttribute(APP);
		BaseAppContext bac;
		if (o != null) {
			bac = (BaseAppContext) o;
			x = bac.get(URI);
		} else {
			throw new Exception(String.format("Application %s does not exist",
					APP));
		}
		bindExternalVariables(req);
		x.setPrettyPrint(true);
		x.serialize(ctx, new PrintStream(resp.getOutputStream()));
	}

	private void bindExternalVariables(HttpServletRequest req)
			throws QueryException {
		for (ExtVariable var : x.getModule().getVariables()
				.getDeclaredVariables()) {
			SequenceType type = var.getType();
			// TODO: Correct external binding for non atomic values
			if ((type != null) && (var.getType().getItemType().isAtomic())) {
				Type expectedAtomicType = ((AtomicType) var.getType()
						.getItemType()).type;
				String param = req.getParameter(var.getName().getLocalName());
				if ((param != null) && (!(param = param.trim()).isEmpty())) {
					Item item = new Una(param);
					item = Cast.cast(item, expectedAtomicType, false);
					ctx.bind(var.getName(), item);
				} else {
					Item item = new Una("");
					item = Cast.cast(item, expectedAtomicType, false);
					ctx.bind(var.getName(), item);
				}
			}
		}
	}

	private void prepareExecution(HttpServletRequest req, Session session)
			throws SessionException {
		tx = session.getTX();
		x = null;
		ctx = new ASQueryContext(tx, metaDataMgr, req.getSession(),req);
	}

	private void resolveApplication(HttpServletRequest req,
			HttpServletResponse resp) {
		resp.setContentType("application/xhtml+xml; charset=UTF-8");
		URI = req.getRequestURI();
		String[] URIParts = URI.split("/");
		APP = URIParts[2];
		RESOURCE = URI.substring(URI.lastIndexOf("/") + 1);
		req.getSession().setAttribute(APP_SESSION_ATT, (Atomic) new Str(APP));
	}

	private void processResourceRequest(String app, String resource,
			HttpServletResponse resp) throws StreamCorruptedException,
			FileNotFoundException {
		try {
			String contentType = getMimeType(resource);
			resp.setContentType(contentType);
			InputStream in = getClass().getResourceAsStream(resource);
			BufferedOutputStream out = new BufferedOutputStream(resp
					.getOutputStream());
			try {
				byte[] buffer = new byte[4096];
				int bytesRead = 0;
				while ((bytesRead = in.read(buffer)) != -1)
					out.write(buffer, 0, bytesRead);
			} catch (Exception e) {
				throw new StreamCorruptedException();
			} finally {
				out.close();
				in.close();
				resp.getOutputStream().flush();
				resp.setStatus(HttpServletResponse.SC_OK);
			}
		} catch (StreamCorruptedException e) {
			throw new StreamCorruptedException(String
					.format("Error while reading inputStream of resource %s.",
							resource));
		} catch (Exception e) {
			throw new FileNotFoundException(String.format(
					"File %s does not exist under the application resources.",
					resource));
		}
	}
}
