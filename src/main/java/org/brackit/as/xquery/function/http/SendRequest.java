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
package org.brackit.as.xquery.function.http;

import java.io.PrintStream;
import java.util.Enumeration;
import java.util.HashMap;

import org.brackit.as.annotation.FunctionAnnotation;
import org.brackit.as.annotation.ModuleAnnotation;
import org.brackit.as.util.FunctionUtils;
import org.brackit.as.xquery.ASErrorCode;
import org.brackit.xquery.QueryContext;
import org.brackit.xquery.QueryException;
import org.brackit.xquery.atomic.Atomic;
import org.brackit.xquery.atomic.QNm;
import org.brackit.xquery.atomic.Str;
import org.brackit.xquery.function.AbstractFunction;
import org.brackit.xquery.module.StaticContext;
import org.brackit.xquery.node.SubtreePrinter;
import org.brackit.xquery.node.d2linked.D2NodeFactory;
import org.brackit.xquery.node.parser.DocumentParser;
import org.brackit.xquery.sequence.ItemSequence;
import org.brackit.xquery.xdm.Collection;
import org.brackit.xquery.xdm.Node;
import org.brackit.xquery.xdm.Sequence;
import org.brackit.xquery.xdm.Signature;
import org.brackit.xquery.xdm.Stream;
import org.eclipse.jetty.client.ContentExchange;
import org.eclipse.jetty.client.HttpClient;

/**
 * 
 * @author Henrique Valer
 * 
 */

@ModuleAnnotation(description = "A module for dealnig with HTTP requests and "
		+ "responses.")
@FunctionAnnotation(description = "Sends an HTTP request and return the "
		+ "corresponding response. It is a simplified version of the protocol, "
		+ "since it does not support muti-part messages yet. It receives a request"
		+ " object ($request) as parameter and returns a response object. The "
		+ "request object looks as follows: &lt;request method = \"get\" "
		+ "href=\"http://www.google.de\" status-only=\"false\" timeout=\"10\"&gt; "
		+ "&lt;header name=\"Expires\" value=\"-1\"/&gt;&lt;header name=\"Date\""
		+ " value=\"Thu, 17 Nov 2011 16:26:59 GMT\"/&gt;&lt;/request&gt;. "
		+ "The result object looks like this: &lt;response status=\"200\"&gt;"
		+ " &lt;header name=\"Expires\" value=\"-1\"/&gt;&lt;header name=\"Date\""
		+ " value=\"Thu, 17 Nov 2011 16:26:59 GMT\"/&gt;&lt;/http:response&gt;. "
		+ "Besides that, each response body is appended to the response object as "
		+ "a xs:string, so the result of the function is a sequence composed of a "
		+ "response object followed by the possible many html bodies.", parameters = "$request")
public class SendRequest extends AbstractFunction {

	public SendRequest(QNm name, Signature signature) {
		super(name, signature, true);
	}

