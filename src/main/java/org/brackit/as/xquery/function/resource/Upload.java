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
package org.brackit.as.xquery.function.resource;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.brackit.as.annotation.FunctionAnnotation;
import org.brackit.as.context.InputStreamName;
import org.brackit.as.http.HttpConnector;
import org.brackit.as.xquery.ASErrorCode;
import org.brackit.as.xquery.ASQueryContext;
import org.brackit.xquery.QueryContext;
import org.brackit.xquery.QueryException;
import org.brackit.xquery.atomic.AnyURI;
import org.brackit.xquery.atomic.Atomic;
import org.brackit.xquery.atomic.Bool;
import org.brackit.xquery.atomic.QNm;
import org.brackit.xquery.function.AbstractFunction;
import org.brackit.xquery.module.StaticContext;
import org.brackit.xquery.xdm.Signature;
import org.brackit.xquery.xdm.Sequence;

/**
 * 
 * @author Henrique Valer
 * 
 */
@FunctionAnnotation(description = "Uploads a resource to the server. The resource path "
		+ "($rscPathName) starts at the applications directory, by default: "
		+ "src/main/resources/apps. The resource input ($rscInput) can be of two types: "
		+ "either the name of the parameter being submited via POST, or a string "
		+ "containing the location of the resource."
		+ "On the previous, we can submit an HTML form using POST containing a parameter "
		+ "with the same name used as argument for this function. On the former, we can "
		+ "use a string, suffixed with http, https, ftp or jar to access the resource and "
		+ "upload it to the server." + " ", parameters = { "$rscPathName",
		"$rscInput" })
public class Upload extends AbstractFunction {

	public Upload(QNm name, Signature signature) {
		super(name, signature, true);
	}

	@Override
	public Sequence execute(StaticContext sctx, QueryContext ctx,
			Sequence[] args) throws QueryException {
		URLConnection conn = null;
		try {
			String fRelStoragePath = ((Atomic) args[0]).atomize().stringValue();
			String fPath = ((Atomic) args[1]).atomize().stringValue();
			String fName = null;
			String scheme = new URI(fPath).getScheme();
			InputStream in = null;
			if (scheme == null) {
				HttpServletRequest req = ((ASQueryContext) ctx).getReq();
				if (ServletFileUpload.isMultipartContent(req)) {
					InputStreamName isn = ((InputStreamName) ((ASQueryContext) ctx)
							.getMutliPartParam(fPath));
					in = isn.getInputStream();
					fName = isn.getName();
				}
			} else if (scheme.equals("http") || scheme.equals("https")
					|| scheme.equals("ftp") || scheme.equals("jar")) {
				URL url = new URL(((AnyURI) args[0]).stringValue());
				conn = url.openConnection();
				fName = conn.getURL().getPath();
				in = conn.getInputStream();
			}
			File f = new File(String.format("%s/%s/%s",
					HttpConnector.APPS_PATH, fRelStoragePath, fName));
			OutputStream out = new FileOutputStream(f);

			byte[] buffer = new byte[1024 * 16];
			int bytesRead = 0;
			while ((bytesRead = in.read(buffer)) != -1) {
				out.write(buffer, 0, bytesRead);
			}
			out.close();
			in.close();
			return Bool.TRUE;
		} catch (Exception e) {
			throw new QueryException(e, ASErrorCode.RSC_UPLOAD_INT_ERROR, e
					.getMessage());
		} finally {
			if (conn != null) {
				if (conn != null) {
					if (conn instanceof HttpURLConnection) {
						((HttpURLConnection) conn).disconnect();
					}
					conn = null;
				}
			}
		}
	}
}