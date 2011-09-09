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
import java.io.InputStream;
import java.io.PrintStream;
import java.io.StreamCorruptedException;
import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.brackit.as.context.BaseAppContext;
import org.brackit.as.xquery.ASXQuery;
import org.brackit.as.xquery.HttpSessionTXQueryContext;
import org.brackit.server.session.Session;
import org.brackit.server.tx.Tx;
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

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp,
			Session session) throws Exception {

		resp.setContentType("application/xhtml+xml; charset=UTF-8");

		// // TODO:
		// try {
		//			
		// Resolve Application
		String URI = req.getRequestURI();
		String[] URIParts = URI.split("/");
		String app = URIParts[2];
		String resource = URI.substring(URI.lastIndexOf("/") + 1);

		// TODO: Improve: Where is the best place for such objects?
		// On the AppCtx object!!!
		req.getSession().setAttribute(APP_SESSION_ATT, (Atomic) new Str(app));

		// Resolve resource
		if (!UNKNOWN_MIMETYPE.equals(getMimeType(URI))) {
			processResource(app, URI, resp);
			return;
		}

		// Compilation and execution
		Tx tx = session.getTX();
		ASXQuery x = null;
		HttpSessionTXQueryContext ctx = new HttpSessionTXQueryContext(tx,
				metaDataMgr, req.getSession());

		// without MVC
		// http://localhost:8080/apps/helloWorld/folder/public/default.xq
		if (resource.endsWith(".xq")) {

			Object o = getServletContext().getAttribute(app);
			BaseAppContext bac;
			if (o != null) {
				bac = (BaseAppContext) o;
				x = bac.get(URI);
			} else {
				throw new Exception(String.format(
						"Application %s does not exist", app));
			}

			// TODO: Bind also for MVC queries
			// bind query external variables
			for (ExtVariable var : x.getModule().getVariables()
					.getDeclaredVariables()) {
				SequenceType type = var.getType();
				if ((type != null) && (var.getType().getItemType().isAtomic())) {
					Type expectedAtomicType = ((AtomicType) var.getType()
							.getItemType()).type;
					String param = req.getParameter(var.getName()
							.getLocalName());
					if ((param != null) && (!(param = param.trim()).isEmpty())) {
						Item item = new Una(param);
						item = Cast.cast(item, expectedAtomicType, false);
						ctx.bind(var.getName(), item);
					}
				}
			}

			x.setPrettyPrint(true);
			x.serialize(ctx, new PrintStream(resp.getOutputStream()));

		} else {
			// Query with MVC
			// http://localhost:8080/apps/helloWorldMVC/controllers/testController/echo
			Object o = getServletContext().getAttribute(app);
			BaseAppContext bac;
			if (o != null) {
				bac = (BaseAppContext) o;
				String s = String.format("%s.xq", URI.substring(0, URI
						.lastIndexOf("/")));
				x = bac.get(s);
				List<Function[]> l = x.getModule().getFunctions()
						.getDeclaredFunctions();
				Iterator<Function[]> i = l.iterator();
				while (i.hasNext()) {
					Function[] f = (Function[]) i.next();
					for (int j = 0; j < f.length; j++) {
						if (f[j].getName().stringValue().equals(resource)) {
							x.serializeSequence(ctx, new PrintStream(resp
									.getOutputStream()), f[j].execute(ctx,
									new Sequence[] {}));
							return;
						}
					}
				}
			}
		}
	}

	private void processResource(String app, String resource,
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
