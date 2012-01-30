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

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
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
import org.brackit.as.xquery.function.bit.AddDocToCollection;
import org.brackit.as.xquery.function.bit.CreateCollection;
import org.brackit.as.xquery.function.bit.DropCollection;
import org.brackit.as.xquery.function.bit.Eval;
import org.brackit.as.xquery.function.bit.ExistCollection;
import org.brackit.as.xquery.function.bit.LoadFile;
import org.brackit.as.xquery.function.bit.MakeDirectory;
import org.brackit.as.xquery.function.bit.StoreDoc;
import org.brackit.as.xquery.function.http.SendRequest;
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
import org.brackit.as.xquery.function.util.GetMimeType;
import org.brackit.as.xquery.function.util.ListPredefinedFunctions;
import org.brackit.as.xquery.function.util.ListPredefinedModules;
import org.brackit.as.xquery.function.util.MkDirectory;
import org.brackit.as.xquery.function.util.PlainPrint;
import org.brackit.as.xquery.function.xqfile.CompileXQFile;
import org.brackit.as.xquery.function.xqfile.CreateXQFile;
import org.brackit.as.xquery.function.xqfile.DeleteXQFile;
import org.brackit.as.xquery.function.xqfile.GetCompilationResult;
import org.brackit.as.xquery.function.xqfile.IsLibrary;
import org.brackit.as.xquery.function.xqfile.SaveXQFile;
import org.brackit.xquery.ErrorCode;
import org.brackit.xquery.QueryException;
import org.brackit.xquery.XQuery;
import org.brackit.xquery.atomic.QNm;
import org.brackit.xquery.compiler.CompileChain;
import org.brackit.xquery.module.Functions;
import org.brackit.xquery.module.Namespaces;
import org.brackit.xquery.node.SubtreePrinter;
import org.brackit.xquery.xdm.DocumentException;
import org.brackit.xquery.xdm.Item;
import org.brackit.xquery.xdm.Iter;
import org.brackit.xquery.xdm.Kind;
import org.brackit.xquery.xdm.Node;
import org.brackit.xquery.xdm.Sequence;
import org.brackit.xquery.xdm.Signature;
import org.brackit.xquery.xdm.type.AnyItemType;
import org.brackit.xquery.xdm.type.AtomicType;
import org.brackit.xquery.xdm.type.Cardinality;
import org.brackit.xquery.xdm.type.SequenceType;

/**
 * @author Sebastian Baechle
 * @author Henrique Valer
 * 
 */
public class ASXQuery extends XQuery {

