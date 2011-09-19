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
package org.brackit.as.xquery;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.io.PrintWriter;

import org.brackit.as.xquery.function.bit.AddDocToCollection;
import org.brackit.as.xquery.function.bit.CreateCollection;
import org.brackit.as.xquery.function.bit.DropCollection;
import org.brackit.as.xquery.function.bit.Eval;
import org.brackit.as.xquery.function.bit.ExistCollection;
import org.brackit.as.xquery.function.bit.FtIndexStore;
import org.brackit.as.xquery.function.bit.LoadFile;
import org.brackit.as.xquery.function.bit.MakeDirectory;
import org.brackit.as.xquery.function.bit.Render;
import org.brackit.as.xquery.function.bit.StoreDoc;
import org.brackit.as.xquery.function.session.Clear;
import org.brackit.as.xquery.function.session.GetAttributeNames;
import org.brackit.as.xquery.function.session.GetCreationTime;
import org.brackit.as.xquery.function.session.GetLastAccessedTime;
import org.brackit.as.xquery.function.session.GetMaxInactiveInterval;
import org.brackit.as.xquery.function.session.GetSessionAtt;
import org.brackit.as.xquery.function.session.Invalidate;
import org.brackit.as.xquery.function.session.RemoveSessionAtt;
import org.brackit.as.xquery.function.session.SetMaxInactiveInterval;
import org.brackit.as.xquery.function.session.SetSessionAtt;
import org.brackit.as.xquery.function.util.Template;
import org.brackit.as.xquery.node.WebSubtreePrinter;
import org.brackit.xquery.ErrorCode;
import org.brackit.xquery.QueryException;
import org.brackit.xquery.XQuery;
import org.brackit.xquery.atomic.QNm;
import org.brackit.xquery.compiler.CompileChain;
import org.brackit.xquery.function.Signature;
import org.brackit.xquery.module.Functions;
import org.brackit.xquery.module.Namespaces;
import org.brackit.xquery.node.SubtreePrinter;
import org.brackit.xquery.sequence.type.AnyItemType;
import org.brackit.xquery.sequence.type.AtomicType;
import org.brackit.xquery.sequence.type.Cardinality;
import org.brackit.xquery.sequence.type.SequenceType;
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
		// Bit
		Functions.predefine(new DropCollection(new QNm(Namespaces.BIT_NSURI,
				Namespaces.BIT_PREFIX, "dropCollection"), new Signature(
				new SequenceType(AtomicType.BOOL, Cardinality.One),
				new SequenceType(AtomicType.STR, Cardinality.One))));

		Functions.predefine(new Eval(new QNm(Namespaces.BIT_NSURI,
				Namespaces.BIT_PREFIX, "eval"), new Signature(new SequenceType(
				AtomicType.STR, Cardinality.ZeroOrOne), new SequenceType(
				AtomicType.STR, Cardinality.One))));

		Functions.predefine(new LoadFile(new QNm(Namespaces.BIT_NSURI,
				Namespaces.BIT_PREFIX, "loadFile"), new Signature(
				new SequenceType(AtomicType.STR, Cardinality.ZeroOrOne),
				new SequenceType(AtomicType.STR, Cardinality.One))));

		Functions.predefine(new MakeDirectory(new QNm(Namespaces.BIT_NSURI,
				Namespaces.BIT_PREFIX, "makeDirectory"), new Signature(
				new SequenceType(AtomicType.STR, Cardinality.One),
				new SequenceType(AtomicType.STR, Cardinality.One))));

		Functions.predefine(new StoreDoc(new QNm(Namespaces.BIT_NSURI,
				Namespaces.BIT_PREFIX, "storeDoc"), new Signature(
				new SequenceType(AtomicType.STR, Cardinality.ZeroOrOne),
				new SequenceType(AtomicType.STR, Cardinality.One),
				new SequenceType(AnyItemType.ANY, Cardinality.One))));

		Functions.predefine(new FtIndexStore(new QNm(Namespaces.BIT_NSURI,
				Namespaces.BIT_PREFIX, "ftIndexStore"), new Signature(
				new SequenceType(AtomicType.BOOL, Cardinality.One),
				new SequenceType(AtomicType.STR, Cardinality.One),
				new SequenceType(AnyItemType.ANY, Cardinality.One))));

		Functions.predefine(new AddDocToCollection(new QNm(
				Namespaces.BIT_NSURI, Namespaces.BIT_PREFIX,
				"addDocToCollection"), new Signature(new SequenceType(
				AtomicType.STR, Cardinality.ZeroOrOne), new SequenceType(
				AtomicType.STR, Cardinality.One), new SequenceType(
				AnyItemType.ANY, Cardinality.One))));

		Functions.predefine(new CreateCollection(new QNm(Namespaces.BIT_NSURI,
				Namespaces.BIT_PREFIX, "createCollection"), new Signature(
				new SequenceType(AtomicType.BOOL, Cardinality.One),
				new SequenceType(AtomicType.STR, Cardinality.One))));

		Functions.predefine(new ExistCollection(new QNm(Namespaces.BIT_NSURI,
				Namespaces.BIT_PREFIX, "existCollection"), new Signature(
				new SequenceType(AtomicType.BOOL, Cardinality.One),
				new SequenceType(AtomicType.STR, Cardinality.One))));

		// SESSION
		Functions.predefine(new Clear(new QNm(Namespaces.BIT_NSURI,
				Namespaces.SESSION_PREFIX, "clear"), new Signature(
				new SequenceType(AtomicType.BOOL, Cardinality.One))));

		Functions.predefine(new GetAttributeNames(new QNm(Namespaces.BIT_NSURI,
				Namespaces.SESSION_PREFIX, "getAttributeNames"), new Signature(
				new SequenceType(AnyItemType.ANY, Cardinality.ZeroOrMany))));

		Functions.predefine(new GetCreationTime(new QNm(Namespaces.BIT_NSURI,
				Namespaces.SESSION_PREFIX, "getCreationTime"), new Signature(
				new SequenceType(AtomicType.DATE, Cardinality.ZeroOrOne))));

		Functions.predefine(new GetLastAccessedTime(new QNm(
				Namespaces.BIT_NSURI, Namespaces.SESSION_PREFIX,
				"getLastAccessedTime"), new Signature(new SequenceType(
				AtomicType.DATE, Cardinality.ZeroOrOne))));

		Functions.predefine(new GetMaxInactiveInterval(new QNm(
				Namespaces.BIT_NSURI, Namespaces.SESSION_PREFIX,
				"getMaxInactiveInterval"), new Signature(new SequenceType(
				AtomicType.INT, Cardinality.ZeroOrOne))));

		Functions.predefine(new GetSessionAtt(new QNm(Namespaces.BIT_NSURI,
				Namespaces.SESSION_PREFIX, "getAtt"), new Signature(
				new SequenceType(AnyItemType.ANY, Cardinality.One),
				new SequenceType(AtomicType.STR, Cardinality.One))));

		Functions.predefine(new Invalidate(new QNm(Namespaces.BIT_NSURI,
				Namespaces.SESSION_PREFIX, "invalidate"), new Signature(
				new SequenceType(AtomicType.BOOL, Cardinality.One))));

		Functions.predefine(new RemoveSessionAtt(new QNm(Namespaces.BIT_NSURI,
				Namespaces.SESSION_PREFIX, "rmAtt"), new Signature(
				new SequenceType(AnyItemType.ANY, Cardinality.One),
				new SequenceType(AtomicType.STR, Cardinality.One))));

		Functions.predefine(new SetMaxInactiveInterval(new QNm(
				Namespaces.BIT_NSURI, Namespaces.SESSION_PREFIX,
				"setMaxInactiveInterval"), new Signature(new SequenceType(
				AtomicType.BOOL, Cardinality.One), new SequenceType(
				AtomicType.INT, Cardinality.One))));

		Functions.predefine(new SetSessionAtt(new QNm(Namespaces.BIT_NSURI,
				Namespaces.SESSION_PREFIX, "setAtt"), new Signature(
				new SequenceType(AtomicType.BOOL, Cardinality.One),
				new SequenceType(AtomicType.STR, Cardinality.One),
				new SequenceType(AnyItemType.ANY, Cardinality.One))));

		// Util
		Functions.predefine(new Template(new QNm(Namespaces.BIT_NSURI,
				Namespaces.UTIL_PREFIX, "template"), new Signature(
				new SequenceType(AnyItemType.ANY, Cardinality.One),
				new SequenceType(AnyItemType.ANY, Cardinality.ZeroOrOne),
				new SequenceType(AnyItemType.ANY, Cardinality.ZeroOrOne),
				new SequenceType(AnyItemType.ANY, Cardinality.ZeroOrOne),
				new SequenceType(AnyItemType.ANY, Cardinality.ZeroOrOne),
				new SequenceType(AnyItemType.ANY, Cardinality.ZeroOrOne))));

		Functions.predefine(new Template(new QNm(Namespaces.BIT_NSURI,
				Namespaces.UTIL_PREFIX, "template"), new Signature(
				new SequenceType(AnyItemType.ANY, Cardinality.One),
				new SequenceType(AnyItemType.ANY, Cardinality.One))));

		// Testing
		Functions.predefine(new Render(new QNm(Namespaces.BIT_NSURI,
				Namespaces.BIT_PREFIX, "render"), new Signature(
				new SequenceType(AnyItemType.ANY, Cardinality.One),
				new SequenceType(AtomicType.STR, Cardinality.One))));
	}

	public ASXQuery(InputStream in) throws QueryException {
		super(getStringFromInputStream(in));
	}

	public ASXQuery(String s) throws QueryException {
		super(s);
	}

	public ASXQuery(File f) throws QueryException {
		super(getStringFromFile(f));
	}

	public ASXQuery(CompileChain chain, InputStream in) throws QueryException {
		super(chain, getStringFromInputStream(in));
	}

	public ASXQuery(CompileChain chain, String s) throws QueryException {
		super(chain, s);
	}

	private static String getStringFromFile(File pFile) throws QueryException {
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

	public String serializeWebSequence(HttpSessionTXQueryContext ctx,
			Sequence result) throws DocumentException, QueryException {

		if (result == null) {
			return "";
		}

		WebSubtreePrinter printer = new WebSubtreePrinter();
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
				}
			}
		} finally {
			it.close();
		}
		return printer.getOut().toString();
	}

	public void serializeSequence(HttpSessionTXQueryContext ctx,
			PrintStream ps, Sequence result) throws DocumentException,
			QueryException {

		PrintWriter out = new PrintWriter(ps);

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
