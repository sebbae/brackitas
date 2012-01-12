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
 * DISCLAIMED. IN NO EVENT SHALL <COPYRIGHT HOLDER> BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package org.brackit.as.http.app;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.StreamCorruptedException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.brackit.as.context.BaseAppContext;
import org.brackit.as.context.InputStreamName;
import org.brackit.as.xquery.ASQueryContext;
import org.brackit.as.xquery.ASXQuery;
import org.brackit.server.session.Session;
import org.brackit.server.session.SessionException;
import org.brackit.server.tx.Tx;
import org.brackit.xquery.QueryException;
import org.brackit.xquery.atomic.Atomic;
import org.brackit.xquery.atomic.QNm;
import org.brackit.xquery.atomic.Str;
import org.brackit.xquery.atomic.Una;
import org.brackit.xquery.expr.Cast;
import org.brackit.xquery.expr.Variable;
import org.brackit.xquery.module.StaticContext;
import org.brackit.xquery.xdm.Function;
import org.brackit.xquery.xdm.Item;
import org.brackit.xquery.xdm.Sequence;
import org.brackit.xquery.xdm.Type;
import org.brackit.xquery.xdm.type.AtomicType;
import org.brackit.xquery.xdm.type.SequenceType;

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

	private static class Request {

		private Tx tx;

		private ASXQuery x;

		private String URI;

		private String APP;

		private String RESOURCE;

		private BaseAppContext bac;
	}

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
		Request r = createRequest(req, resp);
		if (!resolveApplication(r)) {
			showError(req, resp, "Unknown application: " + r.APP, null);
			return;
		}
		if (!r.bac.isRunning()) {
			showError(req, resp, String.format("Application terminated: %s",
					r.APP), null);
			return;
		}
		if (!UNKNOWN_MIMETYPE.equals(getMimeType(r.URI))) {
			processResourceRequest(r, resp);
			resp.setStatus(HttpServletResponse.SC_OK);
			return;
		} else {
			prepareExecution(r, req, resp, session);
			if (r.RESOURCE.endsWith(".xq")) {
				PrintWriter writer = new PrintWriter(new OutputStreamWriter(
						resp.getOutputStream(), "utf-8"));

				writer
						.println("<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\">");
				writer.flush();
				processXQueryFileRequest(r, req, resp);
				resp.setStatus(HttpServletResponse.SC_OK);
				return;
			} else {
				PrintWriter writer = new PrintWriter(new OutputStreamWriter(
						resp.getOutputStream(), "utf-8"));
				writer
						.println("<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\">");
				writer.flush();
				processMVCRequest(r, req, resp);
				resp.setStatus(HttpServletResponse.SC_OK);
				return;
			}
		}
	}

	private void processMVCRequest(Request r, HttpServletRequest req,
			HttpServletResponse resp) throws Exception {
		BaseAppContext bac = r.bac;
		String s = String.format("%s.xq", r.URI.substring(0, r.URI
				.lastIndexOf("/")));
		r.x = bac.get(s);
		if (r.x == null) {
			showError(req, resp, String.format("Unknown query: %s", r.URI),
					null);
			return;
		}
		bindExternalVariables(r.x, req);
		StaticContext sctx = r.x.getModule().getStaticContext();
		Map<QNm, Function[]> l = sctx.getFunctions().getDeclaredFunctions();
		Iterator<Function[]> i = l.values().iterator();
		while (i.hasNext()) {
			Function[] f = (Function[]) i.next();
			for (int j = 0; j < f.length; j++) {
				if (f[j].getName().stringValue().equals(r.RESOURCE)) {
					r.x.setPrettyPrint(true);
					PrintWriter writer = new PrintWriter(
							new OutputStreamWriter(resp.getOutputStream(),
									"utf-8"));
					r.x.serializeResult(ctx, writer, f[j].execute(sctx, ctx,
							new Sequence[] {}));
					return;
				}
			}
		}
		showError(req, resp, String.format("Unknown function: %s", r.RESOURCE),
				null);
		return;
	}

	private void processXQueryFileRequest(Request r, HttpServletRequest req,
			HttpServletResponse resp) throws Exception, QueryException,
			IOException {
		r.x = r.bac.get(r.URI);
		if (r.x == null) {
			showError(req, resp,
					String.format("Unknown file query: %s", r.URI), null);
			return;
		}
		bindExternalVariables(r.x, req);
		r.x.setPrettyPrint(true);
		PrintWriter writer = new PrintWriter(new OutputStreamWriter(resp
				.getOutputStream(), "utf-8"));
		r.x.serialize(ctx, writer);
	}

	private boolean resolveApplication(Request r) throws Exception {
		Object o = getServletContext().getAttribute(r.APP);
		if (o == null) {
			return false;
		}
		r.bac = (BaseAppContext) o;
		return true;
	}

	private void bindExternalVariables(ASXQuery x, HttpServletRequest req)
			throws QueryException {
		StaticContext sctx = x.getModule().getStaticContext();
		for (Variable var : x.getModule().getVariables().getDeclaredVariables()) {
			SequenceType type = var.getType();
			// TODO: Correct external binding for non atomic values
			if ((type != null) && (var.getType().getItemType().isAtomic())) {
				Type expectedAtomicType = ((AtomicType) var.getType()
						.getItemType()).getType();
				String param = req.getParameter(var.getName().getLocalName());
				if ((param != null) && (!(param = param.trim()).isEmpty())) {
					Item item = new Una(param);
					item = Cast.cast(sctx, item, expectedAtomicType, false);
					ctx.bind(var.getName(), item);
				} else {
					Item item = new Una("");
					item = Cast.cast(sctx, item, expectedAtomicType, false);
					ctx.bind(var.getName(), item);
				}
			}
		}
	}

	private Request createRequest(HttpServletRequest req,
			HttpServletResponse resp) {
		Request r = new Request();
		r.URI = req.getRequestURI();
		String[] URIParts = r.URI.split("/");
		r.APP = URIParts[2];
		r.RESOURCE = r.URI.substring(r.URI.lastIndexOf("/") + 1);
		req.getSession().setAttribute(FrontController.APP_SESSION_ATT,
				(Atomic) new Str(r.APP));
		return r;
	}

	private void processResourceRequest(Request r, HttpServletResponse resp)
			throws IOException {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		String resource = r.URI;
		File f = new File("src/main/resources" + resource);
		FileInputStream in = new FileInputStream(f);
		try {
			resp.setContentType(String.format("%s; charset=UTF-8",
					getMimeType(resource)));

			byte[] buffer = new byte[1024 * 16];
			int bytesRead = 0;
			int size = 0;
			while ((bytesRead = in.read(buffer)) != -1) {
				size += bytesRead;
				out.write(buffer, 0, bytesRead);
			}
			resp.setHeader("Content-Length", String.valueOf(out.size()));
			resp.setBufferSize(1024 * 100);
		} catch (StreamCorruptedException e) {
			throw new StreamCorruptedException(String
					.format("Error while reading inputStream of resource %s.",
							resource));
		} catch (Exception e) {
			throw new FileNotFoundException(String.format(
					"File %s does not exist under the application resources.",
					resource));
		} finally {
			out.writeTo(resp.getOutputStream());
			out.flush();
			resp.getOutputStream().flush();
			out.close();
			in.close();
		}
	}

	private void prepareExecution(Request r, HttpServletRequest req,
			HttpServletResponse resp, Session session) throws SessionException,
			FileUploadException, IOException {
		r.tx = session.getTX();
		ctx = new ASQueryContext(r.tx, metaDataMgr, req.getSession(), req);
		if (ServletFileUpload.isMultipartContent(req))
			ctx.setMultiPartParams(convertMultiPartParams(req));
		resp.setContentType("text/html; charset=UTF-8");
	}

	private HashMap<String, InputStreamName> convertMultiPartParams(
			HttpServletRequest req) throws FileUploadException, IOException {
		HashMap<String, InputStreamName> result = new HashMap<String, InputStreamName>();
		Iterator<FileItem> iter = new ServletFileUpload(
				new DiskFileItemFactory()).parseRequest(req).iterator();
		while (iter.hasNext()) {
			FileItem item = (FileItem) iter.next();
			result.put(item.getFieldName(), new InputStreamName(item
					.getInputStream(), item.getName()));
		}
		return result;
	}
}