	static {
		// Bit
		Functions.predefine(new DropCollection(new QNm(Namespaces.BIT_NSURI,
				Namespaces.BIT_PREFIX, "drop-collection"), new Signature(
				new SequenceType(AtomicType.BOOL, Cardinality.One),
				new SequenceType(AtomicType.STR, Cardinality.One))));

		Functions.predefine(new Eval(new QNm(Namespaces.BIT_NSURI,
				Namespaces.BIT_PREFIX, "eval"), new Signature(new SequenceType(
				AtomicType.STR, Cardinality.ZeroOrOne), new SequenceType(
				AnyItemType.ANY, Cardinality.One))));

		Functions.predefine(new LoadFile(new QNm(Namespaces.BIT_NSURI,
				Namespaces.BIT_PREFIX, "load-file"), new Signature(
				new SequenceType(AtomicType.STR, Cardinality.ZeroOrOne),
				new SequenceType(AtomicType.STR, Cardinality.One))));

		Functions.predefine(new MakeDirectory(new QNm(Namespaces.BIT_NSURI,
				Namespaces.BIT_PREFIX, "make-directory"), new Signature(
				new SequenceType(AtomicType.STR, Cardinality.One),
				new SequenceType(AtomicType.STR, Cardinality.One))));

		Functions.predefine(new StoreDoc(new QNm(Namespaces.BIT_NSURI,
				Namespaces.BIT_PREFIX, "store-doc"), new Signature(
				new SequenceType(AtomicType.STR, Cardinality.ZeroOrOne),
				new SequenceType(AtomicType.STR, Cardinality.One),
				new SequenceType(AnyItemType.ANY, Cardinality.One))));

		Functions.predefine(new AddDocToCollection(new QNm(
				Namespaces.BIT_NSURI, Namespaces.BIT_PREFIX,
				"add-doc-to-collection"), new Signature(new SequenceType(
				AtomicType.STR, Cardinality.ZeroOrOne), new SequenceType(
				AtomicType.STR, Cardinality.One), new SequenceType(
				AnyItemType.ANY, Cardinality.One))));

		Functions.predefine(new CreateCollection(new QNm(Namespaces.BIT_NSURI,
				Namespaces.BIT_PREFIX, "create-collection"), new Signature(
				new SequenceType(AtomicType.BOOL, Cardinality.One),
				new SequenceType(AtomicType.STR, Cardinality.One))));

		Functions.predefine(new ExistCollection(new QNm(Namespaces.BIT_NSURI,
				Namespaces.BIT_PREFIX, "exist-collection"), new Signature(
				new SequenceType(AtomicType.BOOL, Cardinality.One),
				new SequenceType(AtomicType.STR, Cardinality.One))));

		// SESSION
		Functions.predefine(new Clear(new QNm(Namespaces.SESSION_NSURI,
				Namespaces.SESSION_PREFIX, "clear"), new Signature(
				new SequenceType(AtomicType.BOOL, Cardinality.One))));

		Functions.predefine(new GetAttributeNames(new QNm(
				Namespaces.SESSION_NSURI, Namespaces.SESSION_PREFIX,
				"get-attribute-names"), new Signature(new SequenceType(
				AnyItemType.ANY, Cardinality.ZeroOrMany))));

		Functions.predefine(new GetCreationTime(new QNm(
				Namespaces.SESSION_NSURI, Namespaces.SESSION_PREFIX,
				"get-creation-time"), new Signature(new SequenceType(
				AtomicType.DATE, Cardinality.ZeroOrOne))));

		Functions.predefine(new GetLastAccessedTime(new QNm(
				Namespaces.SESSION_NSURI, Namespaces.SESSION_PREFIX,
				"get-last-accessed-time"), new Signature(new SequenceType(
				AtomicType.DATE, Cardinality.ZeroOrOne))));

		Functions.predefine(new GetMaxInactiveInterval(new QNm(
				Namespaces.SESSION_NSURI, Namespaces.SESSION_PREFIX,
				"get-max-inactive-interval"), new Signature(new SequenceType(
				AtomicType.INT, Cardinality.ZeroOrOne))));

		Functions.predefine(new GetAttribute(new QNm(Namespaces.SESSION_NSURI,
				Namespaces.SESSION_PREFIX, "get-attribute"), new Signature(
				new SequenceType(AnyItemType.ANY, Cardinality.One),
				new SequenceType(AtomicType.STR, Cardinality.One))));

		Functions.predefine(new Invalidate(new QNm(Namespaces.SESSION_NSURI,
				Namespaces.SESSION_PREFIX, "invalidate"), new Signature(
				new SequenceType(AtomicType.BOOL, Cardinality.One))));

		Functions.predefine(new RemoveSessionAtt(new QNm(
				Namespaces.SESSION_NSURI, Namespaces.SESSION_PREFIX,
				"remove-attribute"), new Signature(new SequenceType(
				AnyItemType.ANY, Cardinality.One), new SequenceType(
				AtomicType.STR, Cardinality.One))));

		Functions.predefine(new SetMaxInactiveInterval(new QNm(
				Namespaces.SESSION_NSURI, Namespaces.SESSION_PREFIX,
				"set-max-inactive-interval"), new Signature(new SequenceType(
				AtomicType.BOOL, Cardinality.One), new SequenceType(
				AtomicType.INT, Cardinality.One))));

		Functions.predefine(new SetAttribute(new QNm(Namespaces.SESSION_NSURI,
				Namespaces.SESSION_PREFIX, "set-attribute"), new Signature(
				new SequenceType(AtomicType.BOOL, Cardinality.One),
				new SequenceType(AtomicType.STR, Cardinality.One),
				new SequenceType(AnyItemType.ANY, Cardinality.One))));

		// Request
		Functions.predefine(new GetReqAttribute(new QNm(
				Namespaces.REQUEST_NSURI, Namespaces.REQUEST_PREFIX,
				"get-attribute"), new Signature(new SequenceType(
				AnyItemType.ANY, Cardinality.One), new SequenceType(
				AtomicType.STR, Cardinality.One))));

		Functions.predefine(new GetReqAttributeNames(new QNm(
				Namespaces.REQUEST_NSURI, Namespaces.REQUEST_PREFIX,
				"get-attribute-names"), new Signature(new SequenceType(
				AnyItemType.ANY, Cardinality.ZeroOrMany))));

		Functions.predefine(new GetCookie(new QNm(Namespaces.REQUEST_NSURI,
				Namespaces.REQUEST_PREFIX, "get-cookie"), new Signature(
				new SequenceType(AnyItemType.ANY, Cardinality.One),
				new SequenceType(AtomicType.STR, Cardinality.One))));

		Functions.predefine(new GetCookieNames(new QNm(
				Namespaces.REQUEST_NSURI, Namespaces.REQUEST_PREFIX,
				"get-cookie-names"), new Signature(new SequenceType(
				AnyItemType.ANY, Cardinality.ZeroOrMany))));

		Functions.predefine(new GetParameter(new QNm(Namespaces.REQUEST_NSURI,
				Namespaces.REQUEST_PREFIX, "get-parameter"), new Signature(
				new SequenceType(AnyItemType.ANY, Cardinality.One),
				new SequenceType(AtomicType.STR, Cardinality.One))));

		Functions.predefine(new GetParameterNames(new QNm(
				Namespaces.REQUEST_NSURI, Namespaces.REQUEST_PREFIX,
				"get-parameter-names"), new Signature(new SequenceType(
				AnyItemType.ANY, Cardinality.ZeroOrMany))));

		Functions.predefine(new IsMultipartContent(new QNm(
				Namespaces.REQUEST_NSURI, Namespaces.REQUEST_PREFIX,
				"is-multipart-content"), new Signature(new SequenceType(
				AtomicType.BOOL, Cardinality.One))));

		// Util
		Functions.predefine(new PlainPrint(new QNm(Namespaces.UTIL_NSURI,
				Namespaces.UTIL_PREFIX, "plain-print"), new Signature(
				new SequenceType(AtomicType.STR, Cardinality.ZeroOrOne),
				new SequenceType(AnyItemType.ANY, Cardinality.One))));

		Functions.predefine(new MkDirectory(new QNm(Namespaces.UTIL_NSURI,
				Namespaces.UTIL_PREFIX, "mk-dir"), new Signature(
				new SequenceType(AtomicType.BOOL, Cardinality.One),
				new SequenceType(AtomicType.STR, Cardinality.One))));

		Functions.predefine(new ListPredefinedFunctions(new QNm(
				Namespaces.UTIL_NSURI, Namespaces.UTIL_PREFIX,
				"list-predefined-functions"), new Signature(new SequenceType(
				AnyItemType.ANY, Cardinality.One), new SequenceType(
				AtomicType.STR, Cardinality.One))));

		Functions.predefine(new ListPredefinedModules(new QNm(
				Namespaces.UTIL_NSURI, Namespaces.UTIL_PREFIX,
				"list-predefined-modules"), new Signature(new SequenceType(
				AnyItemType.ANY, Cardinality.One))));

		Functions.predefine(new GetMimeType(new QNm(Namespaces.UTIL_NSURI,
				Namespaces.UTIL_PREFIX, "get-mime-type"), new Signature(
				new SequenceType(AtomicType.STR, Cardinality.One),
				new SequenceType(AtomicType.STR, Cardinality.One))));

		// App
		Functions.predefine(new GetNames(new QNm(Namespaces.APP_NSURI,
				Namespaces.APP_PREFIX, "get-names"), new Signature(
				new SequenceType(AnyItemType.ANY, Cardinality.One))));

		Functions.predefine(new Delete(new QNm(Namespaces.APP_NSURI,
				Namespaces.APP_PREFIX, "delete"), new Signature(
				new SequenceType(AtomicType.BOOL, Cardinality.One),
				new SequenceType(AtomicType.STR, Cardinality.One))));

		Functions.predefine(new Terminate(new QNm(Namespaces.APP_NSURI,
				Namespaces.APP_PREFIX, "terminate"), new Signature(
				new SequenceType(AtomicType.BOOL, Cardinality.One),
				new SequenceType(AtomicType.STR, Cardinality.One))));

		Functions.predefine(new IsRunning(new QNm(Namespaces.APP_NSURI,
				Namespaces.APP_PREFIX, "is-running"), new Signature(
				new SequenceType(AtomicType.BOOL, Cardinality.One),
				new SequenceType(AtomicType.STR, Cardinality.One))));

		Functions.predefine(new Deploy(new QNm(Namespaces.APP_NSURI,
				Namespaces.APP_PREFIX, "deploy"), new Signature(
				new SequenceType(AtomicType.BOOL, Cardinality.One),
				new SequenceType(AtomicType.STR, Cardinality.One))));

		Functions.predefine(new GetStructure(new QNm(Namespaces.APP_NSURI,
				Namespaces.APP_PREFIX, "get-structure"), new Signature(
				new SequenceType(AnyItemType.ANY, Cardinality.ZeroOrOne),
				new SequenceType(AtomicType.STR, Cardinality.One))));

		Functions.predefine(new Generate(new QNm(Namespaces.APP_NSURI,
				Namespaces.APP_PREFIX, "generate"), new Signature(
				new SequenceType(AtomicType.BOOL, Cardinality.One),
				new SequenceType(AtomicType.STR, Cardinality.One),
				new SequenceType(AtomicType.STR, Cardinality.One))));

		Functions.predefine(new Exists(new QNm(Namespaces.APP_NSURI,
				Namespaces.APP_PREFIX, "exist"), new Signature(
				new SequenceType(AtomicType.BOOL, Cardinality.One),
				new SequenceType(AtomicType.STR, Cardinality.One))));

		// XQFile
		Functions.predefine(new CompileXQFile(new QNm(Namespaces.XQFILE_NSURI,
				Namespaces.XQFILE_PREFIX, "compile"), new Signature(
				new SequenceType(AtomicType.BOOL, Cardinality.One),
				new SequenceType(AtomicType.STR, Cardinality.One),
				new SequenceType(AtomicType.STR, Cardinality.One))));

		Functions.predefine(new CreateXQFile(new QNm(Namespaces.XQFILE_NSURI,
				Namespaces.XQFILE_PREFIX, "create"), new Signature(
				new SequenceType(AtomicType.BOOL, Cardinality.One),
				new SequenceType(AtomicType.STR, Cardinality.One))));

		Functions.predefine(new DeleteXQFile(new QNm(Namespaces.XQFILE_NSURI,
				Namespaces.XQFILE_PREFIX, "delete"), new Signature(
				new SequenceType(AtomicType.BOOL, Cardinality.One),
				new SequenceType(AtomicType.STR, Cardinality.One))));

		Functions.predefine(new SaveXQFile(new QNm(Namespaces.XQFILE_NSURI,
				Namespaces.XQFILE_PREFIX, "save"), new Signature(
				new SequenceType(AtomicType.BOOL, Cardinality.One),
				new SequenceType(AtomicType.STR, Cardinality.One),
				new SequenceType(AtomicType.STR, Cardinality.One))));

		Functions.predefine(new IsLibrary(new QNm(Namespaces.XQFILE_NSURI,
				Namespaces.XQFILE_PREFIX, "is-library"), new Signature(
				new SequenceType(AtomicType.BOOL, Cardinality.One),
				new SequenceType(AtomicType.STR, Cardinality.One))));

		Functions.predefine(new GetCompilationResult(new QNm(
				Namespaces.XQFILE_NSURI, Namespaces.XQFILE_PREFIX,
				"get-compilation-error"), new Signature(new SequenceType(
				AtomicType.STR, Cardinality.One), new SequenceType(
				AtomicType.STR, Cardinality.One))));

		// Resources handling
		Functions.predefine(new Upload(new QNm(Namespaces.RESOURCE_NSURI,
				Namespaces.RESOURCE_PREFIX, "upload"), new Signature(
				new SequenceType(AtomicType.BOOL, Cardinality.One),
				new SequenceType(AtomicType.STR, Cardinality.One),
				new SequenceType(AtomicType.STR, Cardinality.One))));

		Functions.predefine(new DeleteResource(
				new QNm(Namespaces.RESOURCE_NSURI, Namespaces.RESOURCE_PREFIX,
						"delete"), new Signature(new SequenceType(
						AtomicType.BOOL, Cardinality.One), new SequenceType(
						AtomicType.STR, Cardinality.One))));

		Functions.predefine(new RenameResource(
				new QNm(Namespaces.RESOURCE_NSURI, Namespaces.RESOURCE_PREFIX,
						"rename"), new Signature(new SequenceType(
						AtomicType.BOOL, Cardinality.One), new SequenceType(
						AtomicType.STR, Cardinality.One), new SequenceType(
						AtomicType.STR, Cardinality.One))));

		// HTTP handling
		Functions.predefine(new SendRequest(new QNm(Namespaces.HTTP_NSURI,
				Namespaces.HTTP_PREFIX, "send-request"), new Signature(
				new SequenceType(AnyItemType.ANY, Cardinality.One),
				new SequenceType(AnyItemType.ANY, Cardinality.One))));

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
		super(getStringFromInputStream(in));
		this.longLive = false;
	}

