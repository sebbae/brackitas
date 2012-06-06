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
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package org.brackit.as.xquery;

import java.io.File;
import java.io.InputStream;
import java.io.PrintWriter;

import org.brackit.as.xquery.function.app.Delete;
import org.brackit.as.xquery.function.app.Deploy;
import org.brackit.as.xquery.function.app.Exists;
import org.brackit.as.xquery.function.app.Generate;
import org.brackit.as.xquery.function.app.GetNames;
import org.brackit.as.xquery.function.app.GetStructure;
import org.brackit.as.xquery.function.app.IsRunning;
import org.brackit.as.xquery.function.app.Terminate;
import org.brackit.as.xquery.function.http.SendRequest;
import org.brackit.as.xquery.function.io.Append;
import org.brackit.as.xquery.function.request.GetCookie;
import org.brackit.as.xquery.function.request.GetCookieNames;
import org.brackit.as.xquery.function.request.GetParameter;
import org.brackit.as.xquery.function.request.GetParameterNames;
import org.brackit.as.xquery.function.request.GetReqAttribute;
import org.brackit.as.xquery.function.request.GetReqAttributeNames;
import org.brackit.as.xquery.function.request.IsMultipartContent;
import org.brackit.as.xquery.function.resource.DeleteResource;
import org.brackit.as.xquery.function.resource.RenameResource;
import org.brackit.as.xquery.function.resource.Upload;
import org.brackit.as.xquery.function.session.Clear;
import org.brackit.as.xquery.function.session.GetAttribute;
import org.brackit.as.xquery.function.session.GetAttributeNames;
import org.brackit.as.xquery.function.session.GetCreationTime;
import org.brackit.as.xquery.function.session.GetLastAccessedTime;
import org.brackit.as.xquery.function.session.GetMaxInactiveInterval;
import org.brackit.as.xquery.function.session.Invalidate;
import org.brackit.as.xquery.function.session.RemoveSessionAtt;
import org.brackit.as.xquery.function.session.SetAttribute;
import org.brackit.as.xquery.function.session.SetMaxInactiveInterval;
import org.brackit.as.xquery.function.util.GetASProperty;
import org.brackit.as.xquery.function.util.GetMimeType;
import org.brackit.as.xquery.function.util.ListPredefinedFunctions;
import org.brackit.as.xquery.function.util.ListPredefinedModules;
import org.brackit.as.xquery.function.util.MkDirectory;
import org.brackit.as.xquery.function.util.PlainPrint;
import org.brackit.as.xquery.function.util.RmDirectory;
import org.brackit.as.xquery.function.xqfile.CompileXQFile;
import org.brackit.as.xquery.function.xqfile.CreateXQFile;
import org.brackit.as.xquery.function.xqfile.DeleteXQFile;
import org.brackit.as.xquery.function.xqfile.GetCompilationResult;
import org.brackit.as.xquery.function.xqfile.IsLibrary;
import org.brackit.as.xquery.function.xqfile.SaveXQFile;
import org.brackit.xquery.ErrorCode;
import org.brackit.xquery.QueryException;
import org.brackit.xquery.XQuery;
import org.brackit.xquery.compiler.CompileChain;
import org.brackit.xquery.module.Functions;
import org.brackit.xquery.util.io.IOUtils;
import org.brackit.xquery.util.serialize.SubtreePrinter;
import org.brackit.xquery.xdm.DocumentException;
import org.brackit.xquery.xdm.Item;
import org.brackit.xquery.xdm.Iter;
import org.brackit.xquery.xdm.Kind;
import org.brackit.xquery.xdm.Node;
import org.brackit.xquery.xdm.Sequence;

/**
 * @author Sebastian Baechle
 * @author Henrique Valer
 * 
 */
public class ASXQuery extends XQuery {

