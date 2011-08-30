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
import org.brackit.xquery.QueryContext;
import org.brackit.xquery.atomic.Una;
import org.brackit.xquery.compiler.CompileChain;
import org.brackit.xquery.expr.Cast;
import org.brackit.xquery.expr.ExtVariable;
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

		ServletContext context = getServletContext();

		// TODO:
		try {

			// Resolve Application
			String URI = req.getRequestURI();
			String[] URIParts = URI.split("/");
			// TODO: Improve get naming
			String app = URIParts[2];
			String page = URIParts[3];
			String resource = URI.substring(URI.lastIndexOf("/"));
			
			req.setAttribute(APP_SESSION_ATT, app);
			req.setAttribute(PAGE_SESSION_ATT, page);
			req.setAttribute(HTTP_URI_REQ, URI);
			req.setAttribute(HTTP_RESOURCE_NAME, resource);

			System.out.println("XQ MIME: " + getMimeType("a.xq"));
			
			// Resolve resource
			if (!UNKNOWN_MIMETYPE.equals(getMimeType(resource))) {
				RequestDispatcher dispatcher = context
						.getRequestDispatcher(HttpConnector.APP_RESOURCE_DISP_TARGET);
				dispatcher.forward(req, resp);
			}
			
			// Compilation and execution
			CompileChain chain = null;			
			ASXQuery x = null;
			QueryContext ctx = new HttpSessionTXQueryContext(session.checkTX(),
					metaDataMgr, req.getSession());
			try {
				// without MVC
				// http://localhost:8080/app/helloWorld/folder/public/default.xq
				if (resource.endsWith(".xq")) {

					// Dinamic binding of parameters: name = variable name
					chain = new ASCompileChain(metaDataMgr, session.checkTX());
					x = new ASXQuery(chain, getQueryFile(((String) req
							.getAttribute(FrontController.HTTP_URI_REQ)),app, page));
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
				}
				else
				{
					// Query with MVC
				}
			}
			finally {
				x.setPrettyPrint(true);
				x.serialize(ctx, new PrintStream(resp.getOutputStream()));					
			}

			/*
			 * Compile query
			 * Check on ServletContext if the applicationContext exists
			 *   yes? -> Compile query with it
			 *   no? -> Create application context
			 */
			
			

			// Execute it

		} catch (Exception e) {
			e.printStackTrace();
			req.setAttribute(ErrorServlet.ERROR_ATT, e.getMessage());
			RequestDispatcher dispatcher = context
					.getRequestDispatcher(HttpConnector.APP_ERROR_DISP_TARGET);
			dispatcher.forward(req, resp);
		}
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
			String s = String.format("apps/%s%s", app, resource
					.toString());
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