	public ASXQuery(String s) throws QueryException {
		super(s);
		this.longLive = false;
	}

	public ASXQuery(File f) throws QueryException {
		super(getStringFromFile(f));
		this.longLive = false;
	}

	public ASXQuery(CompileChain chain, File f) throws QueryException {
		super(chain, getStringFromFile(f));
		this.longLive = false;
	}

	public ASXQuery(CompileChain chain, InputStream in) throws QueryException {
		super(chain, getStringFromInputStream(in));
		this.longLive = false;
	}

	public ASXQuery(CompileChain chain, String s) throws QueryException {
		super(chain, s);
		this.longLive = false;
	}

	public static String getStringFromFile(File pFile) throws QueryException {
		byte[] buffer = new byte[(int) pFile.length()];
		BufferedInputStream in = null;
		try {
			in = new BufferedInputStream(new FileInputStream(pFile));
			in.read(buffer);
		} catch (IOException e) {
			throw new QueryException(e, ErrorCode.ERR_PARSING_ERROR, e
					.getMessage());
		} finally {
			if (in != null)
				try {
					in.close();
				} catch (IOException ignored) {
				}
		}
		return new String(buffer);
	}

	private static String getStringFromInputStream(InputStream in)
			throws QueryException {

		StringBuffer out = new StringBuffer();
		byte[] b = new byte[4096];
		try {
			for (int n; (n = in.read(b)) != -1;) {
				out.append(new String(b, 0, n));
			}
		} catch (IOException e) {
			throw new QueryException(e, ErrorCode.ERR_PARSING_ERROR, e
					.getMessage());
		} finally {
			if (in != null)
				try {
					in.close();
				} catch (IOException ignored) {
				}
		}

		return out.toString();
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