	static {
		// SESSION
		Functions.predefine(new Clear());
		Functions.predefine(new GetAttributeNames());
		Functions.predefine(new GetCreationTime());
		Functions.predefine(new GetLastAccessedTime());
		Functions.predefine(new GetMaxInactiveInterval());
		Functions.predefine(new GetAttribute());
		Functions.predefine(new Invalidate());
		Functions.predefine(new RemoveSessionAtt());
		Functions.predefine(new SetMaxInactiveInterval());
		Functions.predefine(new SetAttribute());
		// Request
		Functions.predefine(new GetReqAttribute());
		Functions.predefine(new GetReqAttributeNames());
		Functions.predefine(new GetCookie());
		Functions.predefine(new GetCookieNames());
		Functions.predefine(new GetParameter());
		Functions.predefine(new GetParameterNames());
		Functions.predefine(new IsMultipartContent());
		// Util
		Functions.predefine(new PlainPrint());
		Functions.predefine(new MkDirectory());
		Functions.predefine(new ListPredefinedFunctions());
		Functions.predefine(new ListPredefinedModules());
		Functions.predefine(new GetMimeType());
		Functions.predefine(new RmDirectory());
		Functions.predefine(new GetASProperty());
		// App
		Functions.predefine(new GetNames());
		Functions.predefine(new Delete());
		Functions.predefine(new Terminate());
		Functions.predefine(new IsRunning());
		Functions.predefine(new Deploy());
		Functions.predefine(new GetStructure());
		Functions.predefine(new Generate());
		Functions.predefine(new Exists());
		// XQFile
		Functions.predefine(new CompileXQFile());
		Functions.predefine(new CreateXQFile());
		Functions.predefine(new DeleteXQFile());
		Functions.predefine(new SaveXQFile());
		Functions.predefine(new IsLibrary());
		Functions.predefine(new GetCompilationResult());
		// Resources handling
		Functions.predefine(new Upload());
		Functions.predefine(new DeleteResource());
		Functions.predefine(new RenameResource());
		// HTTP handling
		Functions.predefine(new SendRequest());
		// IO
		Functions.predefine(new Append());
	}

	private boolean longLive;

	private long LastModified;

	public boolean isLongLive() {
		return longLive;
	}

	public void setLongLive(boolean longLive) {
		this.longLive = longLive;
	}

	public long getLastModified() {
		return LastModified;
	}

	public void setLastModified(long lastModified) {
		LastModified = lastModified;
	}

	public ASXQuery(InputStream in) throws QueryException {
		super(IOUtils.getStringFromInputStream(in));
		this.longLive = false;
	}

	public ASXQuery(String s) throws QueryException {
		super(s);
		this.longLive = false;
	}

	public ASXQuery(File f) throws QueryException {
		super(IOUtils.getStringFromFile(f));
		this.longLive = false;
	}

	public ASXQuery(CompileChain chain, File f) throws QueryException {
		super(chain, IOUtils.getStringFromFile(f));
		this.longLive = false;
	}

	public ASXQuery(CompileChain chain, InputStream in) throws QueryException {
		super(chain, IOUtils.getStringFromInputStream(in));
		this.longLive = false;
	}

	public ASXQuery(CompileChain chain, String s) throws QueryException {
		super(chain, s);
		this.longLive = false;
	}

	public void serializeResult(ASQueryContext ctx, PrintWriter out,
			Sequence result) throws DocumentException, QueryException {

		if (result == null) {
			return;
		}

		boolean first = true;
		SubtreePrinter printer = new SubtreePrinter(out);
		printer.setPrettyPrint(true);
		printer.setAutoFlush(false);
		Item item;
		Iter it = result.iterate();
		try {
			while ((item = it.next()) != null) {
				if (item instanceof Node<?>) {
					Node<?> node = (Node<?>) item;
					Kind kind = node.getKind();

					if ((kind == Kind.ATTRIBUTE) || (kind == Kind.NAMESPACE)) {
						throw new QueryException(
								ErrorCode.ERR_SERIALIZE_ATTRIBUTE_OR_NAMESPACE_NODE);
					}
					if (kind == Kind.DOCUMENT) {
						node = node.getFirstChild();
						while (node.getKind() != Kind.ELEMENT) {
							node = node.getNextSibling();
						}
					}

					printer.print(node);
					first = true;
				} else {
					if (!first) {
						out.write(" ");
					}
					out.write(item.toString());
					first = false;
				}
			}
		} finally {
			printer.flush();
			out.flush();
			it.close();
		}
	}

}
