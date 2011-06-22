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
package org.brackit.as.http.rpc;

import java.io.InputStream;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItemIterator;
import org.apache.commons.fileupload.FileItemStream;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.brackit.as.http.HttpConnector;
import org.brackit.server.metadata.DBItem;
import org.brackit.server.session.Session;
import org.brackit.xquery.ErrorCode;
import org.brackit.xquery.QueryException;
import org.brackit.xquery.atomic.Bool;
import org.brackit.xquery.compiler.parser.XQueryParser.catchClause_return;
import org.brackit.xquery.node.parser.DocumentParser;
import org.brackit.xquery.util.path.Path;
import org.brackit.xquery.xdm.Collection;
import org.brackit.xquery.xdm.Node;
import org.brackit.xquery.xdm.Stream;

/**
 * 
 * @author Sebastian Baechle
 * @author Max Bechtold
 * @author Henrique Valer
 * 
 */
public class DBServlet extends RPCServlet {
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp,
			Session session) throws Exception {
		String docName = req.getRequestURI();
		docName = docName.substring(HttpConnector.GET_PREFIX.length() - 1);
		DBItem<?> dbitem =  metaDataMgr.getItem(session.getTX(), docName);

		
		//Collection<?> collection = metaDataMgr.getItem(session.getTX(), docName); // .lookup(session.getTX(), docName);
		//Stream<? extends Node<?>> docs = collection.getDocuments();
//		Node<?> document;
//		String vReturn = "null";
//		try {
//			document = (Node<?>) docs.next();
//
//			if (document == null) {
//					vReturn = "Error acessing the document " + docName;
//			} else {
//					vReturn = document.getSubtree().toString();
//			}
//		}
//		catch (Exception E) {
//			
//		}
		
		
		resp.setContentType(getMimeType(docName));
		Path<String> path = Path.parse(docName);
		String tail = path.tail();
		resp.setHeader("Content-disposition", String.format(
				"inline; filename=%s", tail));
		//resp.getOutputStream().print(vReturn);
		
		dbitem.serialize(resp.getOutputStream());
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp,
			Session session) throws Exception {
		if (!ServletFileUpload.isMultipartContent(req)) {
			throw new ServletException("Illegal non-multipart request");
		}
		try {
			store(req, resp, session);
		} catch (FileUploadException e) {
			throw new ServletException(e);
		}
	}

	private void store(HttpServletRequest req, HttpServletResponse resp,
			Session session) throws Exception {
		// Declaration
		InputStream file = null;
		String document = req.getRequestURI();
		document = document.substring(HttpConnector.POST_PREFIX.length() - 1);

		// Parse parameters
		ServletFileUpload upload = new ServletFileUpload();
		FileItemIterator iter;

		iter = upload.getItemIterator(req);
		while (iter.hasNext()) {
			FileItemStream item = iter.next();
			String name = item.getFieldName();
			InputStream stream = item.openStream();

			if (name.equals("file")) {
				file = stream;
				break; // No further parameters checked as stream needs to be
				// preserved, see semantics of FileItemIterator
			}
		}

		if (file == null) {
			throw new ServletException(
					"Missing parameter 'file' containing stream of document to be stored!");
		}

		metaDataMgr.create(session.getTX(), document, new DocumentParser(file));
	}

	@Override
	protected void doDelete(HttpServletRequest req, HttpServletResponse resp,
			Session session) throws Exception {
		String document = req.getRequestURI();
		document = document.substring(HttpConnector.DELETE_PREFIX.length() - 1);
		metaDataMgr.drop(session.getTX(), document);
	}
}
