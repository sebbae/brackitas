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
import java.io.StreamCorruptedException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.brackit.server.session.Session;
import org.brackit.xquery.atomic.Atomic;

/**
 * 
 * @author Henrique Valer
 * 
 */
public class ResourceServlet extends BaseServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8332338424448843760L;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp,
			Session session) throws Exception {

		// if (req.getAttribute(FrontController.APP_SESSION_ATT) == null) {
		// throw new Exception("Direct access to this URL is not allowed.");
		// }

		try {
			String[] URI = ((Atomic) req.getSession().getAttribute(
					FrontController.HTTP_URI_REQ)).stringValue().split("/");
			ClassLoader cl = getClass().getClassLoader();
			StringBuffer resource = new StringBuffer();
			for (int i = 3; i < URI.length; i++) {
				resource.append("/" + URI[i]);
			}

			String s = String.format("apps/%s/resources%s",
					((Atomic) req.getSession().getAttribute(
							FrontController.APP_SESSION_ATT)).stringValue(),
					resource.toString());

			// TODO: Check if there will be problems with big files.
			// resp.setHeader("Content-length", in. );
			String contentType = getMimeType(URI[URI.length - 1]);
			resp.setContentType(contentType);

			InputStream in = cl.getResourceAsStream(s);
			BufferedOutputStream out = new BufferedOutputStream(resp
					.getOutputStream());
			try {
				byte[] buffer = new byte[4096];
				int bytesRead = 0;
				while ((bytesRead = in.read(buffer)) != -1) {
					out.write(buffer, 0, bytesRead);
					out.flush();
				}
			} catch (Exception e) {
				throw new StreamCorruptedException(String.format(
						"Error while reading inputStream of resource %s.",
						URI[URI.length]));
			} finally {
				out.close();
				in.close();
				resp.getOutputStream().flush();
			}
		} catch (Exception e) {
			throw new FileNotFoundException(String.format(
					"File %s does not exist under the application resources.",
					((Atomic) req.getSession().getAttribute(
							FrontController.HTTP_RESOURCE_NAME)).stringValue()));
		}
	}
}