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

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.PrintStream;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.brackit.as.http.HttpConnector;
import org.brackit.as.xquery.ASXQuery;
import org.brackit.as.xquery.HttpSessionTXQueryContext;
import org.brackit.as.xquery.compiler.ASCompileChain;
import org.brackit.server.session.Session;
import org.brackit.server.tx.Tx;
import org.brackit.server.tx.impl.TX;
import org.brackit.xquery.QueryContext;
import org.brackit.xquery.atomic.Atomic;
import org.brackit.xquery.atomic.Str;
import org.brackit.xquery.atomic.Una;
import org.brackit.xquery.compiler.BaseResolver;
import org.brackit.xquery.compiler.CompileChain;
import org.brackit.xquery.compiler.ModuleResolver;
import org.brackit.xquery.expr.Cast;
import org.brackit.xquery.expr.ExtVariable;
import org.brackit.xquery.module.LibraryModule;
import org.brackit.xquery.sequence.type.AtomicType;
import org.brackit.xquery.sequence.type.SequenceType;
import org.brackit.xquery.xdm.Item;
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
		ServletContext context = getServletContext();

//		// TODO:
//		try {
//			
			// Resolve Application
			String URI = req.getRequestURI();
			String[] URIParts = URI.split("/");
			// TODO: Improve get naming
			String app = URIParts[2];
			String page = URIParts[3];
			String resource = URI.substring(URI.lastIndexOf("/"));

			// TODO: Improve: Where is the best place for such objects?
			// On the AppCtx object!!!
			req.getSession().setAttribute(APP_SESSION_ATT,
					(Atomic) new Str(app));
			req.getSession().setAttribute(PAGE_SESSION_ATT,
					(Atomic) new Str(page));
			req.getSession().setAttribute(HTTP_URI_REQ, (Atomic) new Str(URI));
			req.getSession().setAttribute(HTTP_RESOURCE_NAME,
					(Atomic) new Str(resource));

			// Resolve resource
			if (!UNKNOWN_MIMETYPE.equals(getMimeType(resource))) {
				RequestDispatcher dispatcher = context
						.getRequestDispatcher(HttpConnector.APP_RESOURCE_DISP_TARGET);
				dispatcher.forward(req, resp);
			}

			// Compilation and execution
			Tx tx = session.getTX();
			ASCompileChain chain = (ASCompileChain) getServletContext().getAttribute("CompileChain");
			ASXQuery x = null;
			HttpSessionTXQueryContext ctx = new HttpSessionTXQueryContext(tx, metaDataMgr, req.getSession());
			
			try {
				// without MVC
				// http://localhost:8080/app/helloWorld/folder/public/default.xq
				if (resource.endsWith(".xq")) {

					// Dinamic binding of parameters: name = variable name
//					chain = new ASCompileChain(metaDataMgr, tx);
					if (chain == null) 
						chain = new ASCompileChain(metaDataMgr, tx);
					x = new ASXQuery(chain, getQueryFile(((Atomic) req
							.getSession().getAttribute(
									FrontController.HTTP_URI_REQ))
							.stringValue(), app, page));
					for (ExtVariable var : x.getModule().getVariables()
							.getDeclaredVariables()) {
						SequenceType type = var.getType();
						if ((type != null)
								&& (var.getType().getItemType().isAtomic())) {
							Type expectedAtomicType = ((AtomicType) var
									.getType().getItemType()).type;
							String param = req.getParameter(var.getName()
									.getLocalName());
							if ((param != null)
									&& (!(param = param.trim()).isEmpty())) {
								Item item = new Una(param);
								item = Cast.cast(item, expectedAtomicType,
										false);
								ctx.bind(var.getName(), item);
							}
						}
					}
					getServletContext().setAttribute("CompileChain", chain);
				}
//				else {
//					// Query with MVC
//					// http://localhost:8080/app/helloWorldMVC/test/public/test/
//
//					/*
//					 * TODO: 1. Remove module and import module declaration.
//					 * Make it automatically. 2. Compile model -> compile
//					 * controller -> execute specific controller function ->
//					 * Execute template -> template also calls functions
//					 */
//
//					final BaseResolver res = new BaseResolver();
//					CompileChain chainMVC = new CompileChain() {
//						private final ModuleResolver resolver = res;
//
//						@Override
//						protected ModuleResolver getModuleResolver() {
//							return resolver;
//						}
//					};
//					ClassLoader cl = getClass().getClassLoader();
//					ASXQuery xq = new ASXQuery(
//							chainMVC,
//							cl
//									.getResourceAsStream("apps/helloWorldMVC/models/testModel.xq"));
//					LibraryModule module = (LibraryModule) xq.getModule();
//					res.register(module.getTargetNS().getUri(), module);
//					x = new ASXQuery(
//							chainMVC,
//							cl
//									.getResourceAsStream("apps/helloWorldMVC/controllers/testController.xq"));
//					// QueryContext ctx = createContext();
//					// Sequence result = xq2.execute(ctx);
//				}
			}
			catch (Throwable e) {
				e.printStackTrace();
				throw new Exception(e);
			} finally {
				// Execute it
				if (x != null) {
					x.setPrettyPrint(true);
					x.serialize(ctx, new PrintStream(resp.getOutputStream()));
//					session.commit();
				}
			}

//		} catch (Exception e) {
//			e.printStackTrace();
////			req.setAttribute(ErrorServlet.ERROR_ATT, e.getMessage());
////			RequestDispatcher dispatcher = context
////					.getRequestDispatcher(HttpConnector.APP_ERROR_DISP_TARGET);
////			dispatcher.forward(req, resp);
//		}
	}

	private InputStream getQueryFile(String reqURI, String app, String page)
			throws FileNotFoundException {
		try {
			// http://localhost:8080/app/helloWorld/folder/public/default.xq
			String[] URI = reqURI.split("/");
			StringBuffer resource = new StringBuffer();
			for (int i = 3; i < URI.length; i++) {
				resource.append("/" + URI[i]);
			}
			String s = String.format("apps/%s%s", app, resource.toString());
			ClassLoader cl = getClass().getClassLoader();
			InputStream in = cl.getResourceAsStream(s);
			if (in != null)
				return in;
			else
				throw new FileNotFoundException();
		} catch (Exception e) {
			e.printStackTrace();
		}
		throw new FileNotFoundException();
	}
}