	@Override
	public Sequence execute(StaticContext sctx, QueryContext ctx,
			Sequence[] args) throws QueryException {
		String req = null;

		if (args[0] instanceof Atomic) {
			req = ((Atomic) args[0]).stringValue();
		} else {
			PrintStream buf = FunctionUtils.createBuffer();
			SubtreePrinter.print((Node<?>) args[0], buf);
			req = buf.toString();
		}
		Collection<?> c = new D2NodeFactory().build(new DocumentParser(req))
				.getCollection();

		HashMap<String, String> reqAtts = new HashMap<String, String>();
		HashMap<String, String> reqHeaders = new HashMap<String, String>();

		Stream<? extends Node<?>> docs = (Stream<? extends Node<?>>) c
				.getDocuments();
		Node<?> document;
		try {
			document = (Node<?>) docs.next();
			if (document == null) {
				throw new Exception();
			}
			Stream<? extends Node<?>> children = document.getChildren();
			Node<?> child;
			while ((child = children.next()) != null) {
				QNm childName = child.getName();
				if (childName.getLocalName().equals("request")) {

					// process attributes
					Stream<? extends Node<?>> atts = child.getAttributes();
					Node<?> att;
					while ((att = atts.next()) != null) {
						reqAtts.put(att.getName().getLocalName(), att
								.getValue().stringValue());
					}
					atts.close();

					// process headers
					Stream<? extends Node<?>> headers = child.getChildren();
					Node<?> header;
					while ((header = headers.next()) != null) {
						if (header.getName().getLocalName().equals("header")) {
							try {
								String name = header.getAttribute(
										new QNm("name")).getValue()
										.stringValue();
								String value = header.getAttribute(
										new QNm("value")).getValue()
										.stringValue();
								if (name == null || value == null)
									throw new QueryException(
											ASErrorCode.HTTP_SENDREQUEST_INT_ERROR,
											String
													.format("Mall formed header element."));
								reqHeaders.put(name, value);
							} catch (Exception e) {
								throw new QueryException(
										ASErrorCode.HTTP_SENDREQUEST_INT_ERROR,
										String
												.format("Badly formed header element."));
							}
						}
					}
				} else {
					throw new QueryException(
							ASErrorCode.HTTP_SENDREQUEST_INT_ERROR, String
									.format("Undesired element %s.", childName
											.getLocalName(), args));
				}
			}
		} catch (Exception e) {
			throw new QueryException(e, ASErrorCode.HTTP_SENDREQUEST_INT_ERROR,
					e.getMessage());
		}

		// request attributes validation
		if (!reqAtts.get("method").toUpperCase().equals("GET")
				&& !reqAtts.get("method").toUpperCase().equals("POST")) {
			throw new QueryException(ASErrorCode.HTTP_SENDREQUEST_INT_ERROR,
					String.format("Specified method, %s, is not valid.",
							reqAtts.get("method")));
		}
		if (reqAtts.get("href") == null) {
			throw new QueryException(ASErrorCode.HTTP_SENDREQUEST_INT_ERROR,
					"Href (URI) not present.");
		}
		if (reqAtts.get("status-only") != null) {
			if (!reqAtts.get("status-only").equals("true")
					&& !reqAtts.get("status-only").equals("false"))
				throw new QueryException(
						ASErrorCode.HTTP_SENDREQUEST_INT_ERROR,
						"The status-only, if present, must be either \"true\" or \"false\".");
		}
		if (reqAtts.get("timeout") != null) {
			try {
				Integer.valueOf(reqAtts.get("timeout"));
			} catch (NumberFormatException e) {
				throw new QueryException(
						ASErrorCode.HTTP_SENDREQUEST_INT_ERROR,
						"Timeout attribute value must be a proper integer.");
			}
		}

		// creates connection and waits the response
		try {
			HttpClient client = new HttpClient();
			client.start();

			ContentExchange exchange = new ContentExchange(true);
			exchange.setURL(reqAtts.get("href"));
			exchange.setMethod(reqAtts.get("method").toUpperCase());
			exchange.setTimeout(Integer.valueOf(reqAtts.get("timeout")) * 1000);

			// Waits until the exchange is terminated
			client.send(exchange);
			exchange.waitForDone();

			// builds response
			// <response status = integer>
			// (http:header*)
			// <response>
			// http:body)?
			StringBuffer response = new StringBuffer();
			response.append(String.format("<response status=\"%s\">\n",
					exchange.getResponseStatus()));
			Enumeration<String> e = exchange.getResponseFields()
					.getFieldNames();
			while (e.hasMoreElements()) {
				String field = e.nextElement();
				String hed = genHeader(field, exchange.getResponseFields()
						.getStringField(field));
				response.append(hed);
			}
			response.append("</response>\n");
			if (new Boolean(reqAtts.get("status-only"))) {
				return new D2NodeFactory().build(new DocumentParser(response
						.toString()));
			} else {
				return new ItemSequence(new D2NodeFactory()
						.build(new DocumentParser(response.toString())),
						new Str(exchange.getResponseContent()));
			}

		} catch (Exception e) {
			throw new QueryException(e, ASErrorCode.HTTP_SENDREQUEST_INT_ERROR,
					e.getMessage());
		}
	}

	private String genHeader(String name, String value) {
		return String.format("<header name=\"%s\" value=\"%s\"/>", name, value);
	}
}