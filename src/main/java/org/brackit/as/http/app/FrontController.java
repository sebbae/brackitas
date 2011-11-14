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
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
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

	private Tx tx;

	private ASXQuery x;

	private static String URI;

	private String APP;

	private String RESOURCE;

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
			// resp.setStatus(HttpServletResponse.SC_OK);
			return;
		} else {
			prepareExecution(req, resp, session);
			if (RESOURCE.endsWith(".xq")) {
				PrintWriter writer = new PrintWriter(new OutputStreamWriter(
						resp.getOutputStream(), "utf-8"));
				writer
						.println("<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.0 Transitional//EN\" \"http://www.w3.org/TR/html4/loose.dtd\">");
				processXQueryFileRequest(req, resp);
				// resp.setStatus(HttpServletResponse.SC_OK);
				return;
			} else {
				PrintWriter writer = new PrintWriter(new OutputStreamWriter(
						resp.getOutputStream(), "utf-8"));
				writer
						.println("<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.0 Transitional//EN\" \"http://www.w3.org/TR/html4/loose.dtd\">");
				processMVCRequest(req, resp);
				// resp.setStatus(HttpServletResponse.SC_OK);
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
			StaticContext sctx = x.getModule().getStaticContext();
			Map<QNm, Function[]> l = sctx.getFunctions().getDeclaredFunctions();
			Iterator<Function[]> i = l.values().iterator();
			while (i.hasNext()) {
				Function[] f = (Function[]) i.next();
				for (int j = 0; j < f.length; j++) {
					if (f[j].getName().stringValue().equals(RESOURCE)) {
						x.setPrettyPrint(true);
						PrintWriter writer = new PrintWriter(
								new OutputStreamWriter(resp.getOutputStream(),
										"utf-8"));
						x.serializeResult(ctx, /*
												 * new
												 * PrintWriter(resp.getOutputStream
												 * ())
												 */writer, f[j].execute(sctx,
								ctx, new Sequence[] {}));
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
		PrintWriter writer = new PrintWriter(new OutputStreamWriter(resp
				.getOutputStream(), "utf-8"));
		x.serialize(ctx, writer);
	}

	private void bindExternalVariables(HttpServletRequest req)
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

	private void resolveApplication(HttpServletRequest req,
			HttpServletResponse resp) {
		URI = req.getRequestURI();
		String[] URIParts = URI.split("/");
		APP = URIParts[2];
		RESOURCE = URI.substring(URI.lastIndexOf("/") + 1);
		req.getSession().setAttribute(FrontController.APP_SESSION_ATT,
				(Atomic) new Str(APP));
		resp.setHeader("Cache-Control", "no-cache");
		resp.setHeader("Pragma", "no-cache");
		resp.setDateHeader("Expires", 0);
	}

	private void processResourceRequest(String app, String resource,
			HttpServletResponse resp) throws IOException {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
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

	private void prepareExecution(HttpServletRequest req,
			HttpServletResponse resp, Session session) throws SessionException,
			FileUploadException, IOException {
		tx = session.getTX();
		x = null;
		ctx = new ASQueryContext(tx, metaDataMgr, req.getSession(), req);
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
