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
package org.brackit.as.http.ui;

import java.io.PrintStream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.brackit.server.session.Session;
import org.brackit.server.session.SessionException;
import org.brackit.server.tx.TxException;

public class FormDownloadServlet extends UIServlet {
	/**
	 * Executes a query to select all files in the database, returning a html
	 * list with this files and the link for their download places. Then
	 * executes a query to retrieve the download form and insert the HTML list
	 * in this download form.
	 * 
	 * @param req
	 *            HTTP request
	 * @param resp
	 *            HTTP response
	 * @return String with the web page containing form for upload files to Brackit
	 *         database
	 * @throws SessionException
	 * @throws TxException
	 * @throws XQueryException
	 */
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp,
			Session session) throws Exception {
		String vReturn = null;

		String XQuery_DB_FILES = "let $master := doc(\"_master.xml\")/bit//dir[@name=\"%s\"] return <ul>"
				+ "{for $i in $master/doc "
				+ "let $preText := \"http://localhost:8080/ui/?payload=file_download&amp;file_name=\" "
				+ "return <li><a href=\"{concat(data($preText),data($i/@name))}\">File:{data($i/@name)}</a></li>} "
				+ "{for $i in $master/blob "
				+ "let $preText := \"http://localhost:8080/ui/?payload=file_download&amp;file_name=\" "
				+ "return <li><a href=\"{concat(data($preText),data($i/@name))}\">File:{data($i/@name)}</a></li>} "
				+ "</ul>";

		vReturn = query(session, "fn:doc('download.html')");
		String strListFile = query(session, String.format(XQuery_DB_FILES, "/"));
		strListFile = strListFile.replaceAll("&", "&amp;").replaceAll(
				"file_name=/", "file_name=");
		vReturn = vReturn.replaceAll(
				"<input type=\"hidden\" name=\"result\" />", strListFile);

		// result output
		new PrintStream(resp.getOutputStream()).append(vReturn);
		resp.setStatus(HttpServletResponse.SC_OK);
	}
}
