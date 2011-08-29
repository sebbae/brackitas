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
package org.brackit.as.http.uiOld;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItemIterator;
import org.apache.commons.fileupload.FileItemStream;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.fileupload.util.Streams;
import org.brackit.as.http.uiOld.Helper.FILE_TYPE;
import org.brackit.server.ServerException;
import org.brackit.server.session.Session;
import org.brackit.server.session.SessionException;
import org.brackit.server.tx.TxException;
import org.brackit.xquery.node.parser.DocumentParser;
import org.brackit.xquery.xdm.DocumentException;

/**
 * 
 * @author Henrique Valer
 * 
 */
public class UploadServlet extends UIServlet {
	/**
	 * Simply executes a query on the database to select the upload form and
	 * return it as a string to the servlet handler.
	 * 
	 * @param req
	 *            HTTP request
	 * @param resp
	 *            HTTP response
	 * @throws XQueryException
	 * @throws SessionException
	 * @throws TxException
	 */
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp,
			Session session) throws Exception {
		String vReturn = httpQuery(session, "fn:doc('upload.html')", req
				.getSession());

		// result output
		new PrintStream(resp.getOutputStream()).append(vReturn);
		resp.setStatus(HttpServletResponse.SC_OK);
	}

	/**
	 * Receives a document via a Form post and insert the document in the
	 * Brackit database. Return the Upload page with the respective message of
	 * upload success or fail
	 * 
	 * @param req
	 *            HTTP request
	 * @param resp
	 *            HTTP response
	 * @throws DocumentException
	 * @throws FileUploadException
	 * @throws XQueryException
	 * @throws SessionException
	 * @throws TxException
	 */
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp,
			Session session) throws Exception {
		if (!ServletFileUpload.isMultipartContent(req)) {
			getServletContext().setAttribute("errorMsg",
					"Illegal non-multipart request");
			doDispatch(req, resp, "/ui/error/");
		}

		try {
			String docName = store(req, resp, session);

			// load upload form
			String strResult = httpQuery(session, "fn:doc('upload.html')", req
					.getSession());
			// Replace successful upload message
			strResult = strResult.replaceAll(
					"<input type=\"hidden\" name=\"result\"/>",
					"<p style=\"color: green;\"><b>File " + docName
							+ " uploaded successfully!</b></p>");

			// result output
			new PrintStream(resp.getOutputStream()).append(strResult);
			resp.setStatus(HttpServletResponse.SC_OK);
		} catch (FileUploadException e) {
			throw new ServletException("Error handling file upload", e);
		}
	}

	private String store(HttpServletRequest req, HttpServletResponse resp,
			Session session) throws FileUploadException, IOException,
			ServletException, ServerException, DocumentException {
		// Storage options
		FILE_TYPE file_type = null;
		InputStream inFile = null;
		String docName = null;
		ServletFileUpload upload = new ServletFileUpload();
		FileItemIterator iter = upload.getItemIterator(req);

		while (iter.hasNext()) {
			/**
			 * Since the content is the page contains Multiple Part, we cannot
			 * use req.GetParameter, as usual. We need to use a iterator and
			 * choose the name of the parameter.
			 */
			FileItemStream item = iter.next();
			String paramName = item.getFieldName();
			InputStream paramInStream = item.openStream();

			if (paramName.equals("file_type")) {
				file_type = FILE_TYPE.valueOf(Streams.asString(paramInStream));
			} else if (paramName.equals("file")) {
				docName = "/" + item.getName();
				inFile = paramInStream;
				break;
			}
		}

		// Exception
		if (inFile == null) {
			throw new ServletException(
					"Missing parameter 'file' containing stream of document to be stored!");
		}

		// Storage type
		if (file_type.toString().equals("XML")) {
			metaDataMgr.create(session.checkTX(), docName, new DocumentParser(
					inFile));
		} else if (file_type.toString().equals("BLOB")) {
			metaDataMgr.putBlob(session.checkTX(), inFile, docName, -1);
		} else {
			getServletContext().setAttribute("errorMsg",
					"Storage type is invalid: " + file_type);
			doDispatch(req, resp, "/ui/error/");
		}

		return docName;
	}
}
